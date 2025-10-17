package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class MantisEntity extends TamableAnimal implements NeutralMob {

    private static final EntityDataAccessor<Boolean> DATA_IS_WAITING = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_GRABBING = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_EATING = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_HELD_PREY_ID = SynchedEntityData.defineId(MantisEntity.class, EntityDataSerializers.INT);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int persistentAngerTime;
    private UUID angerTarget;
    private LivingEntity heldPrey;
    private int eatPreyCooldown = 0;
    private int grabCooldown = 0;

    public MantisEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_IS_WAITING, false);
        builder.define(DATA_IS_GRABBING, false);
        builder.define(DATA_IS_EATING, false);
        builder.define(DATA_HELD_PREY_ID, -1);
    }

    public SpawnGroupData finalizeSpawn(ServerLevel level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, stack -> stack.is(ModTags.Items.MANTIS_FOOD), false));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 10, 2));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Target goals
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new GrabAndEatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false, this::canAttackAsPrey));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Spider.class, true));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.1D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }

        if (grabCooldown > 0) grabCooldown--;

        // Handle held prey
        if (heldPrey != null && heldPrey.isAlive()) {
            this.getLookControl().setLookAt(heldPrey);
            this.getNavigation().stop();

            // Keep prey close to mantis
            if (heldPrey.distanceToSqr(this) > 4.0) {
                Vec3 mantisPos = this.position();
                heldPrey.teleportTo(mantisPos.x, mantisPos.y, mantisPos.z);
            }

            if (--eatPreyCooldown <= 0) {
                eatPreyCooldown = 40; // 2 seconds
                heldPrey.hurt(this.damageSources().mobAttack(this), 2.0F);
                this.playSound(this.getMantisEatingSound(), 1.0F, 1.0F + (this.random.nextFloat() - 0.5F) * 0.2F);
                triggerEatAnimation();

                // Add eating particles
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT,
                            heldPrey.getX(), heldPrey.getY() + 0.5, heldPrey.getZ(),
                            5, 0.3, 0.3, 0.3, 0.1);
                }

                if (!heldPrey.isAlive()) {
                    this.onPreyKilled();
                }
            }
        } else {
            setEating(false);
        }

        setGrabbing(false);

        if (!this.level().isClientSide && !this.hasPrey() && this.getTarget() == null && this.random.nextInt(600) == 0) {
            setWaiting(true);
        }

        if (isWaiting() && (this.getTarget() != null || this.hasPrey())) {
            setWaiting(false);
        }

        // (Optional) visual feedback for camouflage
        if (this.level().isClientSide && isWaiting()) {
            this.level().addParticle(ParticleTypes.GLOW, this.getX(), this.getY() + 1, this.getZ(), 0, 0.01, 0);
        }
    }

    private void onPreyKilled() {
        heldPrey = null;
        setAggressive(false);
        setEating(false);
        this.entityData.set(DATA_HELD_PREY_ID, -1);
    }

    private void triggerEatAnimation() {
        setEating(true);
        // You can add more animation logic here
    }

    private SoundEvent getMantisEatingSound() {
        // Return your custom sound or a vanilla alternative
        return SoundEvents.GENERIC_EAT; // Replace with ModSounds.MANTIS_EAT when you create it
    }

    public boolean canAttackAsPrey(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) return false;
        if (entity instanceof MantisEntity) return false;

        // Spiders are valid prey
        if (entity instanceof Spider) return true;

        // Other tagged prey
        return entity.getType().is(ModTags.EntityTypes.MANTIS_PREY) &&
                entity.getBbWidth() <= 1.5F && entity.getBbHeight() <= 1.5F;
    }


    public void setHeldPrey(@Nullable LivingEntity entity) {
        this.heldPrey = entity;
        if (entity != null) {
            this.entityData.set(DATA_HELD_PREY_ID, entity.getId());
            setEating(true);
        } else {
            this.entityData.set(DATA_HELD_PREY_ID, -1);
            setEating(false);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ModTags.Items.MANTIS_FOOD) && !this.isTame()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            // 33% chance to tame
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public boolean hasPrey() {
        return heldPrey != null && heldPrey.isAlive();
    }

    public LivingEntity getHeldPrey() {
        return heldPrey;
    }

    public boolean isGrabbing() {
        return this.entityData.get(DATA_IS_GRABBING);
    }

    public void setGrabbing(boolean grabbing) {
        this.entityData.set(DATA_IS_GRABBING, grabbing);
    }

    public boolean isEating() {
        return this.entityData.get(DATA_IS_EATING);
    }

    public void setEating(boolean eating) {
        this.entityData.set(DATA_IS_EATING, eating);
    }

    public boolean isWaiting() {
        return this.entityData.get(DATA_IS_WAITING);
    }

    public void setWaiting(boolean waiting) {
        this.entityData.set(DATA_IS_EATING, waiting);
    }

    @Override
    public boolean isTame() {
        return super.isTame();
    }

    @Override
    public boolean isOrderedToSit() {
        return super.isOrderedToSit();
    }

    @Override
    public boolean isInSittingPose() {
        return super.isInSittingPose();
    }

    @Override
    public boolean canMate(@NotNull Animal other) {
        return !(this.isEating() || this.isInSittingPose());
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.Items.MANTIS_FOOD);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.MANTIS.get().create(level);
    }


    // Persistent anger system
    @Override
    public int getRemainingPersistentAngerTime() {
        return this.persistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.persistentAngerTime = time;
    }

    @Override
    public @Nullable UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.angerTarget = uuid;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.persistentAngerTime = PERSISTENT_ANGER_TIME.sample(this.random);
    }

    @Override
    public boolean isAggressive() {
        return this.hasPrey() || this.getRemainingPersistentAngerTime() > 0;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }

        // Drop prey if hurt
        if (this.hasPrey()) {
            this.setHeldPrey(null);
        }

        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AngerTime", this.persistentAngerTime);
        compound.putInt("EatPreyCooldown", this.eatPreyCooldown);
        compound.putInt("GrabCooldown", this.grabCooldown);

        if (this.angerTarget != null) {
            compound.putUUID("AngerTarget", this.angerTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.persistentAngerTime = compound.getInt("AngerTime");
        this.eatPreyCooldown = compound.getInt("EatPreyCooldown");
        this.grabCooldown = compound.getInt("GrabCooldown");

        if (compound.hasUUID("AngerTarget")) {
            this.angerTarget = compound.getUUID("AngerTarget");
        }
    }

    static class GrabAndEatGoal extends TargetGoal {
        private final MantisEntity mantis;
        private LivingEntity target;

        public GrabAndEatGoal(MantisEntity mantis) {
            super(mantis, false);
            this.mantis = mantis;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (mantis.hasPrey()) return false;
            this.target = mantis.getTarget();
            return target != null && target.isAlive() && mantis.canAttackAsPrey(target);
        }

        @Override
        public void start() {
            mantis.getNavigation().moveTo(target, 1.2);
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) {
                this.stop();
                return;
            }

            mantis.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distance = mantis.distanceToSqr(target);
            if (distance < 2.0 && !mantis.hasPrey()) {
                // Immediately grab prey
                mantis.setHeldPrey(target);
                mantis.setAggressive(true);
                mantis.setTarget(null);

                // Play grab sound + particles
                mantis.playSound(SoundEvents.SPIDER_STEP, 1.0F, 1.2F);
                if (mantis.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                            target.getX(), target.getY() + 0.5, target.getZ(),
                            5, 0.3, 0.3, 0.3, 0.1);
                }
            } else {
                // Keep chasing until in range
                mantis.getNavigation().moveTo(target, 1.2);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !mantis.hasPrey() && target != null && target.isAlive();
        }

        @Override
        public void stop() {
            mantis.getNavigation().stop();
            target = null;
        }
    }

}