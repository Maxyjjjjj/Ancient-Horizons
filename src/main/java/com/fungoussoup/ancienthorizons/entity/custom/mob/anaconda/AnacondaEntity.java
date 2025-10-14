package com.fungoussoup.ancienthorizons.entity.custom.mob.anaconda;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;
import com.fungoussoup.ancienthorizons.entity.navigations.AnimalSwimMoveControllerSink;
import com.fungoussoup.ancienthorizons.entity.navigations.GroundPathNavigatorWide;
import com.fungoussoup.ancienthorizons.entity.navigations.SemiAquaticPathNavigator;
import com.fungoussoup.ancienthorizons.entity.util.AnacondaPartIndex;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import com.fungoussoup.ancienthorizons.util.Maths;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AnacondaEntity extends SemiAquaticAnimal implements NeutralMob {

    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(AnacondaEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> CHILD_ID = SynchedEntityData.defineId(AnacondaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STRANGLING = SynchedEntityData.defineId(AnacondaEntity.class, EntityDataSerializers.BOOLEAN);

    public final float[] ringBuffer = new float[64];
    public int ringBufferIndex = -1;
    private AnacondaPartEntity[] parts;
    private boolean isLandNavigator;
    private int swimTimer = -1000;
    private float prevStrangleProgress = 0F;
    private float strangleProgress = 0F;
    private int strangleTimer = 0;

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public AnacondaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0F);
        switchNavigator(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHILD_UUID, Optional.empty());
        builder.define(CHILD_ID, -1);
        builder.define(STRANGLING, false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.15F);
    }

    @Nullable
    public UUID getChildId() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    public void setChildId(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new AnimalSwimMoveControllerSink(this, 1.3F, 1F);
            this.navigation = new SemiAquaticPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AnacondaStrangleGoal());
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new SemiAquaticMoveToWaterGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new SemiAquaticLeaveWaterGoal(this, 1.0F));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                e -> e.getType().is(ModTags.EntityTypes.ANACONDA_PREY) || (e.getBbWidth() <= 2.0F && (e instanceof Monster)) || e instanceof Warden));
    }

    public boolean isStrangling() {
        return this.entityData.get(STRANGLING);
    }

    public void setStrangling(boolean running) {
        this.entityData.set(STRANGLING, running);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.MEAT);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean shouldEnterWater() {
        return false;
    }

    @Override
    public boolean shouldLeaveWater() {
        return false;
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 0;
    }

    public float getBodyWaveOffset(int partIndex, float partialTicks) {
        // Creates a more natural wave that considers both movement and idle state
        float idleWave = Mth.sin((this.tickCount + partialTicks) * 0.05F + partIndex * 0.3F) * 2.0F;
        float movementWave = this.walkDist * 0.5F - (partIndex * 0.5F);

        // Blend between idle and movement waves based on speed
        float speed = (float) this.getDeltaMovement().horizontalDistance();
        float movementBlend = Mth.clamp(speed * 5.0F, 0.0F, 1.0F);

        return Mth.lerp(movementBlend, idleWave, movementWave);
    }

    public void tick() {
        super.tick();

        if (this.isInWater()) {
            if (this.isLandNavigator)
                switchNavigator(false);
        } else {
            if (!this.isLandNavigator)
                switchNavigator(true);
        }

        this.prevStrangleProgress = strangleProgress;
        if (this.isStrangling()) {
            if (strangleProgress < 5F)
                strangleProgress++;
        } else {
            if (strangleProgress > 0F)
                strangleProgress--;
        }

        this.yBodyRot = this.getYRot();
        float maxHeadTurn = 25.0F;
        this.yHeadRot = Mth.clamp(this.yHeadRot, this.yBodyRot - maxHeadTurn, this.yBodyRot + maxHeadTurn);

        if (!this.isStrangling()) {
            float targetBodyYaw = this.getYRot();
            float bodyRotDiff = Mth.wrapDegrees(targetBodyYaw - this.yBodyRot);

            // Much slower body rotation for realistic snake turning
            // Snakes can't pivot on a point - their whole body needs to follow
            float turnSpeed = 0.08F; // Very gradual turning (reduced from 0.3F)

            // Slow down turning even more when moving fast (momentum)
            float speed = (float) this.getDeltaMovement().horizontalDistance();
            float speedPenalty = 1.0F - Mth.clamp(speed * 3.0F, 0.0F, 0.5F);
            turnSpeed *= speedPenalty;

            this.yBodyRot += bodyRotDiff * turnSpeed;
        }

        if (this.isStrangling()) {
            if (!level().isClientSide && this.getTarget() != null && this.getTarget().isAlive()) {
                this.setXRot(0);
                final LivingEntity target = this.getTarget();
                final float radius = this.getTarget().getBbWidth() * -0.5F;
                final float angle = (Maths.STARTING_ANGLE * (target.yBodyRot - 45F));
                final double extraX = radius * Mth.sin(Mth.PI + angle);
                final double extraZ = radius * Mth.cos(angle);
                Vec3 targetVec = new Vec3(extraX + target.getX(), target.getY(1.0F), extraZ + target.getZ());
                Vec3 moveVec = targetVec.subtract(this.position()).scale(1F);
                this.setDeltaMovement(moveVec);
                if (!target.onGround()) {
                    target.setDeltaMovement(new Vec3(0, -0.08F, 0));
                } else {
                    target.setDeltaMovement(Vec3.ZERO);
                }
                if (strangleTimer >= 40 && strangleTimer % 20 == 0) {
                    final double health = Mth.clamp(this.getTarget().getMaxHealth(), 4, 50);
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float) Math.max(4F, 0.25F * health));
                }
                if (this.getTarget() == null || !this.getTarget().isAlive()) {
                    strangleTimer = 0;
                    this.setStrangling(false);
                }
            }
            fallDistance = 0;
            strangleTimer++;
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
        if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i] = this.getYRot();
            }
        }
        this.ringBufferIndex++;
        if (this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }
        this.ringBuffer[this.ringBufferIndex] = this.getYRot();

        if (!this.level().isClientSide) {
            final int segments = 10;
            final Entity child = getChild();

            if (child == null) {
                LivingEntity partParent = this;
                parts = new AnacondaPartEntity[segments];
                Vec3 prevPos = this.position();

                for (int i = 0; i < segments; i++) {
                    AnacondaPartEntity part = new AnacondaPartEntity(ModEntities.ANACONDA_PART.get(), this);

                    part.setParent(partParent);
                    if (partParent == this) {
                        this.setChildId(part.getUUID());
                        this.entityData.set(CHILD_ID, part.getId());
                    } else if (partParent instanceof AnacondaPartEntity partParentEntity) {
                        partParentEntity.setChildId(part.getUUID());
                    }

                    part.copyDataFrom(this);
                    part.setBodyIndex(i);
                    part.setPartType(AnacondaPartIndex.sizeAt(i));

                    float partRotation = this.getYRot() + calcPartRotation(i);

                    part.setPos(part.tickMultipartPosition(
                            this.getId(),
                            AnacondaPartIndex.sizeAt(i),
                            prevPos,
                            this.getXRot(),
                            partRotation,
                            false
                    ));

                    partParent = part;
                    level().addFreshEntity(part);
                    parts[i] = part;

                    prevPos = part.position();
                }
            }

            if (shouldReplaceParts() && this.getChild() instanceof AnacondaPartEntity) {
                parts = new AnacondaPartEntity[segments];
                parts[0] = (AnacondaPartEntity) this.getChild();
                this.entityData.set(CHILD_ID, parts[0].getId());
                int i = 1;
                while (i < parts.length && parts[i - 1].getChild() instanceof AnacondaPartEntity) {
                    parts[i] = (AnacondaPartEntity) parts[i - 1].getChild();
                    i++;
                }
            }

            AnacondaPartIndex parentPartType = AnacondaPartIndex.HEAD;
            Vec3 parentPos = this.position();
            float parentXRot = this.getXRot();
            float parentYRot = this.getYRot();

            for (int i = 0; i < segments; i++) {
                if (this.parts[i] != null) {
                    AnacondaPartEntity currentPart = this.parts[i];

                    currentPart.setStrangleProgress(this.strangleProgress);
                    currentPart.copyDataFrom(this);

                    final float desiredPartYaw = parentYRot + calcPartRotation(i);

                    parentPos = currentPart.tickMultipartPosition(
                            this.getId(),
                            parentPartType,
                            parentPos,
                            parentXRot,
                            desiredPartYaw,
                            true
                    );

                    parentPartType = currentPart.getPartType();
                    parentXRot = currentPart.getXRot();
                    parentYRot = currentPart.getYRot();
                }
            }

            if (isInWater()) swimTimer = Math.max(swimTimer + 1, 0);
            else swimTimer = Math.min(swimTimer - 1, 0);
        }
    }

    private boolean shouldReplaceParts() {
        if (parts == null || parts[0] == null)
            return true;

        for (int i = 0; i < 10; i++) {
            if (parts[i] == null) {
                return true;
            }
        }

        return false;
    }

    public boolean isPushable() {
        return !this.isStrangling();
    }

    public void pushEntities() {
        final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        entities.stream().filter(entity -> !(entity instanceof AnacondaPartEntity) && entity.isPushable()).forEach(entity -> entity.push(this));
    }

    public float getRingBuffer(int bufferOffset, float partialTicks) {
        if (this.isDeadOrDying()) {
            partialTicks = 0.0F;
        }

        partialTicks = 1.0F - partialTicks;
        final int i = this.ringBufferIndex - bufferOffset & 63;
        final int j = this.ringBufferIndex - bufferOffset - 1 & 63;
        final float d0 = this.ringBuffer[i];
        final float d1 = this.ringBuffer[j] - d0;
        return Mth.wrapDegrees(d0 + d1 * partialTicks);
    }

    public Entity getChild() {
        UUID id = getChildId();
        if (id != null && !level().isClientSide) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

    private float calcPartRotation(int i) {
        // More realistic base wave intensity (reduced from 15 to 8 degrees for smoother motion)
        final float waveIntensity = 8.0F;

        // Adjust phase offset for more natural wave propagation
        // This controls how the wave travels down the body
        final float segmentPhaseOffset = 0.5F;

        // Movement speed factor - controls how fast the wave moves
        // Uses walkDist which tracks movement animation progress
        final float movementSpeed = 0.3F;

        // Reduce waving when strangling (keep this - it's good)
        final float waveSuppression = 1.0F - (this.strangleProgress * 0.15F);

        // Add speed-based wave intensity - snakes wave more when moving faster
        final float speedFactor = (float) Mth.clamp(this.getDeltaMovement().horizontalDistance() * 2.0F, 0.3F, 1.0F);

        // TURNING ENHANCEMENT: Add influence from body rotation changes
        // This makes the body curve naturally during turns
        float rotationDelta = Mth.wrapDegrees(this.getYRot() - this.yBodyRot);
        float turnInfluence = rotationDelta * 0.3F * (1.0F - (i * 0.08F)); // Decreases toward tail

        // Strangling coil logic (improved for smoother coiling)
        final float strangleRotation = this.strangleProgress * 0.15F * i;
        final float stranglePulse = (float) (1.0F + 0.15F * Math.sin(0.1F * strangleTimer));
        final float strangleIntensity = Mth.clamp(strangleTimer * 2.5F, 0, 80F) * stranglePulse;

        // Calculate wave movement with proper phase offset
        float waveMovement = this.walkDist * movementSpeed;

        // Base serpentine wave - this creates the S-curve motion
        float baseWave = waveIntensity * speedFactor *
                -Mth.sin(waveMovement - (i * segmentPhaseOffset)) *
                waveSuppression;

        // Add slight vertical influence based on terrain
        float verticalInfluence = 0.0F;
        if (!this.isInWater() && this.onGround()) {
            // Add subtle side-to-side adjustment when on ground
            verticalInfluence = Mth.sin(this.tickCount * 0.05F + i * 0.3F) * 1.5F * speedFactor;
        }

        // Swimming motion - different wave pattern in water
        float swimWave = 0.0F;
        if (this.isInWater()) {
            swimWave = waveIntensity * 0.7F *
                    -Mth.sin((this.tickCount * 0.1F) - (i * 0.4F)) *
                    speedFactor;
        }

        float strangleCoil = strangleRotation * strangleIntensity;

        // Combine all movements
        if (this.isInWater()) {
            return swimWave + turnInfluence + strangleCoil;
        } else {
            return baseWave + verticalInfluence + turnInfluence + strangleCoil;
        }
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
    public void setPersistentAngerTarget(@javax.annotation.Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public boolean shouldMove() {
        return !this.isStrangling();
    }

    public void travel(Vec3 travelVector) {
        if (!this.shouldMove()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(travelVector);
        }

    }

    public float getStrangleProgress(float partialTick) {
        return this.prevStrangleProgress + (this.strangleProgress - this.prevStrangleProgress) * partialTick;
    }

    private class AnacondaStrangleGoal extends Goal {
        private final AnacondaEntity anaconda;
        private int jumpAttemptCooldown = 0;

        public AnacondaStrangleGoal() {
            anaconda = AnacondaEntity.this;
        }

        @Override
        public boolean canUse() {
            return anaconda.getTarget() != null && anaconda.getTarget().isAlive();
        }

        public void tick() {
            if (jumpAttemptCooldown > 0)
                jumpAttemptCooldown--;

            final LivingEntity target = anaconda.getTarget();
            if (target != null && target.isAlive()) {
                if (jumpAttemptCooldown == 0 && anaconda.distanceTo(target) < 1 + target.getBbWidth() && !anaconda.isStrangling()) {
                    target.hurt(anaconda.damageSources().mobAttack(anaconda), 4);
                    anaconda.setStrangling(target.getBbWidth() <= 2.0F && !(target instanceof AnacondaEntity));
                    jumpAttemptCooldown = 5 + random.nextInt(5);
                }
                if (anaconda.isStrangling()) {
                    anaconda.getNavigation().stop();
                } else {
                    try {
                        anaconda.getNavigation().moveTo(target, 1.3F);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void stop() {
            anaconda.setStrangling(false);
        }
    }
}