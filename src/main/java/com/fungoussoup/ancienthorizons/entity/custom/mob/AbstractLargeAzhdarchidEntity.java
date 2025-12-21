package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingLookControl;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

import static com.fungoussoup.ancienthorizons.entity.client.large_azhdarchid.LargeAzhdarchidAnimations.*;

public abstract class AbstractLargeAzhdarchidEntity extends Animal implements SemiFlyer, NeutralMob, PlayerRideable, Saddleable {

    // Data accessors for syncing data between client and server
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLED = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_ROLL = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_TAKEOFF_COOLDOWN = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SOARING = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(AbstractLargeAzhdarchidEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    private static final float MAX_STAND_ANIMATION = 6.0F;

    // Flight mechanics
    private int flapTicks = 0;
    private int rollTicks = 0;
    private boolean isBarrelRolling = false;
    private float rollDirection = 0.0F;
    private int takeoffCooldown = 0;
    private Vec3 targetFlightVelocity = Vec3.ZERO;

    // Animations
    public AnimationState flyAnimationState = new AnimationState();
    private int flyAnimationTimeout = 0;
    public AnimationState soarAnimationState = new AnimationState();
    private int soarAnimationTimeout = 0;
    public AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;
    public AnimationState diveAnimationState = new AnimationState();
    private int diveAnimationTimeout = 0;
    public AnimationState steerAnimationState = new AnimationState();
    public int steerAnimationTimeout = 0;

    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private AnimationDefinition currentAnimation = null;

    // Navigation
    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    public AbstractLargeAzhdarchidEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SemiFlyingMoveControl(this);
        this.lookControl = new SemiFlyingLookControl(this);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.WATER, 5.0F); // Avoid water
        this.setPathfindingMalus(PathType.WATER_BORDER, 3.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_SADDLED, false);
        builder.define(DATA_ROLL, 0.0F);
        builder.define(DATA_TAKEOFF_COOLDOWN, 0);
        builder.define(DATA_SOARING, false);
        builder.define(DATA_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Keep takeoff/land goals at low priority
        this.goalSelector.addGoal(6, new AzhdarchidTakeoffGoal());
        this.goalSelector.addGoal(7, new AzhdarchidLandGoal());

        // Ground-based goals should be higher priority
        this.goalSelector.addGoal(0, new AzhdarchidAirAttackGoal(this)); // Highest priority for air attacks
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false)); // Ground attacks
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, stack -> stack.is(ModTags.Items.AZHDARCHID_FOOD), false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1D)); // Prefer to stay on land
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Animal.class, false, this::isPreyItem));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    private boolean isPreyItem(LivingEntity entity) {
        return entity.getType().is(ModTags.EntityTypes.AZHDARCHID_PREY);
    }

    @Override
    public void tick() {
        // Safety: prevent overflow
        if (this.tickCount > Integer.MAX_VALUE - 1000) {
            this.tickCount = 0;
        }

        super.tick();

        if (this.level().isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.setupAnimationStates();
            this.clientSideStandAnimationO = this.clientSideStandAnimation;
        }

        if (!this.level().isClientSide) {
            // Update takeoff cooldown
            if (this.takeoffCooldown > 0) {
                this.takeoffCooldown--;
                this.entityData.set(DATA_TAKEOFF_COOLDOWN, this.takeoffCooldown);
            }

            // Handle flight mechanics with safety
            try {
                this.updateFlightState();
            } catch (Exception e) {
                // If flight update fails, stop flying to be safe
                if (this.isFlying()) {
                    this.stopFlying();
                }
            }
        }

        // Client-side visual effects
        this.updateFlightAnimation();
        this.updateRollAnimation();
    }

    private void updateFlightState() {
        boolean wasFlying = this.isFlying();
        boolean shouldFly = this.shouldStartFlying();
        boolean shouldLand = this.shouldLand();

        if (!wasFlying && shouldFly && this.takeoffCooldown <= 0) {
            this.startFlying();
        } else if (wasFlying && shouldLand) {
            this.stopFlying();
        }

        // Update navigation based on flight state
        if (this.isFlying() != wasFlying) {
            this.navigation = this.isFlying() ? this.flyingNavigation : this.groundNavigation;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Update anger naturally
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
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

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.remainingPersistentAngerTime = time;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public boolean isAngry() {
        return this.persistentAngerTarget != null;
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    @Override
    public boolean canStandOnFluid(FluidState fluidState) {
        return false; // Cannot stand on water - will sink
    }

    public void startFlying() {
        // Require some ground speed to take off
        double groundSpeed = Math.sqrt(
                this.getDeltaMovement().x * this.getDeltaMovement().x +
                        this.getDeltaMovement().z * this.getDeltaMovement().z
        );

        if (groundSpeed < 0.1 && !this.isVehicle()) {
            // Not moving fast enough for takeoff
            return;
        }

        this.entityData.set(DATA_FLYING, true);
        this.takeoffCooldown = 200; // 10 second cooldown - longer to prevent frequent flying
        this.playSound(this.getTakeoffSound(), 1.0F, 1.0F);

        // Give initial upward velocity based on ground speed
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x * 1.2, Math.max(motion.y, 0.4 + groundSpeed * 0.5), motion.z * 1.2);
    }


    public void stopFlying() {
        this.entityData.set(DATA_FLYING, false);
        this.playSound(this.getLandSound(), 1.0F, 1.0F);
    }

    private void updateFlightAnimation() {
        if (this.isFlying()) {
            this.flapTicks++;
        }
    }

    private void updateRollAnimation() {
        if (this.isBarrelRolling) {
            this.rollTicks++;
            float rollProgress = this.rollTicks / 20.0F; // 1 second roll
            float roll = this.rollDirection * 360.0F * rollProgress;
            this.entityData.set(DATA_ROLL, roll);

            if (this.rollTicks >= 20) {
                this.isBarrelRolling = false;
                this.rollTicks = 0;
                this.entityData.set(DATA_ROLL, 0.0F);
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive()) {
            if (this.isVehicle() && this.canBeControlledByRider()) {
                this.travelWithRider(travelVector);
            } else if (this.isFlying()) {
                this.travelFlying(travelVector);
            } else {
                super.travel(travelVector);
            }
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.AZHDARCHID_FOOD);
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
        this.setSaddled(true);
        if (soundSource != null) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.HORSE_SADDLE, soundSource, 0.5F, 1.0F);
        }
    }

    private void travelWithRider(Vec3 travelVector) {
        LivingEntity rider = this.getControllingPassenger();
        if (rider instanceof Player player) {
            // Handle rider input
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(rider.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float forward = player.zza;
            float strafe = player.xxa;

            if (this.isFlying()) {
                this.handleFlightMovement(forward, strafe, player);
            } else {
                // Ground movement
                if (forward != 0.0F || strafe != 0.0F) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.75F);
                    super.travel(new Vec3(strafe, travelVector.y, forward));
                }
            }
        }
    }

    private void handleFlightMovement(float forward, float strafe, Player player) {
        try {
            // Calculate movement direction
            Vec3 look = this.getLookAngle();
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 right = look.cross(up).normalize();

            Vec3 moveDirection = look.scale(forward).add(right.scale(strafe));

            // Handle diving and climbing
            if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
                float verticalInput = 0.0F;
                if (localPlayer.input.jumping) {
                    verticalInput = 0.3F;
                } else if (localPlayer.input.shiftKeyDown) {
                    verticalInput = -0.3F;
                }

                float pitch = -this.getXRot() * 0.017453292F;
                verticalInput += Mth.sin(pitch) * forward * 0.5F;

                moveDirection = moveDirection.add(0, verticalInput, 0);

                // Handle barrel roll with cooldown
                if (player.isSprinting() && !this.isBarrelRolling && strafe != 0.0F && this.tickCount % 20 == 0) {
                    this.startBarrelRoll(strafe > 0 ? 1.0F : -1.0F);
                }
            }

            // Apply movement with flight speed and clamp
            float speed = (float) this.getAttributeValue(Attributes.FLYING_SPEED);
            speed = Mth.clamp(speed, 0.1F, 1.5F);

            Vec3 newMovement = moveDirection.scale(speed);

            // Clamp total velocity
            double maxVelocity = 2.0;
            if (newMovement.lengthSqr() > maxVelocity * maxVelocity) {
                newMovement = newMovement.normalize().scale(maxVelocity);
            }

            this.setDeltaMovement(newMovement);

            // Flap wings for movement
            if (moveDirection.lengthSqr() > 0.01) {
                this.playFlapSound();
            }
        } catch (Exception e) {
            // If flight movement fails, stabilize
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        }
    }

    private void travelFlying(Vec3 travelVector) {
        // AI flight behavior
        if (this.getDeltaMovement().lengthSqr() < 0.1) {
            // Maintain minimum flight speed
            Vec3 forward = this.getLookAngle();
            this.setDeltaMovement(forward.scale(0.2));
        }

        // Apply gravity reduction while flying
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y * 0.95 - 0.02, motion.z);

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void startBarrelRoll(float direction) {
        this.isBarrelRolling = true;
        this.rollDirection = direction;
        this.rollTicks = 0;
        this.playSound(SoundEvents.ELYTRA_FLYING, 0.5F, 1.5F);
    }

    private void playFlapSound() {
        if (!this.isSoaring()) {
            if (this.flapTicks % 10 == 0) { // Play every 10 ticks
                this.playSound(this.getFlapSound(), 0.5F, 1.0F);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (itemstack.is(ItemTags.WOOL_CARPETS) && !this.isSaddled()) {
            // Saddle the creature
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.setSaddled(true);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.HORSE_SADDLE, SoundSource.NEUTRAL, 0.5F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (this.isSaddled() && !this.isVehicle()) {
            // Mount the creature
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public PathNavigation getNavigation() {
        return this.isFlying() ? this.flyingNavigation : this.groundNavigation;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return !this.isFlying() && super.causeFallDamage(fallDistance, damageMultiplier, damageSource);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.isFlying()) {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof Player;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().get(0);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    public void equipSaddle(@Nullable SoundSource soundSource) {
        this.setSaddled(true);
        if (soundSource != null) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.HORSE_SADDLE, soundSource, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(DATA_SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(DATA_SADDLED, saddled);
    }

    // SemiFlyer implementation
    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    @Override
    public void setFlying(boolean flying) {
        boolean currently = isFlying();
        if (currently == flying) return;

        this.entityData.set(DATA_FLYING, flying);

        try {
            if (flying) {
                this.navigation = this.flyingNavigation;
                this.moveControl = new SemiFlyingMoveControl(this);
                this.lookControl = new SemiFlyingLookControl(this);
                this.setNoGravity(true);
                if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
                }
            } else {
                this.navigation = this.groundNavigation;
                this.moveControl = new MoveControl(this);
                this.setNoGravity(false);
                if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
                }
            }
        } catch (Exception e) {
            // Revert state if something goes wrong
            this.entityData.set(DATA_FLYING, currently);
        }
    }

    public float getRoll() {
        return this.entityData.get(DATA_ROLL);
    }

    public int getFlapTicks() {
        return this.flapTicks;
    }

    public boolean isBarrelRolling() {
        return this.isBarrelRolling;
    }

    protected SoundEvent getTakeoffSound(){
        return SoundEvents.BAT_TAKEOFF;
    };
    protected SoundEvent getLandSound(){
        return SoundEvents.GENERIC_SMALL_FALL;
    };
    protected SoundEvent getFlapSound(){
        return SoundEvents.ENDER_DRAGON_FLAP;
    };

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putInt("TakeoffCooldown", this.takeoffCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.takeoffCooldown = compound.getInt("TakeoffCooldown");
    }

    public boolean isSoaring() {
        return this.entityData.get(DATA_SOARING);
    }

    public void setSoaring(boolean soaring) {
        this.entityData.set(DATA_SOARING, soaring);
    }

    public void setAnimation(@Nullable AnimationDefinition anim) {
        this.currentAnimation = anim;

        // Stop all animation states first
        this.flyAnimationState.stop();
        this.diveAnimationState.stop();
        this.attackAnimationState.stop();
        this.soarAnimationState.stop();
        this.steerAnimationState.stop();

        // Start the appropriate animation state
        if (anim == AZHDARCHID_SOAR) {
            this.soarAnimationState.start(this.tickCount);
        } else if (anim == AZHDARCHID_DIVE) {
            this.diveAnimationState.start(this.tickCount);
        } else if (anim == AZHDARCHID_ATTACK || anim == AZHDARCHID_PECK_AIR || anim == AZHDARCHID_PECK_LAND) {
            this.attackAnimationState.start(this.tickCount);
        } else if (anim == AZHDARCHID_FLY) {
            this.flyAnimationState.start(this.tickCount);
        } else if (anim == AZHDARCHID_RSTEER || anim == AZHDARCHID_LSTEER) {
            this.steerAnimationState.start(this.tickCount);
        }
    }

    public void setupAnimationStates() {
        this.clientSideStandAnimation = Math.min(this.clientSideStandAnimation + 0.1F, MAX_STAND_ANIMATION);

        // Handle soar animation
        if (this.isSoaring() && this.soarAnimationTimeout <= 0) {
            this.soarAnimationState.start(this.tickCount);
            this.soarAnimationTimeout = 40; // Reset timeout
        } else {
            --this.soarAnimationTimeout;
        }

        // Handle fly animation
        if (this.isFlying() && !this.isSoaring() && this.flyAnimationTimeout <= 0) {
            this.flyAnimationState.start(this.tickCount);
            this.flyAnimationTimeout = 40; // Reset timeout
        } else {
            --this.flyAnimationTimeout;
        }

        // Handle dive animation
        if ((this.isBarrelRolling() || (this.isFlying() && this.getLookAngle().y < -0.5)) && this.diveAnimationTimeout <= 0) {
            this.diveAnimationState.start(this.tickCount);
            this.diveAnimationTimeout = 40; // Reset timeout
        } else {
            --this.diveAnimationTimeout;
        }

        // Update animation states
        if (!this.isSoaring()) {
            this.soarAnimationState.stop();
        }

        if (!this.isFlying() || this.isSoaring()) {
            this.flyAnimationState.stop();
        }

        if (!this.isBarrelRolling() && !(this.isFlying() && this.getLookAngle().y < -0.5)) {
            this.diveAnimationState.stop();
        }
    }

    // Required method for animation system
    public AnimationState getAnimationState() {
        if (this.isSoaring()) {
            return this.soarAnimationState;
        } else if (this.isBarrelRolling() || (this.isFlying() && this.getLookAngle().y < -0.5)) {
            return this.diveAnimationState;
        } else if (this.isFlying()) {
            return this.flyAnimationState;
        } else {
            return new AnimationState(); // Default/idle state
        }
    }

    public AnimationDefinition getCurrentAnimation() {
        return currentAnimation;
    }

    public int getFlyAnimationTimeout() {
        return flyAnimationTimeout;
    }

    public void setFlyAnimationTimeout(int flyAnimationTimeout) {
        this.flyAnimationTimeout = flyAnimationTimeout;
    }

    public int getSoarAnimationTimeout() {
        return soarAnimationTimeout;
    }

    public void setSoarAnimationTimeout(int soarAnimationTimeout) {
        this.soarAnimationTimeout = soarAnimationTimeout;
    }

    public int getDiveAnimationTimeout() {
        return diveAnimationTimeout;
    }

    public void setDiveAnimationTimeout(int diveAnimationTimeout) {
        this.diveAnimationTimeout = diveAnimationTimeout;
    }

    public int getSteerAnimationTimeout() {
        return steerAnimationTimeout;
    }

    public void setSteerAnimationTimeout(int steerAnimationTimeout) {
        this.steerAnimationTimeout = steerAnimationTimeout;
    }

    public AnimationState getAttackAnimationState() {
        return attackAnimationState;
    }

    public int getAttackAnimationTimeout() {
        return attackAnimationTimeout;
    }

    public void setAttackAnimationTimeout(int attackAnimationTimeout) {
        this.attackAnimationTimeout = attackAnimationTimeout;
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    private void setAttacking(boolean attacking){
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    class AzhdarchidTakeoffGoal extends Goal {
        private BlockPos targetPos;

        public AzhdarchidTakeoffGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (AbstractLargeAzhdarchidEntity.this.isFlying()) return false;
            if (AbstractLargeAzhdarchidEntity.this.takeoffCooldown > 0) return false;

            LivingEntity target = AbstractLargeAzhdarchidEntity.this.getTarget();

            // Only fly if target exists and is FAR away
            if (target != null) {
                double distanceSq = AbstractLargeAzhdarchidEntity.this.distanceToSqr(target);
                // Must be at least 30 blocks away
                if (distanceSq > 900) {
                    targetPos = target.blockPosition();
                    // Check if we can't reach via ground
                    return !AbstractLargeAzhdarchidEntity.this.hasGroundPath(targetPos);
                }
            }

            return false;
        }

        @Override
        public void start() {
            AbstractLargeAzhdarchidEntity.this.startFlying();
        }

        @Override
        public boolean canContinueToUse() {
            if (!AbstractLargeAzhdarchidEntity.this.isFlying()) return false;

            LivingEntity target = AbstractLargeAzhdarchidEntity.this.getTarget();
            if (target == null) return false;

            // Stop if we're now close enough
            return AbstractLargeAzhdarchidEntity.this.distanceToSqr(target) > 64;
        }
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

    class AzhdarchidLandGoal extends Goal {
        public AzhdarchidLandGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!AbstractLargeAzhdarchidEntity.this.isFlying()) return false;

            // Land if on ground
            if (AbstractLargeAzhdarchidEntity.this.onGround()) return true;

            // Land if no target
            LivingEntity target = AbstractLargeAzhdarchidEntity.this.getTarget();
            if (target == null) {
                return AbstractLargeAzhdarchidEntity.this.distanceToGround() < 5;
            }

            // Land if target is now close
            double distanceSq = AbstractLargeAzhdarchidEntity.this.distanceToSqr(target);
            return distanceSq < 64 && AbstractLargeAzhdarchidEntity.this.distanceToGround() < 8;
        }

        @Override
        public void start() {
            AbstractLargeAzhdarchidEntity.this.stopFlying();
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // Implement in concrete classes
        return null;
    }

    private boolean hasGroundPath(BlockPos target) {
        try {
            if (!this.level().isLoaded(target)) {
                return false;
            }

            Path path = this.groundNavigation.createPath(target, 1);
            return path != null && path.canReach();
        } catch (Exception e) {
            return false; // Assume no path if error occurs
        }
    }

    static class AzhdarchidAirAttackGoal extends Goal {
        private final AbstractLargeAzhdarchidEntity azhdarchid;
        private int diveTimer = 0;
        private Vec3 diveTarget = null;
        private static final int DIVE_DURATION = 30; // 1.5 seconds

        public AzhdarchidAirAttackGoal(AbstractLargeAzhdarchidEntity azhdarchid) {
            this.azhdarchid = azhdarchid;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.azhdarchid.getTarget();

            // Must be flying and have a valid target
            if (!this.azhdarchid.isFlying() || target == null) return false;

            double distanceSq = this.azhdarchid.distanceToSqr(target);
            // Attack only if target is within 8–40 blocks
            if (distanceSq < 64 || distanceSq > 1600) return false;

            // 1 in 50 random chance to dive
            return this.azhdarchid.random.nextInt(50) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.diveTimer > 0) return true;

            LivingEntity target = this.azhdarchid.getTarget();
            if (target == null || !target.isAlive()) return false;

            double distanceSq = this.azhdarchid.distanceToSqr(target);
            return this.azhdarchid.isFlying() && distanceSq < 1600;
        }

        @Override
        public void start() {
            LivingEntity target = this.azhdarchid.getTarget();
            if (target != null) {
                this.diveTarget = target.position().subtract(0, 1.0, 0); // aim just below target
                this.diveTimer = DIVE_DURATION;
                this.azhdarchid.setAttacking(true);
                this.azhdarchid.setAnimation(AZHDARCHID_DIVE);
            }
        }

        @Override
        public void stop() {
            this.diveTimer = 0;
            this.diveTarget = null;
            this.azhdarchid.setAttacking(false);
            this.azhdarchid.diveAnimationState.stop();
        }

        public void tick() {
            LivingEntity target = azhdarchid.getTarget();
            if (target == null || this.diveTarget == null) {
                this.stop();
                return;
            }

            // Always face the target
            azhdarchid.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.diveTimer > 0) {
                try {
                    // Dive phase: move rapidly toward diveTarget
                    Vec3 currentPos = azhdarchid.position();
                    Vec3 direction = this.diveTarget.subtract(currentPos).normalize();

                    double speed = azhdarchid.getAttributeValue(Attributes.FLYING_SPEED) * 2.5;
                    Vec3 motion = direction.scale(speed);

                    // Clamp velocity to prevent runaway
                    double maxVelocity = 2.0;
                    if (motion.lengthSqr() > maxVelocity * maxVelocity) {
                        motion = motion.normalize().scale(maxVelocity);
                    }

                    azhdarchid.setDeltaMovement(motion);
                    this.diveTimer--;

                    // Check for collision or proximity
                    if (currentPos.distanceToSqr(this.diveTarget) < 2.0) {
                        // Impact phase
                        this.performDiveAttack(target);
                        this.stop();
                    }
                } catch (Exception e) {
                    // If anything goes wrong during dive, abort
                    this.stop();
                }
            } else {
                // If dive ended, return to normal flight
                azhdarchid.setAnimation(AZHDARCHID_SOAR);
            }
        }

        private void performDiveAttack(LivingEntity target) {
            if (target != null && this.azhdarchid.distanceTo(target) < 4.0F) {
                float attackDamage = (float) this.azhdarchid.getAttributeValue(Attributes.ATTACK_DAMAGE);
                target.hurt(this.azhdarchid.damageSources().mobAttack(this.azhdarchid), attackDamage);
                this.azhdarchid.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
            }
        }
    }


    public MoveControl getMovementControl() {
        return this.moveControl;
    }
}