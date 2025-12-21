package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.HoatzinFlightGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoatzinEntity extends Animal implements SemiFlyer {
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(HoatzinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_FLIGHT_STAMINA = SynchedEntityData.defineId(HoatzinEntity.class, EntityDataSerializers.INT);

    private static final int MAX_FLIGHT_STAMINA = 200; // 10 seconds of flight
    private static final int STAMINA_RECOVERY_RATE = 2; // Stamina per tick when not flying
    private static final int FLIGHT_STAMINA_COST = 1; // Stamina cost per tick when flying
    private static final double SMELL_RANGE = 16.0; // Range at which the smell affects entities
    private static final int SMELL_EFFECT_INTERVAL = 40; // Ticks between smell effects (2 seconds)

    private int smellTimer = 0;
    private int flightCooldown = 0;

    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;

    public HoatzinEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new HoatzinFlightGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_FLIGHT_STAMINA, MAX_FLIGHT_STAMINA);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    public void tick() {
        // Safety: prevent overflow
        if (this.tickCount > Integer.MAX_VALUE - 1000) {
            this.tickCount = 0;
        }

        super.tick();

        handleFlightMechanics();
        handleSmellEffect();

        // Switch navigation with safety
        try {
            this.navigation = isFlying() ? flyingNavigation : groundNavigation;
        } catch (Exception e) {
            // Keep current navigation if switch fails
        }

        // Recover stamina when not flying
        if (!isFlying() && getFlightStamina() < MAX_FLIGHT_STAMINA) {
            setFlightStamina(Math.min(MAX_FLIGHT_STAMINA, getFlightStamina() + STAMINA_RECOVERY_RATE));
        }

        if (flightCooldown > 0) {
            flightCooldown--;
        }
    }

    private int landTimer = 0;

    private void handleFlightMechanics() {
        if (isFlying()) {
            int stamina = getFlightStamina();
            if (stamina > 0) {
                setFlightStamina(stamina - FLIGHT_STAMINA_COST);
            } else {
                stopFlying();
                flightCooldown = 100;
            }

            if (!this.onGround()) {
                Vec3 motion = this.getDeltaMovement();
                if (motion.y < 0) {
                    this.setDeltaMovement(motion.x, motion.y * 0.8D, motion.z);
                }
            }

            if (this.onGround()) {
                landTimer++;
                if (landTimer > 5) stopFlying(); // don’t cut flight instantly
            } else {
                landTimer = 0;
            }
        }
    }


    private void handleSmellEffect() {
        smellTimer++;
        if (smellTimer >= SMELL_EFFECT_INTERVAL) {
            smellTimer = 0;
        }
    }

    @Override
    public void startFlying() {
        if (!canFly()) return;

        setFlying(true);
        // Give initial upward momentum
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, Math.max(motion.y, 0.3D), motion.z);
    }

    @Override
    public void stopFlying() {
        setFlying(false);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    @Override
    public boolean canFly() {
        return flightCooldown <= 0 && !this.isBaby();
    }

    @Override
    public boolean shouldGlide() {
        return !this.onGround() && this.getDeltaMovement().y < -0.1D && !isFlying();
    }

    @Override
    public void setFlying(boolean flying) {
        boolean currently = isFlying();
        if (currently == flying) return;

        this.entityData.set(DATA_FLYING, flying);

        try {
            if (flying) {
                this.moveControl = new SemiFlyingMoveControl(this, 10, 9);
                this.navigation = this.flyingNavigation;
                this.setNoGravity(true);
            } else {
                this.moveControl = new MoveControl(this);
                this.navigation = this.groundNavigation;
                this.setNoGravity(false);
            }
        } catch (Exception e) {
            // Revert if something goes wrong
            this.entityData.set(DATA_FLYING, currently);
        }
    }

    public int getFlightStamina() {
        return this.entityData.get(DATA_FLIGHT_STAMINA);
    }

    public void setFlightStamina(int stamina) {
        this.entityData.set(DATA_FLIGHT_STAMINA, Mth.clamp(stamina, 0, MAX_FLIGHT_STAMINA));
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.LEAVES) ||
                itemStack.is(Items.WHEAT_SEEDS) ||
                itemStack.is(Items.BEETROOT_SEEDS) ||
                itemStack.is(Items.PUMPKIN_SEEDS) ||
                itemStack.is(Items.MELON_SEEDS) ||
                itemStack.getItem() == Items.SWEET_BERRIES ||
                itemStack.getItem() == Items.APPLE;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.HOATZIN.get().create(serverLevel);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT; // Placeholder, you should create custom sounds
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", isFlying());
        compound.putInt("FlightStamina", getFlightStamina());
        compound.putInt("FlightCooldown", flightCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setFlying(compound.getBoolean("Flying"));
        setFlightStamina(compound.getInt("FlightStamina"));
        flightCooldown = compound.getInt("FlightCooldown");
    }
}