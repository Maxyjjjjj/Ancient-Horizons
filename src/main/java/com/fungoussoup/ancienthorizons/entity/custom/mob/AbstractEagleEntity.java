package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.*;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public abstract class AbstractEagleEntity extends TamableAnimal implements NeutralMob, SemiFlyer {

    // Flight state management
    public enum FlightState {
        GROUNDED,
        TAKEOFF,
        FLYING,
        LANDING
    }

    protected FlightState flightState = FlightState.GROUNDED;
    protected int flightStateTicks;

    // Synched data
    protected static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_HAS_PREY =
            SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(AbstractEagleEntity.class, EntityDataSerializers.BOOLEAN);

    // Neutral mob
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private UUID persistentAngerTarget;

    // Navigation
    protected final PathNavigation groundNavigation;
    protected final PathNavigation flyingNavigation;

    // Hunting & prey
    protected LivingEntity swoopTarget;
    protected int swoopTicks;
    protected int maxSwoopDuration = 40;
    protected LivingEntity carriedPrey;
    public int swoopCooldown;
    protected BlockPos perchTarget;

    public AnimationState sitAnimationState = new AnimationState();

    protected AbstractEagleEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
        this.moveControl = new SemiFlyingMoveControl(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
        builder.define(DATA_HAS_PREY, false);
        builder.define(DATA_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Basic goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new ModFollowOwnerGoal(this, 1.2, 16.0F, 2.0F));
        this.goalSelector.addGoal(4, new SemiFlyingFlyGoal(this, 1.0));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new SwoopAttackGoal());
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Targeting
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Animal.class, 15, true, false,
                (entity) -> shouldHunt() && isValidPrey(entity)));
    }

    public abstract boolean isValidPrey(LivingEntity entity);

    protected boolean shouldHunt() {
        return !this.isOrderedToSit();
    }

    @Override
    public void tick() {
        if (this.tickCount > Integer.MAX_VALUE - 1000) {
            this.tickCount = 0;
        }

        super.tick();

        // Move carried prey with the eagle
        if (isCarryingPrey()) {
            try {
                carriedPrey.setPos(this.getX(), this.getY() - 0.5, this.getZ());
                carriedPrey.setDeltaMovement(0, 0, 0);
            } catch (Exception e) {
                dropPrey();
            }
        }

        flightStateTicks++;

        switch (flightState) {
            case TAKEOFF -> tickTakeoff();
            case FLYING -> tickFlying();
            case LANDING -> tickLanding();
            case GROUNDED -> {
            }
        }

        if (swoopCooldown > 0) swoopCooldown--;

        // Sync prey state
        this.entityData.set(DATA_HAS_PREY, isCarryingPrey());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.onGround() && !this.isFlying() && !this.isInWater()) {
            this.startFlying();
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }

        if (isFlying()) {
            this.fallDistance = 0;
            if (this.getDeltaMovement().y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.85, 1.0));
            }
        }
    }

    protected void tickTakeoff() {
        if (flightStateTicks == 1) {
            setDeltaMovement(getDeltaMovement().add(0, 0.4, 0));
        }
        if (flightStateTicks > 10) {
            setFlightState(FlightState.FLYING);
        }
    }

    protected void tickFlying() {
        resetFallDistance();

        if (this.getDeltaMovement().y < 0 && !isCarryingPrey()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
        }

        if (this.getNavigation().isDone() && this.random.nextInt(120) == 0) {
            if (this.level().isLoaded(this.blockPosition())) {
                double dx = this.getX() + (this.random.nextDouble() - 0.5) * 12.0;
                double dy = this.getY() + (this.random.nextDouble() - 0.5) * 6.0;
                double dz = this.getZ() + (this.random.nextDouble() - 0.5) * 12.0;

                BlockPos targetPos = BlockPos.containing(dx, dy, dz);
                if (this.level().isLoaded(targetPos)) {
                    this.getNavigation().moveTo(dx, dy, dz, 1.0);
                }
            }
        }

        if (onGround() || (this.verticalCollisionBelow && swoopTarget == null)) {
            setFlightState(FlightState.GROUNDED);
        }

        if (isCarryingPrey()) {
            swoopTicks++;
            if (swoopTicks >= maxSwoopDuration || !carriedPrey.isAlive()) {
                dropPrey();
            }
        }
    }

    protected void tickLanding() {
        if (perchTarget != null && this.blockPosition().closerThan(perchTarget, 1.0)) {
            setFlightState(FlightState.GROUNDED);
            perchTarget = null;
        }
        if (this.onGround()) {
            setFlightState(FlightState.GROUNDED);
        }
    }

    protected void choosePerch(BlockPos target) {
        this.perchTarget = target;
        setFlightState(FlightState.LANDING);
    }

    protected final void setFlightState(FlightState next) {
        if (flightState == next) return;

        flightState = next;
        flightStateTicks = 0;

        try {
            switch (next) {
                case GROUNDED -> {
                    this.navigation = groundNavigation;
                    this.moveControl = new MoveControl(this);
                    setNoGravity(false);
                    entityData.set(DATA_FLYING, false);
                    if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
                    }
                }
                case TAKEOFF, FLYING -> {
                    this.navigation = flyingNavigation;
                    this.moveControl = new SemiFlyingMoveControl(this, 10, 5);
                    setNoGravity(true);
                    entityData.set(DATA_FLYING, true);
                    if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
                    }
                }
                case LANDING -> {
                    setNoGravity(false);
                }
            }
        } catch (Exception e) {
            // Revert on error
        }
    }

    // Prey management
    public boolean isCarryingPrey() {
        return carriedPrey != null && carriedPrey.isAlive();
    }

    public void pickUpPrey(LivingEntity prey) {
        if (canCarryPrey(prey) && !isCarryingPrey()) {
            carriedPrey = prey;
            prey.setNoGravity(true);
            prey.setSilent(true);
            swoopTicks = 0;
        }
    }

    public void dropPrey() {
        if (carriedPrey != null) {
            carriedPrey.setNoGravity(false);
            carriedPrey.setSilent(false);
            carriedPrey = null;
        }
    }

    public boolean startSwoop(LivingEntity target) {
        if (canSwoop() && target != null && canCarryPrey(target)) {
            swoopTarget = target;
            pickUpPrey(target);
            onSuccessfulSwoop();
            setFlightState(FlightState.FLYING);
            return true;
        }
        return false;
    }

    public boolean canSwoop() {
        return swoopCooldown <= 0 && isFlying();
    }

    public void onSuccessfulSwoop() {
        swoopCooldown = getSwoopCooldownTicks();
    }

    public abstract int getSwoopCooldownTicks();

    public abstract double getSwoopSpeed();

    protected abstract boolean canCarryPrey(LivingEntity prey);

    // SemiFlyer implementation
    @Override
    public PathNavigation getNavigation() {
        return isFlying() ? flyingNavigation : groundNavigation;
    }

    @Override
    public final void startFlying() {
        if (flightState == FlightState.GROUNDED) {
            setFlightState(FlightState.TAKEOFF);
        }
    }

    @Override
    public final void stopFlying() {
        if (flightState == FlightState.FLYING) {
            setFlightState(FlightState.LANDING);
        }
    }

    @Override
    public final boolean isFlying() {
        return flightState == FlightState.FLYING || flightState == FlightState.TAKEOFF;
    }

    @Override
    public final boolean canFly() {
        return true;
    }

    @Override
    public final boolean shouldGlide() {
        return false;
    }

    @Override
    public final void setFlying(boolean flying) {
        if (flying) startFlying();
        else stopFlying();
    }

    @Override
    public boolean isNoGravity() {
        return this.isFlying() || super.isNoGravity();
    }

    // NeutralMob implementation
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    // Interaction handling
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) return InteractionResult.CONSUME;

        if (!this.isTame()) {
            if (stack.is(Items.CHICKEN) || stack.is(Items.RABBIT)) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                int chance = stack.is(Items.RABBIT) ? 4 : 3;
                if (this.random.nextInt(chance) == 0) {
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
                if (stack.isEmpty()) {
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

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            if (isValidPrey(living) && !isCarryingPrey() && this.random.nextFloat() < 0.7f) {
                pickUpPrey(living);
            }
        }
        return result;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    // NBT
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        swoopCooldown = tag.getInt("SwoopCooldown");
        swoopTicks = tag.getInt("SwoopTicks");

        if (tag.getBoolean("Flying")) {
            flightState = FlightState.FLYING;
            setNoGravity(true);
        } else {
            flightState = FlightState.GROUNDED;
            setNoGravity(false);
        }

        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Flying", isFlying());
        tag.putInt("SwoopCooldown", swoopCooldown);
        tag.putInt("SwoopTicks", swoopTicks);
        this.addPersistentAngerSaveData(tag);
    }


    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    public boolean hasVisiblePrey() {
        return this.entityData.get(DATA_HAS_PREY);
    }
}