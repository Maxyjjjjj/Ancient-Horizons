package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.*;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeagullEntity extends Animal implements SemiFlyer {

    // Data Accessors
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> FLIGHT_LOOK_YAW = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AGGRESSIVE = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HUNGER_LEVEL = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CARRYING_FOOD = SynchedEntityData.defineId(SeagullEntity.class, EntityDataSerializers.BOOLEAN);

    // Custom fields
    public boolean aiItemFlag = false;
    public int stealCooldown = 0;
    public int lastStealTime = 0;
    public int aggressionLevel = 0;
    private int sitTimer = 0;
    private int hungerTimer = 0;
    private boolean isLanding = false;
    private Vec3 landingTarget = null;
    private BlockPos roostPos = null;
    private int callCooldown = 0;
    private int idleFlightTimer = 0;
    public boolean isHungry = false;

    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    public SeagullEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level, 32);
        this.moveControl = new SemiFlyingMoveControl(this, 6, 4);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
        builder.define(FLIGHT_LOOK_YAW, 0.0F);
        builder.define(ATTACK_TICK, 0);
        builder.define(SITTING, false);
        builder.define(AGGRESSIVE, false);
        builder.define(HUNGER_LEVEL, 50);
        builder.define(CARRYING_FOOD, false);
    }

    @Override
    public void startFlying() {
        if (this.isFlying()) return;
        this.setFlying(true);
        this.setNoGravity(true);
        this.navigation = this.flyingNavigation;
        this.playSound(SoundEvents.PARROT_FLY, 0.8F, 1.2F);

        // Lift off slightly
        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.5, 0));
    }

    @Override
    public void stopFlying() {
        if (!this.isFlying()) return;
        this.setFlying(false);
        this.setNoGravity(false);
        this.navigation = this.groundNavigation;
        this.isLanding = false;
    }


    private void updateFlightState() {
        boolean wasFlying = this.isFlying();
        boolean shouldFly = this.shouldStartFlying();
        boolean shouldLand = this.shouldLand();

        if (!wasFlying && shouldFly) {
            this.startFlying();
        } else if (wasFlying && shouldLand) {
            this.stopFlying();
        }

        // Update navigation based on flight state
        if (this.isFlying() != wasFlying) {
            this.navigation = this.isFlying() ? this.flyingNavigation : this.groundNavigation;
        }
    }

    private boolean shouldStartFlying() {
        // Start flying if player is riding and wants to take off, or if jumping while moving
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            return this.onGround() && this.getDeltaMovement().horizontalDistance() > 0.1;
        }

        // AI conditions for taking off
        return this.onGround() && this.getTarget() != null &&
                this.distanceToSqr(this.getTarget()) > 100 &&
                this.random.nextInt(200) == 0;
    }

    private double distanceToGround() {
        BlockPos pos = this.blockPosition();
        for (int i = 0; i < 20; i++) {
            BlockPos checkPos = pos.below(i);
            if (!this.level().getBlockState(checkPos).isAir()) {
                return i;
            }
        }
        return 20; // Max check distance
    }

    private boolean shouldLand() {
        // Always land when touching ground
        if (this.onGround()) return true;

        // Player controlled - land when shift is held and close to ground
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            return this.getDeltaMovement().y < -0.2 && this.distanceToGround() < 10;
        }

        // AI landing conditions
        LivingEntity target = this.getTarget();

        // Land if no target
        if (target == null) {
            return this.distanceToGround() < 5 || this.random.nextInt(200) == 0;
        }

        // Land if target is now close enough for ground combat
        double distanceSq = this.distanceToSqr(target);
        if (distanceSq < 64) { // Within 8 blocks
            return this.distanceToGround() < 8;
        }

        // Land if been flying too long without reaching target (prevent infinite flight)
        return this.tickCount % 1200 == 0; // Every minute, consider landing
    }


    // Getters and Setters
    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
    }

    public boolean isAggressive() {
        return this.entityData.get(AGGRESSIVE);
    }

    public void setAggressive(boolean aggressive) {
        this.entityData.set(AGGRESSIVE, aggressive);
    }

    public int getHungerLevel() {
        return this.entityData.get(HUNGER_LEVEL);
    }

    public void setHungerLevel(int level) {
        this.entityData.set(HUNGER_LEVEL, Mth.clamp(level, 0, 100));
        this.isHungry = level < 30;
    }

    public boolean isCarryingFood() {
        return this.entityData.get(CARRYING_FOOD);
    }

    public void setCarryingFood(boolean carrying) {
        this.entityData.set(CARRYING_FOOD, carrying);
    }

    public float getFlightLookYaw() {
        return this.entityData.get(FLIGHT_LOOK_YAW);
    }

    public void setFlightLookYaw(float yaw) {
        this.entityData.set(FLIGHT_LOOK_YAW, yaw);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.SEAGULL_FOOD);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.25, stack -> stack.is(ModTags.Items.SEAGULL_FOOD), true));
        this.goalSelector.addGoal(3, new SeagullStealFromPlayersGoal(this));
        this.goalSelector.addGoal(3, new SemiFlyingFlyGoal(this, 1.0F));
        this.goalSelector.addGoal(3, new SeagullAvoidEntityGoal<>(this, Player.class, 6.0F, 1.0, 1.2,
                player -> this.isAggressive() && this.aggressionLevel > 3));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, AbstractFish.class, false, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> this.isAggressive() && this.aggressionLevel > 5 && !this.isBaby()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FLYING_SPEED, 0.3);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Handle cooldowns
        if (this.stealCooldown > 0) {
            this.stealCooldown--;
        }

        if (this.callCooldown > 0) {
            this.callCooldown--;
        }

        // Handle hunger
        this.hungerTimer++;
        if (this.hungerTimer >= 6000) { // Every 5 minutes
            this.setHungerLevel(this.getHungerLevel() - 1);
            this.hungerTimer = 0;
        }

        // Handle aggression decay
        if (this.aggressionLevel > 0 && this.random.nextInt(200) == 0) {
            this.aggressionLevel--;
            if (this.aggressionLevel <= 0) {
                this.setAggressive(false);
            }
        }

        // Handle sitting
        if (this.isSitting()) {
            this.sitTimer++;
            if (this.sitTimer > 600 + this.random.nextInt(400)) { // 30-50 seconds
                this.setSitting(false);
                this.setFlying(true);
                this.sitTimer = 0;
            }
        }

        // Handle idle flight
        if (this.isFlying() && this.getTarget() == null) {
            this.idleFlightTimer++;
            if (this.idleFlightTimer > 1200) { // 1 minute
                this.tryToLand();
                this.idleFlightTimer = 0;
            }
        }

        // Handle flock calling
        if (this.isHungry && this.callCooldown <= 0 && this.random.nextInt(100) == 0) {
            this.callNearbySeagulls();
            this.callCooldown = 200 + this.random.nextInt(200);
        }

        if (this.isFlying() && this.getHungerLevel() < 10) {
            this.tryToLand();
        }

        if (!this.level().isClientSide) {
            this.updateFlightState();
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (damageSource.getEntity() instanceof Player) {
            this.aggressionLevel = Math.min(this.aggressionLevel + 2, 10);
            this.setAggressive(true);

            // Call for backup
            this.callNearbySeagulls();
        }

        this.setSitting(false);

        return super.hurt(damageSource, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isFood(itemStack) && !this.isAggressive()) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            this.setHungerLevel(this.getHungerLevel() + 10);
            this.heal(2.0F);

            if (this.aggressionLevel > 0) {
                this.aggressionLevel = Math.max(0, this.aggressionLevel - 3);
                if (this.aggressionLevel == 0) {
                    this.setAggressive(false);
                }
            }

            // Spawn particles
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        3, 0.2, 0.2, 0.2, 0.1);
            }

            this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // Custom methods
    public void steal() {
        this.lastStealTime = this.tickCount;
        this.setCarryingFood(true);
        this.setHungerLevel(this.getHungerLevel() + 5);
        this.playSound(getStealSound(), 1.0F, 1.0F);

        // Spawn particles
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    5, 0.3, 0.3, 0.3, 0.02);
        }
    }

    public Vec3 getBlockInViewAway(Vec3 from, int distance) {
        Vec3 direction = this.position().subtract(from).normalize();
        Vec3 targetPos = this.position().add(direction.scale(distance));

        // Add some randomness
        targetPos = targetPos.add(
                (this.random.nextDouble() - 0.5) * 4,
                (this.random.nextDouble() - 0.5) * 2,
                (this.random.nextDouble() - 0.5) * 4
        );

        // Ensure reasonable height
        targetPos = new Vec3(targetPos.x, Math.max(targetPos.y, this.level().getSeaLevel() + 5), targetPos.z);

        return targetPos;
    }

    private void tryToLand() {
        if (this.isFlying() && !this.isSitting() && !this.isLanding) {
            BlockPos groundPos = this.findLandingSpot();
            if (groundPos != null) {
                this.landingTarget = Vec3.atCenterOf(groundPos);
                this.isLanding = true;
            }
        }
    }

    private BlockPos findLandingSpot() {
        BlockPos currentPos = this.blockPosition();

        for (int i = 0; i < 10; i++) {
            BlockPos testPos = currentPos.offset(
                    this.random.nextInt(21) - 10,
                    -this.random.nextInt(5) - 1,
                    this.random.nextInt(21) - 10
            );

            BlockState state = this.level().getBlockState(testPos);
            BlockState above = this.level().getBlockState(testPos.above());

            if (state.isSolid() && above.isAir()) {
                return testPos.above();
            }
        }

        return null;
    }

    private void callNearbySeagulls() {
        List<SeagullEntity> nearbySeagulls = this.level().getEntitiesOfClass(
                SeagullEntity.class,
                this.getBoundingBox().inflate(20)
        );

        for (SeagullEntity seagull : nearbySeagulls) {
            if (seagull != this && seagull.getTarget() == null) {
                seagull.aggressionLevel = Math.min(seagull.aggressionLevel + 1, 5);
                if (seagull.aggressionLevel > 2) {
                    seagull.setAggressive(true);
                }
            }
        }

        this.playSound(getCallSound(), 1.0F, 1.0F);
    }

    // Sound methods
    @Override
    public SoundEvent getAmbientSound() {
        return this.isAggressive() ? getAggressiveSound() : getIdleSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return getHurtSound();
    }

    protected SoundEvent getIdleSound() {
        return SoundEvents.PARROT_AMBIENT; // Placeholder
    }

    protected SoundEvent getAggressiveSound() {
        return SoundEvents.PARROT_HURT; // Placeholder
    }

    protected SoundEvent getCallSound() {
        return SoundEvents.PARROT_IMITATE_PILLAGER; // Placeholder
    }

    protected SoundEvent getStealSound() {
        return SoundEvents.ITEM_PICKUP; // Placeholder
    }

    protected SoundEvent getHurtSound() {
        return SoundEvents.PARROT_HURT; // Placeholder
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH; // Placeholder
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Flying", this.isFlying());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putBoolean("Aggressive", this.isAggressive());
        tag.putInt("HungerLevel", this.getHungerLevel());
        tag.putInt("StealCooldown", this.stealCooldown);
        tag.putInt("AggressionLevel", this.aggressionLevel);
        tag.putBoolean("CarryingFood", this.isCarryingFood());

        if (this.roostPos != null) {
            tag.putLong("RoostPos", this.roostPos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setFlying(tag.getBoolean("Flying"));
        this.setSitting(tag.getBoolean("Sitting"));
        this.setAggressive(tag.getBoolean("Aggressive"));
        this.setHungerLevel(tag.getInt("HungerLevel"));
        this.stealCooldown = tag.getInt("StealCooldown");
        this.aggressionLevel = tag.getInt("AggressionLevel");
        this.setCarryingFood(tag.getBoolean("CarryingFood"));

        if (tag.contains("RoostPos")) {
            this.roostPos = BlockPos.of(tag.getLong("RoostPos"));
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.SEAGULL.get().create(serverLevel);
    }

    public boolean isHungry() {
        return this.isHungry;
    }
}