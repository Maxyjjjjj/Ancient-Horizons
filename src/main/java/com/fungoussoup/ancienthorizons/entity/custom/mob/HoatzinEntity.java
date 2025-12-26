package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.HoatzinFlightGoal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HoatzinEntity extends Animal implements SemiFlyer {

    /* =========================
       DATA
       ========================= */

    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(HoatzinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_FLIGHT_STAMINA =
            SynchedEntityData.defineId(HoatzinEntity.class, EntityDataSerializers.INT);

    private static final int MAX_FLIGHT_STAMINA = 200;
    private static final int STAMINA_COST = 1;
    private static final int STAMINA_RECOVER = 2;

    /* =========================
       STATE
       ========================= */

    private FlightState flightState = FlightState.GROUNDED;
    private int flightStateTicks;
    private int flightCooldown;

    private enum FlightState {
        GROUNDED,
        TAKEOFF,
        FLYING,
        LANDING
    }

    /* =========================
       NAVIGATION (STABLE)
       ========================= */

    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    public HoatzinEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
    }

    /* =========================
       ATTRIBUTES
       ========================= */

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    /* =========================
       GOALS
       ========================= */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new HoatzinFlightGoal(this)); // REQUEST ONLY
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    /* =========================
       SYNC
       ========================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_FLIGHT_STAMINA, MAX_FLIGHT_STAMINA);
    }

    /* =========================
       TICK
       ========================= */

    @Override
    public void tick() {
        super.tick();

        flightStateTicks++;

        switch (flightState) {
            case TAKEOFF -> tickTakeoff();
            case FLYING -> tickFlying();
            case LANDING -> tickLanding();
            case GROUNDED -> recoverStamina();
        }

        if (flightCooldown > 0) flightCooldown--;
    }

    private void tickTakeoff() {
        if (flightStateTicks == 1) {
            setNoGravity(true);
            setDeltaMovement(getDeltaMovement().add(0, 0.3, 0));
        }
        if (flightStateTicks > 10) {
            setFlightState(FlightState.FLYING);
        }
    }

    private void tickFlying() {
        setFlightStamina(getFlightStamina() - STAMINA_COST);
        resetFallDistance();

        if (getFlightStamina() <= 0) {
            flightCooldown = 100;
            setFlightState(FlightState.LANDING);
        }

        if (onGround()) {
            setFlightState(FlightState.GROUNDED);
        }
    }

    private void tickLanding() {
        setNoGravity(false);
        if (onGround()) {
            setFlightState(FlightState.GROUNDED);
        }
    }

    private void recoverStamina() {
        if (getFlightStamina() < MAX_FLIGHT_STAMINA) {
            setFlightStamina(getFlightStamina() + STAMINA_RECOVER);
        }
    }

    /* =========================
       STATE TRANSITION
       ========================= */

    private void setFlightState(FlightState next) {
        if (flightState == next) return;

        flightState = next;
        flightStateTicks = 0;

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

    /* =========================
       NAVIGATION
       ========================= */

    @Override
    public PathNavigation getNavigation() {
        return isFlying() ? flyingNavigation : groundNavigation;
    }

    /* =========================
       SemiFlyer (REQUEST ONLY)
       ========================= */

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
        return flightCooldown <= 0 && !isBaby();
    }

    @Override
    public boolean shouldGlide() {
        return false;
    }

    /* =========================
       MISC
       ========================= */

    public int getFlightStamina() {
        return entityData.get(DATA_FLIGHT_STAMINA);
    }

    private void setFlightStamina(int value) {
        entityData.set(DATA_FLIGHT_STAMINA, Mth.clamp(value, 0, MAX_FLIGHT_STAMINA));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.LEAVES) || stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) || stack.is(Items.SWEET_BERRIES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.HOATZIN.get().create(level);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PARROT_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.PARROT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PARROT_DEATH; }

    /* =========================
       NBT
       ========================= */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Flying", isFlying());
        tag.putInt("Stamina", getFlightStamina());
        tag.putInt("Cooldown", flightCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFlightState(tag.getBoolean("Flying") ? FlightState.FLYING : FlightState.GROUNDED);
        setFlightStamina(tag.getInt("Stamina"));
        flightCooldown = tag.getInt("Cooldown");
    }
}
