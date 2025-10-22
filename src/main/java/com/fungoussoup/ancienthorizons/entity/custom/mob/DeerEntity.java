package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.interfaces.Stampedeable;
import com.fungoussoup.ancienthorizons.entity.ai.StampedeGoal;
import com.fungoussoup.ancienthorizons.entity.ai.StampedeTracker;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DeerEntity extends Animal implements Stampedeable {
    private static final EntityDataAccessor<Boolean> DATA_IS_STAMPEDING =
            SynchedEntityData.defineId(DeerEntity.class, EntityDataSerializers.BOOLEAN);
    private int eatAnimationTick;
    private EatBlockGoal eatBlockGoal;

    private static final int EAT_ANIMATION_TICKS = 40;

    private int alertTimer = 0;
    private static final int ALERT_DURATION = 60; // 3 seconds

    // Antler growth for males (visual variants could be added)
    private boolean hasAntlers = random.nextBoolean();

    public DeerEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_STAMPEDING, false);
    }

    @Override
    protected void registerGoals() {
        this.eatBlockGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StampedeGoal(this, 1.6D, 16));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, this::isFood, true));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(6, this.eatBlockGoal);
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.JUMP_STRENGTH, 0.5D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Handle alert state
            if (alertTimer > 0) {
                alertTimer--;
            }
        }
    }

    protected void customServerAiStep() {
        this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
        }
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.9f;
    }

    public float getHeadEatPositionScale(float partialTick) {
        if (this.eatAnimationTick <= 0) {
            return 0.0F;
        } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
            return 1.0F;
        } else {
            return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - partialTick) / 4.0F : -((float)(this.eatAnimationTick - 40) - partialTick) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float partialTick) {
        if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
            float f = ((float)(this.eatAnimationTick - 4) - partialTick) / 32.0F;
            return ((float)Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else {
            return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.getXRot() * ((float)Math.PI / 180F);
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.COW_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        DeerEntity baby = ModEntities.DEER.get().create(serverLevel);
        if (baby != null) {
            // 50% chance to inherit antlers from parents
            if (this.hasAntlers || (ageableMob instanceof DeerEntity deer && deer.hasAntlers)) {
                baby.hasAntlers = serverLevel.random.nextBoolean();
            }
        }
        return baby;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        boolean wasHurt = super.hurt(damageSource, amount);

        if (wasHurt && !this.level().isClientSide) {
            this.alertTimer = ALERT_DURATION;

            if (damageSource.getEntity() instanceof LivingEntity attacker) {
                // Track the hit for stampede behavior
                StampedeTracker.getInstance().onAnimalHit(attacker, this);
            }
        }

        return wasHurt;
    }

    @Override
    public boolean isStampeding() {
        return this.entityData.get(DATA_IS_STAMPEDING);
    }

    @Override
    public void setStampeding(boolean stampeding) {
        this.entityData.set(DATA_IS_STAMPEDING, stampeding);
    }

    @Override
    public void triggerStampede() {
        this.setStampeding(true);
        this.alertTimer = ALERT_DURATION;
    }

    // Sound effects
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? ModSoundEvents.FAWN_AMBIENT : ModSoundEvents.DEER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.DEER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.DEER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    // Make deer naturally cautious
    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity target) {
        return false; // Deer don't attack
    }

    // Enhanced fall damage reduction (deer are agile)
    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return super.calculateFallDamage(distance, damageMultiplier * 0.5F);
    }

    // Better pathfinding over obstacles
    @Override
    public boolean canBeLeashed() {
        return false; // Wild deer can't be leashed
    }

    @Override
    public float getScale() {
        // Babies are smaller
        return this.isBaby() ? 0.5F : 1.0F;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.isBaby() ? dimensions.height() * 0.8F : dimensions.height() * 0.9F;
    }

    // NBT Data persistence
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsStampeding", this.isStampeding());
        tag.putBoolean("HasAntlers", this.hasAntlers);
        tag.putInt("AlertTimer", this.alertTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setStampeding(tag.getBoolean("IsStampeding"));
        this.hasAntlers = tag.getBoolean("HasAntlers");
        this.alertTimer = tag.getInt("AlertTimer");
    }

    // Visual variant support
    public boolean hasAntlers() {
        return this.hasAntlers;
    }

    public void setHasAntlers(boolean hasAntlers) {
        this.hasAntlers = hasAntlers;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        // 50% of adult deer spawn with antlers (representing males)
        if (!this.isBaby() && level.getLevel().random.nextFloat() < 0.5F) {
            this.hasAntlers = true;
        }

        return spawnGroupData;
    }

    // Increased alertness range - deer are wary
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            this.alertTimer = ALERT_DURATION;
        }
    }

    public boolean isAlert() {
        return this.alertTimer > 0;
    }

    public int getEatAnimationTick() {
        return eatAnimationTick;
    }
}