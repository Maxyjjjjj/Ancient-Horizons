package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.ModFollowOwnerGoal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

public class DearcEntity extends TamableAnimal implements SemiFlyer {

    private enum FlightState {
        GROUNDED,
        TAKEOFF,
        FLYING,
        LANDING
    }

    private FlightState flightState = FlightState.GROUNDED;
    private int flightTicks;

    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(DearcEntity.class, EntityDataSerializers.BOOLEAN);

    private final PathNavigation groundNavigation;
    private final PathNavigation airNavigation;

    public DearcEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);

        this.moveControl = new MoveControl(this);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.airNavigation = new BirdNavigation(this, level);

        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new ModFollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
    }

    @Override
    public void tick() {
        super.tick();

        flightTicks++;

        switch (flightState) {
            case TAKEOFF -> tickTakeoff();
            case FLYING -> tickFlying();
            case LANDING -> tickLanding();
            default -> {}
        }
    }

    private void tickTakeoff() {
        if (flightTicks == 1) {
            setDeltaMovement(getDeltaMovement().add(0, 0.4, 0));
            setNoGravity(true);
        }
        if (flightTicks > 10) {
            setFlightState(FlightState.FLYING);
        }
    }

    private void tickFlying() {
        resetFallDistance();
        if (onGround()) {
            setFlightState(FlightState.GROUNDED);
        }
    }

    private void tickLanding() {
        if (onGround()) {
            setFlightState(FlightState.GROUNDED);
        }
    }

    /* =========================
       STATE TRANSITIONS (ONLY PLACE)
       ========================= */

    private void setFlightState(FlightState next) {
        if (flightState == next) return;

        flightState = next;
        flightTicks = 0;

        switch (next) {
            case GROUNDED -> {
                setNoGravity(false);
                entityData.set(DATA_FLYING, false);
            }
            case TAKEOFF, FLYING -> {
                setNoGravity(true);
                entityData.set(DATA_FLYING, true);
            }
            case LANDING -> setNoGravity(false);
        }
    }


    @Override
    public PathNavigation getNavigation() {
        return isFlying() ? airNavigation : groundNavigation;
    }

    @Override
    public void startFlying() {
        if (canFly() && flightState == FlightState.GROUNDED) {
            setFlightState(FlightState.TAKEOFF);
        }
    }

    @Override
    public void stopFlying() {
        if (flightState == FlightState.FLYING) {
            setFlightState(FlightState.LANDING);
        }
    }

    @Override
    public boolean isFlying() {
        return flightState == FlightState.FLYING || flightState == FlightState.TAKEOFF;
    }

    @Override
    public void setFlying(boolean flying) {
        if (flying) startFlying();
        else stopFlying();
    }

    @Override
    public boolean canFly() {
        return !isBaby() && !isInWater();
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float dist, float mul, DamageSource src) {
        return !isFlying() && super.causeFallDamage(dist, mul, src);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.DEARC.get().create(level);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PHANTOM_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.PHANTOM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PHANTOM_DEATH; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Flying", isFlying());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFlightState(tag.getBoolean("Flying") ? FlightState.FLYING : FlightState.GROUNDED);
    }
}