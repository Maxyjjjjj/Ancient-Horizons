package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RuffEntity extends Animal {

    // Data accessors for syncing data between client and server
    private static final EntityDataAccessor<Integer> BREEDING_ROLE =
            SynchedEntityData.defineId(RuffEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_DISPLAYING =
            SynchedEntityData.defineId(RuffEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DISPLAY_TIME =
            SynchedEntityData.defineId(RuffEntity.class, EntityDataSerializers.INT);

    private int breedingSeasonTimer = 0;
    private int territoryRadius = 16;
    private BlockPos lekCenter; // Communal breeding ground

    public RuffEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public int getTerritoryRadius() {
        return territoryRadius;
    }

    public void setTerritoryRadius(int territoryRadius) {
        this.territoryRadius = territoryRadius;
    }

    public enum BreedingRole {
        INDEPENDENT(0), // black plume - dominant, territorial
        SATELLITE(1),   // white plume - subordinate, sneaky mating
        FAEDER(2),      // similar to females, but actually are males - mimic females
        NON_BREEDING(3); // females and juveniles

        private final int id;

        BreedingRole(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static BreedingRole fromId(int id) {
            for (BreedingRole role : values()) {
                if (role.id == id) return role;
            }
            return NON_BREEDING;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BREEDING_ROLE, BreedingRole.NON_BREEDING.getId());
        builder.define(IS_DISPLAYING, false);
        builder.define(DISPLAY_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        // Basic AI goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D,
                (itemStack) -> itemStack.is(Items.SPIDER_EYE), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new DisplayBehaviorGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Target goals - Ruffs eat spiders and insects
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Spider.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    // Breeding role methods
    public BreedingRole getBreedingRole() {
        return BreedingRole.fromId(this.entityData.get(BREEDING_ROLE));
    }

    public void setBreedingRole(BreedingRole role) {
        this.entityData.set(BREEDING_ROLE, role.getId());
    }

    public boolean isDisplaying() {
        return this.entityData.get(IS_DISPLAYING);
    }

    public void setDisplaying(boolean displaying) {
        this.entityData.set(IS_DISPLAYING, displaying);
        if (displaying) {
            this.entityData.set(DISPLAY_TIME, 100); // Display for 5 seconds
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Handle breeding season timing
            if (!this.isBaby()) {
                breedingSeasonTimer++;

                // Spring breeding season (every 24000 ticks = 1 MC day)
                boolean isBreedingSeason = (this.level().getDayTime() % 24000) < 6000; // Morning hours

                if (isBreedingSeason && breedingSeasonTimer % 200 == 0) { // Every 10 seconds
                    updateBreedingBehavior();
                }
            }

            // Handle display behavior timer
            if (this.isDisplaying()) {
                int displayTime = this.entityData.get(DISPLAY_TIME);
                if (displayTime > 0) {
                    this.entityData.set(DISPLAY_TIME, displayTime - 1);
                } else {
                    this.setDisplaying(false);
                }
            }
        }
    }

    private void updateBreedingBehavior() {
        // Males develop breeding roles at maturity
        if (this.getBreedingRole() == BreedingRole.NON_BREEDING && !this.isBaby() && this.random.nextFloat() < 0.7f) {
            // Assign breeding role based on genetics/random chance
            float chance = this.random.nextFloat();
            if (chance < 0.15f) {
                setBreedingRole(BreedingRole.INDEPENDENT);
            } else if (chance < 0.30f) {
                setBreedingRole(BreedingRole.SATELLITE);
            } else if (chance < 0.35f) {
                setBreedingRole(BreedingRole.FAEDER);
            }
            // Remaining stay as non-breeding (females)
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.SPIDER_EYE) ||
                itemStack.is(Items.WHEAT_SEEDS) ||
                itemStack.is(Items.BEETROOT_SEEDS);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        RuffEntity baby = ModEntities.RUFF.get().create(serverLevel);
        if (baby != null) {
            baby.setBreedingRole(BreedingRole.NON_BREEDING); // All babies start non-breeding
        }
        return baby;
    }

    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData groupData = super.finalizeSpawn(level, difficulty, reason, spawnData);

        // Set initial breeding role
        if (!this.isBaby() && this.random.nextBoolean()) {
            // 50% chance to be male with breeding role
            updateBreedingBehavior();
        }

        return groupData;
    }

    // Sound methods
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT; // Placeholder - you'd want custom sounds
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    // NBT data saving/loading
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BreedingRole", this.getBreedingRole().getId());
        compound.putInt("BreedingSeasonTimer", this.breedingSeasonTimer);
        compound.putBoolean("IsDisplaying", this.isDisplaying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setBreedingRole(BreedingRole.fromId(compound.getInt("BreedingRole")));
        this.breedingSeasonTimer = compound.getInt("BreedingSeasonTimer");
        this.setDisplaying(compound.getBoolean("IsDisplaying"));
    }

    // Custom AI Goal for display behavior
    private class DisplayBehaviorGoal extends Goal {
        private int displayCooldown = 0;

        @Override
        public boolean canUse() {
            if (displayCooldown > 0) {
                displayCooldown--;
                return false;
            }

            // Only males in breeding roles display
            BreedingRole role = getBreedingRole();
            return !isDisplaying() &&
                    (role == BreedingRole.INDEPENDENT || role == BreedingRole.SATELLITE) &&
                    random.nextInt(100) < 5; // 5% chance per tick when conditions are met
        }

        @Override
        public void start() {
            setDisplaying(true);
            displayCooldown = 400; // 20 second cooldown
        }

        @Override
        public boolean canContinueToUse() {
            return isDisplaying();
        }

        @Override
        public void tick() {
            // Display animation behavior would go here
            // For now, just make the ruff jump occasionally
            if (random.nextInt(20) == 0) {
                RuffEntity.this.getJumpControl().jump();
            }
        }
    }

    // Utility methods
    public boolean isMale() {
        BreedingRole role = getBreedingRole();
        return role == BreedingRole.INDEPENDENT ||
                role == BreedingRole.SATELLITE ||
                role == BreedingRole.FAEDER;
    }

    public boolean isFemale() {
        return getBreedingRole() == BreedingRole.NON_BREEDING && !isBaby();
    }

    public float getDisplayIntensity() {
        if (!isDisplaying()) return 0.0f;
        int displayTime = this.entityData.get(DISPLAY_TIME);
        return Math.min(1.0f, displayTime / 20.0f);
    }
}