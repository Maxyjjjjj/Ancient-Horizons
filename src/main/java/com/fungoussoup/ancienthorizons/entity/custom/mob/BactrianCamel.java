package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.BactrianCamelAi;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.projectile.BactrianCamelSpit;
import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;

public class BactrianCamel extends AbstractHorse implements PlayerRideableJumping, Saddleable, RangedAttackMob {
    public static final float BABY_SCALE = 0.45F;
    public static final int DASH_COOLDOWN_TICKS = 55;
    public static final int MAX_HEAD_Y_ROT = 30;
    private static final float RUNNING_SPEED_BONUS = 0.1F;
    private static final float DASH_VERTICAL_MOMENTUM = 1.4285F;
    private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222F;
    private static final int DASH_MINIMUM_DURATION_TICKS = 5;
    private static final int SITDOWN_DURATION_TICKS = 40;
    private static final int STANDUP_DURATION_TICKS = 52;
    private static final int IDLE_MINIMAL_DURATION_TICKS = 80;
    private static final float SITTING_HEIGHT_DIFFERENCE = 1.43F;
    public static final EntityDataAccessor<Boolean> DASH;
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK;
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitPoseAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
    private static final EntityDimensions SITTING_DIMENSIONS;
    private int dashCooldown = 0;
    private int idleAnimationTimeout = 0;

    public BactrianCamel(EntityType<? extends BactrianCamel> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new BactrianCamel.BactrianCamelMoveControl();
        this.lookControl = new BactrianCamel.BactrianCamelLookControl();
        GroundPathNavigation groundpathnavigation = (GroundPathNavigation)this.getNavigation();
        groundpathnavigation.setCanFloat(true);
        groundpathnavigation.setCanWalkOverFences(true);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        long i = compound.getLong("LastPoseTick");
        if (i < 0L) {
            this.setPose(Pose.SITTING);
        }

        this.resetLastPoseChangeTick(i);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.225D)
                .add(Attributes.JUMP_STRENGTH, 0.42D)
                .add(Attributes.STEP_HEIGHT, 1.5D);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DASH, false);
        builder.define(LAST_POSE_CHANGE_TICK, 0L);
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        BactrianCamelAi.initMemories(this, level.getRandom());
        this.resetLastPoseChangeTickToFullStand(level.getLevel().getGameTime());
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    protected Brain.Provider<BactrianCamel> brainProvider() {
        return BactrianCamelAi.brainProvider();
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return BactrianCamelAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    public EntityDimensions getDefaultDimensions(Pose pose) {
        return pose == Pose.SITTING ? SITTING_DIMENSIONS.scale(this.getAgeScale()) : super.getDefaultDimensions(pose);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("bactrianCamelBrain");
        Brain<BactrianCamel> brain = (Brain<BactrianCamel>) this.getBrain();
        this.level().getProfiler().pop();
        this.level().getProfiler().push("camelActivityUpdate");
        BactrianCamelAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    public void tick() {
        super.tick();
        if (this.isDashing() && this.dashCooldown < 50 && (this.onGround() || this.isInLiquid() || this.isPassenger())) {
            this.setDashing(false);
        }

        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.level().playSound((Player)null, this.blockPosition(), SoundEvents.CAMEL_DASH_READY, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (this.refuseToMove()) {
            this.clampHeadRotationToBody();
        }

        if (this.isCamelSitting() && this.isInWater()) {
            this.standUpInstantly();
        }

    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isCamelVisuallySitting()) {
            this.sitUpAnimationState.stop();
            this.dashAnimationState.stop();
            if (this.isVisuallySittingDown()) {
                this.sitAnimationState.startIfStopped(this.tickCount);
                this.sitPoseAnimationState.stop();
            } else {
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.sitAnimationState.stop();
            this.sitPoseAnimationState.stop();
            this.dashAnimationState.animateWhen(this.isDashing(), this.tickCount);
            this.sitUpAnimationState.animateWhen(this.isInPoseTransition() && this.getPoseTime() >= 0L, this.tickCount);
        }

    }

    protected void updateWalkAnimation(float partialTick) {
        float f;
        if (this.getPose() == Pose.STANDING && !this.dashAnimationState.isStarted()) {
            f = Math.min(partialTick * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }

        this.walkAnimation.update(f, 0.2F);
    }

    public void travel(Vec3 travelVector) {
        if (this.refuseToMove() && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            travelVector = travelVector.multiply(0.0D, 1.0D, 0.0D);
        }

        super.travel(travelVector);
    }

    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        if (player.zza > 0.0F && this.isCamelSitting() && !this.isInPoseTransition()) {
            this.standUp();
        }
    }

    public boolean refuseToMove() {
        return this.isCamelSitting(); // or any sitting/locked AI state
    }

    protected float getRiddenSpeed(Player player) {
        double base = this.getAttributeValue(Attributes.MOVEMENT_SPEED);
        // When player sprints (and camel's jump cooldown is 0) apply a proportional running bonus.
        if (player.isSprinting() && this.getJumpCooldown() == 0) {
            // RUNNING_SPEED_BONUS is already defined as 0.1F — treat it as relative bonus (10%)
            return (float)(base * (1.0D + RUNNING_SPEED_BONUS));
        } else {
            return (float) base;
        }
    }


    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return this.refuseToMove() ? new Vec2(entity.getXRot(), entity.getYRot()) : super.getRiddenRotation(entity);
    }


    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return this.refuseToMove() ? Vec3.ZERO : super.getRiddenInput(player, travelVector);
    }

    public boolean canJump() {
        return !this.refuseToMove() && super.canJump();
    }

    public void onPlayerJump(int jumpPower) {
        if (this.isSaddled() && this.dashCooldown <= 0 && this.onGround()) {
            super.onPlayerJump(jumpPower);
        }

    }

    public boolean canSprint() {
        return true;
    }

    protected void executeRidersJump(float playerJumpPendingScale, Vec3 travelVector) {
        double d0 = this.getJumpPower();
        this.addDeltaMovement(this.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)(22.2222F * playerJumpPendingScale) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor()).add(0.0D, (double)(1.4285F * playerJumpPendingScale) * d0, 0.0D));
        this.dashCooldown = 55;
        this.setDashing(true);
        this.hasImpulse = true;
        CommonHooks.onLivingJump(this);
    }

    public boolean isDashing() {
        return (Boolean)this.entityData.get(DASH);
    }

    public void setDashing(boolean dashing) {
        this.entityData.set(DASH, dashing);
    }

    public void handleStartJump(int jumpPower) {
        this.makeSound(SoundEvents.CAMEL_DASH);
        this.gameEvent(GameEvent.ENTITY_ACTION);
        this.setDashing(true);
    }

    public void handleStopJump() {
    }

    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAMEL_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.CAMEL_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CAMEL_HURT;
    }

    protected void playStepSound(BlockPos pos, BlockState block) {
        if (block.is(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
            this.playSound(SoundEvents.CAMEL_STEP_SAND, 1.0F, 1.0F);
        } else {
            this.playSound(SoundEvents.CAMEL_STEP, 1.0F, 1.0F);
        }
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.CAMEL_FOOD);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() && !this.isBaby()) {
            this.openCustomInventoryScreen(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            InteractionResult interactionresult = itemstack.interactLivingEntity(player, this, hand);
            if (interactionresult.consumesAction()) {
                return interactionresult;
            } else if (this.isFood(itemstack)) {
                return this.fedFood(player, itemstack);
            } else {
                if (this.getPassengers().size() < 2 && !this.isBaby()) {
                    this.doPlayerRide(player);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
    }

    public boolean handleLeashAtDistance(Entity leashHolder, float distance) {
        if (distance > 6.0F && this.isCamelSitting() && !this.isInPoseTransition() && this.canCamelChangePose()) {
            this.standUp();
        }

        return true;
    }

    public boolean canCamelChangePose() {
        return this.wouldNotSuffocateAtTargetPose(this.isCamelSitting() ? Pose.STANDING : Pose.SITTING);
    }

    protected boolean handleEating(Player player, ItemStack stack) {
        if (!this.isFood(stack)) {
            return false;
        } else {
            boolean flag = this.getHealth() < this.getMaxHealth();
            if (flag) {
                this.heal(2.0F);
            }

            boolean flag1 = this.isTamed() && this.getAge() == 0 && this.canFallInLove();
            if (flag1) {
                this.setInLove(player);
            }

            boolean flag2 = this.isBaby();
            if (flag2) {
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                if (!this.level().isClientSide) {
                    this.ageUp(10);
                }
            }

            if (!flag && !flag1 && !flag2) {
                return false;
            } else {
                if (!this.isSilent()) {
                    SoundEvent soundevent = this.getEatingSound();
                    if (soundevent != null) {
                        this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                    }
                }

                this.gameEvent(GameEvent.EAT);
                return true;
            }
        }
    }

    protected boolean canPerformRearing() {
        return false;
    }

    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal != this && otherAnimal instanceof BactrianCamel camel) {
            if (this.canParent() && camel.canParent()) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public BactrianCamel getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.BACTRIAN_CAMEL.get().create(level);
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return SoundEvents.CAMEL_EAT;
    }

    protected void actuallyHurt(DamageSource damageSource, float damageAmount) {
        this.standUpInstantly();
        super.actuallyHurt(damageSource, damageAmount);
    }

    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        int i = Math.max(this.getPassengers().indexOf(entity), 0);
        boolean flag = i == 0;
        float f = 0.5F;
        float f1 = (float)(this.isRemoved() ? 0.01D : this.getBodyAnchorAnimationYOffset(flag, 0.0F, dimensions, partialTick));
        if (this.getPassengers().size() > 1) {
            if (!flag) {
                f = -0.7F;
            }

            if (entity instanceof Animal) {
                f += 0.2F;
            }
        }

        return (new Vec3(0.0D, (double)f1, (double)(f * partialTick))).yRot(-this.getYRot() * ((float)Math.PI / 180F));
    }

    public float getAgeScale() {
        return this.isBaby() ? 0.45F : 1.0F;
    }

    private double getBodyAnchorAnimationYOffset(boolean firstPassenger, float partialTick, EntityDimensions dimensions, float scale) {
        double d0 = (double)(dimensions.height() - 0.375F * scale);
        float f = scale * 1.43F;
        float f1 = f - scale * 0.2F;
        float f2 = f - f1;
        boolean flag = this.isInPoseTransition();
        boolean flag1 = this.isCamelSitting();
        if (flag) {
            int i = flag1 ? 40 : 52;
            int j;
            float f3;
            if (flag1) {
                j = 28;
                f3 = firstPassenger ? 0.5F : 0.1F;
            } else {
                j = firstPassenger ? 24 : 32;
                f3 = firstPassenger ? 0.6F : 0.35F;
            }

            float f4 = Mth.clamp((float)this.getPoseTime() + partialTick, 0.0F, (float)i);
            boolean flag2 = f4 < (float)j;
            float f5 = flag2 ? f4 / (float)j : (f4 - (float)j) / (float)(i - j);
            float f6 = f - f3 * f1;
            d0 += flag1 ? (double)Mth.lerp(f5, flag2 ? f : f6, flag2 ? f6 : f2) : (double)Mth.lerp(f5, flag2 ? f2 - f : f2 - f6, flag2 ? f2 - f6 : 0.0F);
        }

        if (flag1 && !flag) {
            d0 += (double)f2;
        }

        return d0;
    }

    public Vec3 getLeashOffset(float partialTick) {
        EntityDimensions entitydimensions = this.getDimensions(this.getPose());
        float f = this.getAgeScale();
        return new Vec3(0.0D, this.getBodyAnchorAnimationYOffset(true, partialTick, entitydimensions, f) - (double)(0.2F * f), (double)(entitydimensions.width() * 0.56F));
    }

    public int getMaxHeadYRot() {
        return 30;
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() <= 2;
    }

    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public boolean isCamelSitting() {
        return (Long)this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }

    public boolean isCamelVisuallySitting() {
        return this.getPoseTime() < 0L != this.isCamelSitting();
    }

    public boolean isInPoseTransition() {
        long i = this.getPoseTime();
        return i < (long)(this.isCamelSitting() ? 40 : 52);
    }

    private boolean isVisuallySittingDown() {
        return this.isCamelSitting() && this.getPoseTime() < 40L && this.getPoseTime() >= 0L;
    }

    public void sitDown() {
        if (!this.isCamelSitting()) {
            this.makeSound(SoundEvents.CAMEL_SIT);
            this.setPose(Pose.SITTING);
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.resetLastPoseChangeTick(-this.level().getGameTime());
        }

    }

    public void standUp() {
        if (this.isCamelSitting()) {
            this.makeSound(SoundEvents.CAMEL_STAND);
            this.setPose(Pose.STANDING);
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.resetLastPoseChangeTick(this.level().getGameTime());
        }

    }

    public void standUpInstantly() {
        this.setPose(Pose.STANDING);
        this.gameEvent(GameEvent.ENTITY_ACTION);
        this.resetLastPoseChangeTickToFullStand(this.level().getGameTime());
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long lastPoseChangeTick) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, lastPoseChangeTick);
    }

    private void resetLastPoseChangeTickToFullStand(long lastPoseChangedTick) {
        this.resetLastPoseChangeTick(Math.max(0L, lastPoseChangedTick - 52L - 1L));
    }

    public long getPoseTime() {
        return this.level().getGameTime() - Math.abs((Long)this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    public SoundEvent getSaddleSoundEvent() {
        return SoundEvents.CAMEL_SADDLE;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (!this.firstTick && DASH.equals(key)) {
            this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
        }

        super.onSyncedDataUpdated(key);
    }

    public boolean isTamed() {
        return true;
    }

    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide) {
            player.openHorseInventory(this, this.inventory);
        }

    }

    protected BodyRotationControl createBodyControl() {
        return new BactrianCamel.CamelBodyRotationControl(this);
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        BactrianCamelSpit spit = new BactrianCamelSpit(this.level(), this);
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - spit.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2) * 0.2D;
        spit.shoot(d0, d1 + d3, d2, 1.5F, 10.0F); // Last two values are velocity and inaccuracy

        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

        this.level().addFreshEntity(spit);
    }

    static {
        DASH = SynchedEntityData.defineId(BactrianCamel.class, EntityDataSerializers.BOOLEAN);
        LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(BactrianCamel.class, EntityDataSerializers.LONG);
        SITTING_DIMENSIONS = EntityDimensions.scalable(1.7F, 2.375F - 1.43F).withEyeHeight(0.845F);
    }

    class CamelBodyRotationControl extends BodyRotationControl {
        public CamelBodyRotationControl(BactrianCamel camel) {
            super(camel);
        }

        public void clientTick() {
            if (!BactrianCamel.this.refuseToMove()) {
                super.clientTick();
            }
        }
    }

    class BactrianCamelLookControl extends LookControl {
        BactrianCamelLookControl() {
            super(BactrianCamel.this);
        }

        public void tick() {
            if (!BactrianCamel.this.hasControllingPassenger()) {
                super.tick();
            }
        }
    }

    class BactrianCamelMoveControl extends MoveControl {
        public BactrianCamelMoveControl() {
            super(BactrianCamel.this);
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO && !BactrianCamel.this.isLeashed() && BactrianCamel.this.isCamelSitting() && !BactrianCamel.this.isInPoseTransition() && BactrianCamel.this.canCamelChangePose()) {
                BactrianCamel.this.standUp();
            }

            super.tick();
        }
    }
}