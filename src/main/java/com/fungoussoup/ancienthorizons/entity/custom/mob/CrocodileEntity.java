package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;
import com.fungoussoup.ancienthorizons.entity.interfaces.BaskingAnimal;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.fungoussoup.ancienthorizons.registry.ModTags.EntityTypes.CARNIVORES;

public class CrocodileEntity extends SemiAquaticAnimal implements BaskingAnimal, NeutralMob {
    private static final EntityDataAccessor<Boolean> IN_WATER = SynchedEntityData.defineId(CrocodileEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIME_IN_STATE = SynchedEntityData.defineId(CrocodileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(CrocodileEntity.class, EntityDataSerializers.INT);

    private static final double SPRINT_DISTANCE_THRESHOLD = 8.0D; // Sprint when target is within 8 blocks
    private static final double CHASE_DISTANCE_THRESHOLD = 16.0D; // Stop sprinting when target is beyond 16 blocks
    private int sprintCooldown = 0;
    private static final int SPRINT_COOLDOWN_TIME = 100;

    private static final int MIN_TIME_IN_WATER = 6000; // 5 minutes
    private static final int MIN_TIME_ON_LAND = 3600; // 3 minutes
    private static final int WATER_SEARCH_RANGE = 16;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int timeInCurrentState = 0;
    private int attackCooldown = 0;
    @Nullable
    private UUID persistentAngerTarget;

    public CrocodileEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IN_WATER, false);
        builder.define(TIME_IN_STATE, 0);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new SemiAquaticMoveToWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new SemiAquaticLeaveWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, false, monster -> !(monster instanceof Warden || monster instanceof Ravager || monster instanceof ElderGuardian)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, this::shouldAttackAnimal));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractFish.class, false));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private boolean shouldAttackAnimal(LivingEntity entity) {
        return isValidPrey(entity) || isAngryAt(entity);
    }

    private boolean isValidPrey(LivingEntity entity) {
        if (entity instanceof Player) {
            return false;
        }
        return entity.getType().is(ModTags.EntityTypes.CROCODILE_LAND_PREY);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            boolean inWater = this.isInWater();
            this.entityData.set(IN_WATER, inWater);

            this.timeInCurrentState++;
            this.entityData.set(TIME_IN_STATE, this.timeInCurrentState);

            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }

            if (this.sprintCooldown > 0) {
                this.sprintCooldown--;
            }

            // Update sprinting state
            this.updateSprintingState();
        }
    }

    private void updateSprintingState() {
        boolean shouldSprint = false;

        // Case 1: Chasing a target aggressively
        if (this.shouldSprintTowardsTarget()) {
            shouldSprint = true;
        }
        // Case 2: Fleeing when low on health
        else if (this.shouldSprintWhenFleeing()) {
            shouldSprint = true;
        }
        // Case 3: Rushing to water when on fire
        else if (this.shouldSprintToWater()) {
            shouldSprint = true;
        }
        // Case 4: Ambush attack from water
        else if (this.shouldAmbushSprint()) {
            shouldSprint = true;
        }

        this.setSprinting(shouldSprint);
    }

    private boolean shouldSprintTowardsTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distanceToTarget = this.distanceToSqr(target);

        // Sprint when target is within chase distance but not too close
        // (save energy when already in melee range)
        return distanceToTarget < CHASE_DISTANCE_THRESHOLD * CHASE_DISTANCE_THRESHOLD
                && distanceToTarget > 4.0D // 2 blocks
                && this.sprintCooldown == 0
                && this.hasLineOfSight(target);
    }

    private boolean shouldSprintWhenFleeing() {
        return this.getHealth() < this.getMaxHealth() * 0.3F // Below 30% health
                && this.getLastHurtByMob() != null
                && this.tickCount - this.getLastHurtByMobTimestamp() < 60; // Within last 3 seconds
    }

    private boolean shouldSprintToWater() {
        if (this.isInWater()) {
            return false;
        }

        // Sprint to water when on fire
        if (this.isOnFire()) {
            return true;
        }

        // Sprint to water when been on land too long
        if (this.timeInCurrentState > MIN_TIME_ON_LAND * 1.5) {
            BlockPos waterPos = this.findNearestWater();
            return waterPos != null;
        }

        return false;
    }

    private boolean shouldAmbushSprint() {
        if (!this.isInWater()) {
            return false;
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distanceToTarget = this.distanceToSqr(target);

        // Ambush sprint: burst of speed when close to prey in water
        return distanceToTarget < SPRINT_DISTANCE_THRESHOLD * SPRINT_DISTANCE_THRESHOLD
                && distanceToTarget > 2.0D
                && this.sprintCooldown == 0
                && this.hasLineOfSight(target)
                && !target.isInWater(); // Attacking something at water's edge
    }

    private BlockPos findNearestWater() {
        BlockPos currentPos = this.blockPosition();

        for (int x = -WATER_SEARCH_RANGE; x <= WATER_SEARCH_RANGE; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -WATER_SEARCH_RANGE; z <= WATER_SEARCH_RANGE; z++) {
                    BlockPos checkPos = currentPos.offset(x, y, z);
                    if (this.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                        return checkPos;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    // ========== SemiAquaticAnimal Implementation ==========

    @Override
    public boolean shouldEnterWater() {
        return !this.isInWater() &&
                (this.timeInCurrentState > MIN_TIME_ON_LAND || this.isOnFire());
    }

    @Override
    public boolean shouldLeaveWater() {
        return this.isInWater() &&
                this.timeInCurrentState > MIN_TIME_IN_WATER &&
                this.getLastHurtByMob() == null;
    }

    @Override
    public boolean shouldStopMoving() {
        return this.isBasking() || (this.isInWater() && this.getRandom().nextInt(100) < 2);
    }

    @Override
    public int getWaterSearchRange() {
        return WATER_SEARCH_RANGE;
    }

    @Override
    protected void spawnSprintParticle() {
        super.spawnSprintParticle();
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    public void setSprinting(boolean sprinting) {
        // Start sprint cooldown when stopping sprint
        if (!sprinting && this.isSprinting()) {
            this.sprintCooldown = SPRINT_COOLDOWN_TIME;
        }
        super.setSprinting(sprinting);
    }


    // ========== BaskingAnimal Implementation ==========

    public boolean isBasking() {
        return !this.isInWater() &&
                this.level().isDay() &&
                this.getDeltaMovement().horizontalDistanceSqr() < 0.01D &&
                this.level().canSeeSky(this.blockPosition());
    }

    // ========== NeutralMob Implementation ==========

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    // ========== Movement & Navigation ==========

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            // Faster movement in water when sprinting (ambush attack)
            float movementMultiplier = this.isSprinting() ? 0.15F : 0.1F;
            this.moveRelative(movementMultiplier, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    // ========== Combat ==========

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.attackCooldown > 0) {
            return false;
        }

        boolean success = super.doHurtTarget(target);
        if (success) {
            this.attackCooldown = 40; // 2 second cooldown

            // Death roll effect - extra damage in water
            if (this.isInWater() && target instanceof LivingEntity) {
                target.hurt(this.damageSources().mobAttack(this), 4.0F);
            }
        }
        return success;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity attacker = source.getEntity();
            if (attacker instanceof Player && !((Player) attacker).getAbilities().instabuild) {
                this.becomeAngryAt(attacker);
            }
            return super.hurt(source, amount);
        }
    }

    private void becomeAngryAt(Entity entity) {
        if (entity instanceof LivingEntity) {
            this.setTarget((LivingEntity) entity);
            this.startPersistentAngerTimer();
        }
    }

    // ========== Data Persistence ==========

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TimeInState", this.timeInCurrentState);
        compound.putBoolean("InWater", this.entityData.get(IN_WATER));
        compound.putInt("SprintCooldown", this.sprintCooldown);
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.timeInCurrentState = compound.getInt("TimeInState");
        this.entityData.set(IN_WATER, compound.getBoolean("InWater"));
        this.sprintCooldown = compound.getInt("SprintCooldown");
        this.readPersistentAngerSaveData(this.level(), compound);
    }

    // ========== Sounds ==========

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.CROC_AMBIENT; // Replace with custom sound
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.CROC_HURT; // Replace with custom sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.CROC_DEATH; // Replace with custom sound
    }

    // ========== Breeding ==========

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.CROC_FOOD); // Add your food items here (e.g., raw fish)
    }

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.CROCODILE.get().create(serverLevel); // Implement breeding if needed
    }
}