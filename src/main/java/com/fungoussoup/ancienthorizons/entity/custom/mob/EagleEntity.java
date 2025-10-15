package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EagleEntity extends TamableAnimal implements NeutralMob, SemiFlyer {
    // Data accessors for syncing client-server state
    private static final EntityDataAccessor<Boolean> EAGLE_FLYING =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> EAGLE_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_PREY =
            SynchedEntityData.defineId(EagleEntity.class, EntityDataSerializers.BOOLEAN);

    // Neutral mob properties
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private UUID targetUUID;

    // Sit and other animations
    public AnimationState sitAnimationState = new AnimationState();

    // Flight state management
    private boolean wasFlying = false;
    private int groundTimer = 0;

    // Golden Eagle specific behavior
    private int swoopCooldown = 0;
    private LivingEntity preyTarget;
    private boolean isCarryingPrey = false;
    private final int huntingRange = 32;

    private boolean isHunting = false;

    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    public EagleEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level, 32);
        this.moveControl = new SemiFlyingMoveControl(this);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(EAGLE_FLYING, false);
        builder.define(EAGLE_REMAINING_ANGER_TIME, 0);
        builder.define(HAS_PREY, false);
    }

    @Override
    protected void registerGoals() {
        // Priority 0: Float in water
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));

        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.2, 16, 1));

        // Priority 5: Main flying OR walking behavior
        this.goalSelector.addGoal(5, new SemiFlyingFlyGoal(this, 1.0));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));

        // Priority 6: Swoop attack
        this.goalSelector.addGoal(6, new SwoopAttackGoal());

        // Priority 7: Look at players
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8));

        // Priority 8: Random looking
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));

        // Hunt small animals and rabbits when wild or commanded
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Animal.class, 15, true, false,
                (entity) -> (!this.isTame() || shouldHunt()) &&
                        isValidPrey(entity)));
    }

    @Override
    public boolean isNoGravity() {
        return this.isFlying() || super.isNoGravity();
    }


    private boolean isValidPrey(LivingEntity entity) {
        return entity.getType().is(ModTags.EntityTypes.EAGLE_PREY);
    }

    private boolean shouldHunt() {
        return !isOrderedToSit();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0) // Golden eagles are robust
                .add(Attributes.FLYING_SPEED, 0.8) // Fast fliers
                .add(Attributes.MOVEMENT_SPEED, 0.25) // Slower on the ground
                .add(Attributes.ATTACK_DAMAGE, 6.0) // Powerful talons
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3); // Stable in flight
    }

    @Override
    public void tick() {
        super.tick();

        // Smoothly maintain or restore flight
        if (isFlying()) {
            this.resetFallDistance();

            // Hover slightly instead of dropping too fast
            if (this.getDeltaMovement().y < 0 && !this.isCarryingPrey) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
            }

            // Random small motion to simulate air drift
            if (this.getNavigation().isDone() && this.random.nextInt(80) == 0) {
                double dx = this.getX() + (this.random.nextDouble() - 0.5) * 12.0;
                double dy = this.getY() + (this.random.nextDouble() - 0.5) * 6.0;
                double dz = this.getZ() + (this.random.nextDouble() - 0.5) * 12.0;
                this.getNavigation().moveTo(dx, dy, dz, 1.0);
            }

            // Land automatically if close to ground and not hunting
            if (this.onGround() || (this.verticalCollisionBelow && !this.isHunting)) {
                stopFlying();
            }
        } else {
            // Take off if startled, hunting, or falling too long
            if (!this.onGround() && !this.isInWater() && this.random.nextFloat() < 0.01F) {
                startFlying();
            }
        }

        if (isHunting && (getTarget() == null || !getTarget().isAlive())) {
            // Search for prey
            List<Animal> possiblePrey = this.level().getEntitiesOfClass(
                    Animal.class,
                    this.getBoundingBox().inflate(huntingRange),
                    this::isValidPrey
            );

            Animal nearestPrey = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Animal prey : possiblePrey) {
                double distanceSq = this.distanceToSqr(prey);
                if (distanceSq < nearestDistance) {
                    nearestDistance = distanceSq;
                    nearestPrey = prey;
                }
            }

            if (nearestPrey != null) {
                this.setTarget(nearestPrey);
            } else {
                isHunting = false;
            }
        }

        // Update ground timer
        if (this.onGround()) {
            groundTimer++;
        } else {
            groundTimer = 0;
        }

        // Update swoop cooldown
        if (swoopCooldown > 0) {
            swoopCooldown--;
        }

        // Handle flying state changes
        boolean currentlyFlying = isFlying();
        if (currentlyFlying != wasFlying) {
            wasFlying = currentlyFlying;
        }

        // Handle prey carrying
        if (isCarryingPrey && preyTarget != null) {
            if (!preyTarget.isAlive() || preyTarget.isRemoved()) {
                dropPrey();
            } else {
                Vec3 eaglePos = this.position();
                preyTarget.setPos(eaglePos.x, eaglePos.y - 0.8, eaglePos.z);
                preyTarget.setDeltaMovement(this.getDeltaMovement());
            }
        }

        this.entityData.set(HAS_PREY, isCarryingPrey);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.onGround() && !this.isFlying() && !this.isInWater()) {
            this.startFlying();
        }

        // Update anger naturally
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }

        // Adjust movement based on flying state
        if (isFlying()) {
            this.fallDistance = 0;

            // Apply slight upward force to maintain altitude (unless in falconry mode)
            if (this.getDeltaMovement().y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.85, 1.0));
            }
        }
    }

    // NeutralMob interface implementation
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(EAGLE_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(EAGLE_REMAINING_ANGER_TIME, time);
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.targetUUID;
    }

    @Override
    public void setPersistentAngerTarget(UUID uuid) {
        this.targetUUID = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    // Interaction handling
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            return InteractionResult.CONSUME;
        }

        // Taming logic with raw rabbit or raw chicken (golden eagle diet)
        if (!this.isTame()) {
            if (itemStack.is(Items.RABBIT) || itemStack.is(Items.CHICKEN)) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                // A higher chance to tame with preferred food
                int tameChance = itemStack.is(Items.RABBIT) ? 4 : 3;
                if (this.random.nextInt(tameChance) == 0) {
                    this.tame(player);
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }

                return InteractionResult.SUCCESS;
            }
        } else {
            if (this.isOwnedBy(player)) {
                if (itemStack.isEmpty()) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    // Combat and prey handling
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);

        if (result && target instanceof LivingEntity living) {
            // Chance to grab prey
            if (isValidPrey(living) && !isCarryingPrey && this.random.nextFloat() < 0.7f) {
                grabPrey(living);
            }
        }

        return result;
    }

    private void grabPrey(LivingEntity prey) {

        if (prey.isAlive() && !isCarryingPrey && prey.getFirstPassenger() == null && !prey.isRemoved()) {
            this.preyTarget = prey;
            this.isCarryingPrey = true;
            prey.setNoGravity(true);
            prey.setSilent(true);

            this.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.2f);

            if (this.random.nextFloat() < 0.3f) {
                prey.hurt(this.damageSources().mobAttack(this), 1000);
            }
        }
    }

    private void dropPrey() {
        if (preyTarget != null) {
            preyTarget.stopRiding();
            preyTarget.setNoGravity(false);
            preyTarget.setSilent(false);
            preyTarget = null;
        }
        isCarryingPrey = false;

    }

    // Sound handling - Golden eagle sounds
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.EAGLE_AMBIENT; // Replace it with custom golden eagle sound
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.EAGLE_HURT; // Replace it with custom golden eagle sound
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.EAGLE_DEATH; // Replace it with custom golden eagle sound
    }

    // Flight physics
    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    // Food and breeding
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.MEAT);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        EagleEntity baby = ModEntities.EAGLE.get().create(serverLevel);
        if (baby != null) {
            UUID ownerUUID = this.getOwnerUUID();
            if (ownerUUID != null) {
                baby.setOwnerUUID(ownerUUID);
                baby.setTame(true, true);
            }
        }
        return baby;
    }

    // NBT data handling
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsFlying", this.isFlying());
        tag.putBoolean("IsCarryingPrey", this.isCarryingPrey);
        tag.putInt("GroundTimer", this.groundTimer);
        tag.putInt("SwoopCooldown", this.swoopCooldown);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(EAGLE_FLYING, tag.getBoolean("IsFlying"));
        this.isCarryingPrey = tag.getBoolean("IsCarryingPrey");
        this.groundTimer = tag.getInt("GroundTimer");
        this.swoopCooldown = tag.getInt("SwoopCooldown");
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public void startFlying() {
        this.entityData.set(EAGLE_FLYING, true);
    }

    @Override
    public void stopFlying() {
        this.entityData.set(EAGLE_FLYING, false);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(EAGLE_FLYING);
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    private class SwoopAttackGoal extends Goal {
        private LivingEntity target;
        private int swoopPhase = 0;

        @Override
        public boolean canUse() {
            if (swoopCooldown > 0) return false;

            target = getTarget();
            return target != null && isFlying() &&
                    distanceToSqr(target) < 100.0 &&
                    getY() > target.getY() + 2.0;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && swoopPhase < 3;
        }

        @Override
        public void start() {
            swoopPhase = 0;
        }

        @Override
        public void tick() {
            if (target == null) return;

            switch (swoopPhase) {
                case 0: // Rising phase
                    if (getY() < target.getY() + 10.0) {
                        Vec3 upward = getDeltaMovement().add(0, 0.15, 0);
                        setDeltaMovement(upward);
                    } else {
                        swoopPhase = 1;
                    }
                    break;

                case 1: // Diving phase
                    Vec3 toTarget = target.position().subtract(position()).normalize();
                    Vec3 dive = toTarget.add(0, -0.4, 0).normalize().scale(1.0);
                    setDeltaMovement(dive);

                    if (distanceToSqr(target) < 6.0) {
                        doHurtTarget(target);
                        swoopPhase = 2;
                    }
                    break;

                case 2: // Recovery phase
                    Vec3 recover = getDeltaMovement().add(0, 0.3, 0);
                    setDeltaMovement(recover);
                    swoopPhase = 3;
                    break;
            }
        }

        @Override
        public void stop() {
            target = null;
            swoopPhase = 0;
            swoopCooldown = 120; // 6-second cooldown
        }
    }

    @Override
    public void setFlying(boolean flying) {
        boolean currentlyFlying = isFlying();
        if (currentlyFlying == flying) return;

        this.entityData.set(EAGLE_FLYING, flying);

        if (flying) {
            this.navigation = flyingNavigation;
            this.moveControl = new SemiFlyingMoveControl(this, 10, 5);
            this.setNoGravity(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2); // slower lateral speed
        } else {
            this.navigation = groundNavigation;
            this.moveControl = new MoveControl(this);
            this.setNoGravity(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        }
    }

    public boolean isCarryingPrey() {
        return isCarryingPrey;
    }

    public boolean hasVisiblePrey() {
        return this.entityData.get(HAS_PREY);
    }
}