package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BirdNavigation;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingFlyGoal;
import com.fungoussoup.ancienthorizons.entity.ai.SemiFlyingMoveControl;
import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
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
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * PhilippineEagleEntity
 * - Tamable, NeutralMob
 * - Semi-flying: swaps navigation and moveControl when flying
 * - Swoop attack, prey grabbing, persistent anger system
 * - Save/load NBT
 */
public class PhilippineEagleEntity extends TamableAnimal implements NeutralMob, SemiFlyer {
    // Synched data keys
    private static final EntityDataAccessor<Boolean> DATA_FLYING =
            SynchedEntityData.defineId(PhilippineEagleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(PhilippineEagleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_PREY =
            SynchedEntityData.defineId(PhilippineEagleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(PhilippineEagleEntity.class, EntityDataSerializers.BOOLEAN);

    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    // Flight & hunting state
    private final PathNavigation groundNavigation;
    private final PathNavigation flyingNavigation;
    private boolean wasFlying = false;
    private int groundTimer = 0;

    private int swoopCooldown = 0;
    private LivingEntity preyTarget = null;
    private boolean isCarryingPrey = false;
    private boolean isHunting = false;
    private int huntingRange = 28;

    public PhilippineEagleEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setCanPickUpLoot(true);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.flyingNavigation = new BirdNavigation(this, level, 32);
        this.moveControl = new SemiFlyingMoveControl(this);
    }

    /* ----------------------
       Attributes
       ---------------------- */
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 1.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    /* ----------------------
       Synched data
       ---------------------- */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLYING, false);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
        builder.define(DATA_HAS_PREY, false);
        builder.define(DATA_ATTACKING, false);
    }

    /* ----------------------
       Goals
       ---------------------- */
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 16.0F, 2.0F));
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
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Animal.class, false, (entity) -> this.shouldHunt() && isValidPrey(entity)));
        this.targetSelector.addGoal(7, new NonTameRandomTargetGoal<>(this, Parrot.class, false, (parrot) -> this.shouldHunt() && !((Parrot)parrot).isTame()));
    }

    private boolean isValidPrey(LivingEntity entity) {
        return entity.getType().is(ModTags.EntityTypes.PHILIPPINE_EAGLE_PREY);
    }

    private boolean shouldHunt() {
        return !this.isOrderedToSit();
    }

    /* ----------------------
       Tick / AI step
       ---------------------- */
    @Override
    public void tick() {
        super.tick();

        // handle flight stabilization
        if (isFlying()) {
            this.resetFallDistance();
            if (this.getDeltaMovement().y < 0 && !isCarryingPrey) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.9, 1.0));
            }

            if (this.getNavigation().isDone() && this.random.nextInt(80) == 0) {
                double dx = this.getX() + (this.random.nextDouble() - 0.5) * 12.0;
                double dy = this.getY() + (this.random.nextDouble() - 0.5) * 6.0;
                double dz = this.getZ() + (this.random.nextDouble() - 0.5) * 12.0;
                this.getNavigation().moveTo(dx, dy, dz, 1.0);
            }

            if (this.onGround() || (this.verticalCollisionBelow && !this.isHunting)) {
                stopFlying();
            }
        } else {
            if (!this.onGround() && !this.isInWater() && this.random.nextFloat() < 0.01F) {
                startFlying();
            }
        }

        // Hunting search if needed
        if (isHunting && (getTarget() == null || !getTarget().isAlive())) {
            List<Animal> possible = this.level().getEntitiesOfClass(
                    Animal.class,
                    this.getBoundingBox().inflate(huntingRange),
                    this::isValidPrey
            );

            Animal nearest = null;
            double nd = Double.MAX_VALUE;
            for (Animal a : possible) {
                double ds = this.distanceToSqr(a);
                if (ds < nd) {
                    nd = ds;
                    nearest = a;
                }
            }
            if (nearest != null) {
                this.setTarget(nearest);
            } else {
                isHunting = false;
            }
        }

        // timers & carry logic
        if (this.onGround()) groundTimer++; else groundTimer = 0;
        if (swoopCooldown > 0) swoopCooldown--;

        if (isCarryingPrey && preyTarget != null) {
            if (!preyTarget.isAlive() || preyTarget.isRemoved()) {
                dropPrey();
            } else {
                Vec3 p = this.position();
                preyTarget.setPos(p.x, p.y - 0.8, p.z);
                preyTarget.setDeltaMovement(this.getDeltaMovement());
            }
        }

        // sync client-visible flag
        this.entityData.set(DATA_HAS_PREY, isCarryingPrey);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // ensure we start flying if off-ground
        if (!this.onGround() && !this.isFlying() && !this.isInWater()) {
            this.startFlying();
        }

        // update anger server side
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }

        // slight upward stabilization while flying
        if (isFlying()) {
            this.fallDistance = 0;
            if (this.getDeltaMovement().y < 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.85, 1.0));
            }
        }
    }

    /* ----------------------
       NeutralMob (anger) implementation
       ---------------------- */
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
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

    /* ----------------------
       Interaction / taming
       ---------------------- */
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

    /* ----------------------
       Combat & prey handling
       ---------------------- */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        if (result && target instanceof LivingEntity living) {
            if (isValidPrey(living) && !isCarryingPrey && this.random.nextFloat() < 0.7f) {
                grabPrey(living);
            }
        }
        return result;
    }

    private void grabPrey(LivingEntity prey) {
        if (prey.isAlive() && !isCarryingPrey && !prey.isRemoved()) {
            this.preyTarget = prey;
            this.isCarryingPrey = true;
            prey.setNoGravity(true);
            prey.setSilent(true);
            // don't startRiding - we manually position prey each tick
            this.playSound(SoundEvents.GENERIC_EAT, 1.0f, 1.2f);
            if (this.random.nextFloat() < 0.3f) {
                prey.hurt(this.damageSources().mobAttack(this), 1000);
            }
        }
    }

    private void dropPrey() {
        if (preyTarget != null) {
            preyTarget.setNoGravity(false);
            preyTarget.setSilent(false);
            preyTarget = null;
        }
        isCarryingPrey = false;
    }

    /* ----------------------
       Sounds
       ---------------------- */
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    /* ----------------------
       Flight handling (SemiFlyer)
       ---------------------- */
    @Override
    public boolean isNoGravity() {
        return this.isFlying() || super.isNoGravity();
    }

    @Override
    public void startFlying() {
        if (this.isFlying()) return;
        setFlying(true);
        // give small lift
        Vec3 m = this.getDeltaMovement();
        this.setDeltaMovement(m.x, Math.max(m.y, 0.35), m.z);
    }

    @Override
    public void stopFlying() {
        if (!this.isFlying()) return;
        setFlying(false);
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
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
    public void setFlying(boolean flying) {
        boolean currently = isFlying();
        if (currently == flying) return;
        this.entityData.set(DATA_FLYING, flying);

        if (flying) {
            this.navigation = this.flyingNavigation;
            this.moveControl = new SemiFlyingMoveControl(this, 10, 5);
            this.setNoGravity(true);
            // slightly adjust ground speed attribute while flying
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2);
        } else {
            this.navigation = this.groundNavigation;
            this.moveControl = new MoveControl(this);
            this.setNoGravity(false);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        }
    }

    /* ----------------------
       Save / Load NBT
       ---------------------- */
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFlying", this.isFlying());
        compound.putBoolean("IsCarryingPrey", this.isCarryingPrey);
        compound.putInt("GroundTimer", this.groundTimer);
        compound.putInt("SwoopCooldown", this.swoopCooldown);
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(DATA_FLYING, compound.getBoolean("IsFlying"));
        this.isCarryingPrey = compound.getBoolean("IsCarryingPrey");
        this.groundTimer = compound.getInt("GroundTimer");
        this.swoopCooldown = compound.getInt("SwoopCooldown");
        this.readPersistentAngerSaveData(this.level(), compound);
    }

    /* ----------------------
       Breeding
       ---------------------- */
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        PhilippineEagleEntity baby = ModEntities.PHILIPPINE_EAGLE.get().create(level);
        if (baby != null) {
            UUID owner = this.getOwnerUUID();
            if (owner != null) {
                baby.setOwnerUUID(owner);
                baby.setTame(true, true);
            }
        }
        return baby;
    }

    /* ----------------------
       Swoop attack goal
       ---------------------- */
    private class SwoopAttackGoal extends Goal {
        private LivingEntity target;
        private int phase = 0;

        public SwoopAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (swoopCooldown > 0) return false;
            target = getTarget();
            if (target == null) return false;
            if (!isFlying()) return false;
            double dsq = distanceToSqr(target);
            return dsq < 120.0 && getY() > target.getY() + 2.0 && random.nextInt(50) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && phase < 3;
        }

        @Override
        public void start() {
            phase = 0;
            setFlying(true);
            setAttacking(true);
        }

        @Override
        public void stop() {
            target = null;
            phase = 0;
            swoopCooldown = 120;
            setAttacking(false);
        }

        @Override
        public void tick() {
            if (target == null) return;
            switch (phase) {
                case 0: // climb if needed
                    if (getY() < target.getY() + 10.0) {
                        setDeltaMovement(getDeltaMovement().add(0, 0.15, 0));
                    } else {
                        phase = 1;
                    }
                    break;
                case 1: // dive
                    Vec3 to = target.position().subtract(position()).normalize();
                    Vec3 dive = to.add(0, -0.4, 0).normalize().scale(getAttributeValue(Attributes.FLYING_SPEED));
                    setDeltaMovement(dive);
                    if (distanceToSqr(target) < 6.0) {
                        doHurtTarget(target);
                        if (isValidPrey(target)) grabPrey(target);
                        phase = 2;
                    }
                    break;
                case 2: // recover
                    setDeltaMovement(getDeltaMovement().add(0, 0.3, 0));
                    phase = 3;
                    break;
            }
        }
    }

    private void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    /* ----------------------
       Misc helpers
       ---------------------- */
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    public boolean isCarryingPrey() {
        return this.isCarryingPrey;
    }

    public boolean hasVisiblePrey() {
        return this.entityData.get(DATA_HAS_PREY);
    }

    // neutral mob helpers are handled by NeutralMob interface default methods in vanilla code

}
