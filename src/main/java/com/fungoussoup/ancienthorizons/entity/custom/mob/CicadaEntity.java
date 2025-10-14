package com.fungoussoup.ancienthorizons.entity.custom.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class CicadaEntity extends Animal {
    // Cicada subspecies intervals (in Minecraft days)
    public enum CicadaType {
        BROOD_XIII(13),
        BROOD_XVII(17);

        private final int interval;

        CicadaType(int interval) {
            this.interval = interval;
        }

        public int getInterval() {
            return interval;
        }
    }

    private static final EntityDataAccessor<Integer> DATA_CICADA_TYPE =
            SynchedEntityData.defineId(CicadaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_EMERGENCE_DAY =
            SynchedEntityData.defineId(CicadaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_CALLING =
            SynchedEntityData.defineId(CicadaEntity.class, EntityDataSerializers.BOOLEAN);

    private int callingSoundTimer = 0;
    private int lifespanTicks = 0;
    private final int maxLifespan = 24000 * 7; // 7 Minecraft days above ground

    public CicadaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setEmergenceDay(getCurrentDay());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CICADA_TYPE, 0);
        builder.define(DATA_EMERGENCE_DAY, 0);
        builder.define(DATA_IS_CALLING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, (stack) -> false, false)); // Not attracted to items
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new CicadaCallingGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            lifespanTicks++;

            // Cicadas have a limited lifespan above ground
            if (lifespanTicks > maxLifespan) {
                this.hurt(this.damageSources().starve(), Float.MAX_VALUE);
            }

            // Handle calling behavior
            if (this.isCalling()) {
                callingSoundTimer++;
                if (callingSoundTimer >= 40 + this.random.nextInt(40)) { // 2-4 seconds
                    this.playSound(getCicadaCallSound(), 0.8F, 0.8F + this.random.nextFloat() * 0.4F);
                    callingSoundTimer = 0;
                }
            }
        }
    }

    public static boolean canSpawn(EntityType<CicadaEntity> entityType, LevelAccessor level,
                                   MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        // Allow spawn eggs and commands to bypass brood day logic
        if (spawnType == MobSpawnType.SPAWN_EGG || spawnType == MobSpawnType.COMMAND) {
            return pos.getY() >= 62 && level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK);
        }

        if (level instanceof ServerLevel serverLevel) {
            long currentDay = serverLevel.getDayTime() / 24000L;

            // Check if it's the right time for either brood to emerge
            for (CicadaType type : CicadaType.values()) {
                if (currentDay % type.getInterval() == 0) {
                    return pos.getY() >= 62 && level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                            && random.nextFloat() < 0.3F; // 30% chance during emergence
                }
            }

            // Very low chance outside emergence years
            return pos.getY() >= 62 && level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                    && random.nextFloat() < 0.01F; // 1% chance normally
        }

        return false;
    }


    public void finalizeSpawn(LevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                              MobSpawnType reason, @Nullable net.minecraft.world.entity.SpawnGroupData spawnData) {
        super.finalizeSpawn((ServerLevelAccessor) level, difficulty, reason, spawnData);

        if (level instanceof ServerLevel serverLevel) {
            long currentDay = serverLevel.getDayTime() / 24000L;
            CicadaType selectedType = CicadaType.BROOD_XIII; // default

            if (reason == MobSpawnType.NATURAL) {
                for (CicadaType type : CicadaType.values()) {
                    if (currentDay % type.getInterval() == 0) {
                        selectedType = type;
                        break;
                    }
                }
                this.setEmergenceDay((int) currentDay);
            } else {
                // For spawn eggs/commands, random cicada type
                CicadaType[] values = CicadaType.values();
                selectedType = values[random.nextInt(values.length)];
                this.setEmergenceDay((int) currentDay); // or just -1 if you want no emergence logic
            }

            this.setCicadaType(selectedType);
        }

    }

    // Getters and Setters
    public CicadaType getCicadaType() {
        return CicadaType.values()[this.entityData.get(DATA_CICADA_TYPE)];
    }

    public void setCicadaType(CicadaType type) {
        this.entityData.set(DATA_CICADA_TYPE, type.ordinal());
    }

    public int getEmergenceDay() {
        return this.entityData.get(DATA_EMERGENCE_DAY);
    }

    public void setEmergenceDay(int day) {
        this.entityData.set(DATA_EMERGENCE_DAY, day);
    }

    public boolean isCalling() {
        return this.entityData.get(DATA_IS_CALLING);
    }

    public void setCalling(boolean calling) {
        this.entityData.set(DATA_IS_CALLING, calling);
    }

    private int getCurrentDay() {
        if (this.level() instanceof ServerLevel serverLevel) {
            return (int) (serverLevel.getDayTime() / 24000L);
        }
        return 0;
    }

    private SoundEvent getCicadaCallSound() {
        return SoundEvents.BAT_AMBIENT; // Replace with custom cicada sound
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false; // Cicadas don't eat items
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null; // Cicadas don't breed in the traditional sense
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isCalling() ? getCicadaCallSound() : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SILVERFISH_HURT; // Placeholder
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH; // Placeholder
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CicadaType", this.getCicadaType().ordinal());
        compound.putInt("EmergenceDay", this.getEmergenceDay());
        compound.putInt("LifespanTicks", this.lifespanTicks);
        compound.putBoolean("IsCalling", this.isCalling());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("CicadaType")) {
            this.setCicadaType(CicadaType.values()[compound.getInt("CicadaType")]);
        }
        if (compound.contains("EmergenceDay")) {
            this.setEmergenceDay(compound.getInt("EmergenceDay"));
        }
        if (compound.contains("LifespanTicks")) {
            this.lifespanTicks = compound.getInt("LifespanTicks");
        }
        if (compound.contains("IsCalling")) {
            this.setCalling(compound.getBoolean("IsCalling"));
        }
    }

    // Custom AI Goal for calling behavior
    private static class CicadaCallingGoal extends Goal {
        private final CicadaEntity cicada;
        private int callTimer = 0;
        private final int callDuration = 100; // 5 seconds
        private final int restDuration = 200; // 10 seconds

        public CicadaCallingGoal(CicadaEntity cicada) {
            this.cicada = cicada;
        }

        @Override
        public boolean canUse() {
            // Call more frequently during dawn and dusk
            long timeOfDay = cicada.level().getDayTime() % 24000;
            return (timeOfDay < 2000 || timeOfDay > 22000) && cicada.getRandom().nextFloat() < 0.3F;
        }

        @Override
        public void start() {
            cicada.setCalling(true);
            callTimer = 0;
        }

        @Override
        public void tick() {
            callTimer++;
            if (callTimer > callDuration) {
                cicada.setCalling(false);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return callTimer < callDuration;
        }

        @Override
        public void stop() {
            cicada.setCalling(false);
            callTimer = 0;
        }
    }
}