package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.AbstractRoarGoal;
import com.fungoussoup.ancienthorizons.entity.client.maip.MaipAnimations;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.client.animation.AnimationDefinition;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class MaipEntity extends TamableAnimal implements Saddleable, NeutralMob {
    private static final EntityDataAccessor<Boolean> MAIP_SADDLED = SynchedEntityData.defineId(MaipEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> MAIP_SITTING = SynchedEntityData.defineId(MaipEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLLAR_COLOR_ID = SynchedEntityData.defineId(MaipEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SIZE_VARIANT = SynchedEntityData.defineId(MaipEntity.class, EntityDataSerializers.INT);

    public AnimationState roarAnimationState = new AnimationState();
    private int roarAnimationTimeout = 0;
    public AnimationState attackSmallAnimationState = new AnimationState();
    public AnimationState attackMediumAnimationState = new AnimationState();
    public AnimationState attackOneLargeAnimationState = new AnimationState();
    public AnimationState attackTwoLargeAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;
    public AnimationState sitAnimationState = new AnimationState();

    private int warningSoundTicks;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    private AnimationDefinition currentAnimation = null;
    private int mountAttackCooldown = 0;

    public MaipEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MAIP_SADDLED, false);
        builder.define(MAIP_SITTING, false);
        builder.define(COLLAR_COLOR_ID, DyeColor.RED.getId());
        builder.define(SIZE_VARIANT, 1); // 0=small, 1=medium, 2=large
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MaipMeleeAttackGoal());
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new MaipHurtByTargetGoal());
        this.targetSelector.addGoal(2, new MaipRoarAggroGoal(this, 1.0F, 20, 40));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(this, Animal.class, false,
                target -> target.getType().is(ModTags.EntityTypes.MAIP_PREY)));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                e -> e.getType().is(ModTags.EntityTypes.MAIP_ENEMIES)));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, this::isAngryAt));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (warningSoundTicks > 0) warningSoundTicks--;
        if (mountAttackCooldown > 0) mountAttackCooldown--;
        if (remainingPersistentAngerTime > 0) remainingPersistentAngerTime--;

        if (!level().isClientSide) {
            updatePersistentAnger((ServerLevel) level(), true);
        }

        setupAnimationStates();
    }

    private void setupAnimationStates() {
        if (isSitting()) {
            sitAnimationState.start(tickCount);
        } else {
            sitAnimationState.stop();
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.canBeControlledByRider()) {
            LivingEntity rider = (LivingEntity) this.getControllingPassenger();
            if (rider != null) {
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;

                float strafe = rider.xxa * 0.5F;
                float forward = rider.zza;

                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }

                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(strafe, travelVector.y, forward));
            }
        } else {
            super.travel(travelVector);
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player && this.isTame() && this.isOwnedBy(player)) {
            return player;
        }
        return null;
    }

    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() != null && this.isSaddled();
    }

    public void performMountAttack() {
        if (mountAttackCooldown > 0) return;

        LivingEntity target = findNearestTarget();
        if (target != null && this.distanceTo(target) < 4.0D) {
            performAttackBySize();
            this.doHurtTarget(target);
            mountAttackCooldown = 20;
        }
    }

    private LivingEntity findNearestTarget() {
        return this.level().getNearestEntity(
                LivingEntity.class,
                net.minecraft.world.entity.ai.targeting.TargetingConditions.forCombat().range(4.0D),
                this,
                this.getX(), this.getEyeY(), this.getZ(),
                this.getBoundingBox().inflate(4.0D)
        );
    }

    private void performAttackBySize() {
        int size = getSizeVariant();
        switch (size) {
            case 0: // Small
                this.setAnimation(MaipAnimations.ATTACK_S);
                break;
            case 1: // Medium
                this.setAnimation(MaipAnimations.ATTACK_M);
                break;
            case 2: // Large
                this.setAnimation(MaipAnimations.ATTACK_L);
                break;
        }
        this.playSound(ModSoundEvents.MAIP_ATTACK, 1.0F, 1.0F);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level().isClientSide) {
            boolean flag = isOwnedBy(player) || isTame() || (stack.is(ItemTags.MEAT) && !isTame() && !isAngry());
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        // Taming
        if (stack.is(ItemTags.MEAT) && !isTame()) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (random.nextInt(5) == 0) {
                tame(player);
                navigation.stop();
                setTarget(null);
                setOrderedToSit(true);
                level().broadcastEntityEvent(this, (byte) 7);
            } else {
                level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        // Saddling
        if (isTame() && stack.is(Items.SADDLE) && !isSaddled()) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            setSaddled(true);
            this.playSound(SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        // Collar dyeing
        if (isTame() && stack.getItem() instanceof DyeItem dye) {
            if (isOwnedBy(player) && dye.getDyeColor() != getCollarColor()) {
                setCollarColor(dye.getDyeColor());
                stack.consume(1, player);
                return InteractionResult.SUCCESS;
            }
        }

        // Mounting
        if (isTame() && isOwnedBy(player) && isSaddled() && !isBaby()) {
            if (!this.isVehicle()) {
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        // Sitting toggle
        if (isOwnedBy(player) && !isFood(stack) && !this.isVehicle()) {
            setOrderedToSit(!isOrderedToSit());
            jumping = false;
            navigation.stop();
            setTarget(null);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.MAIP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.MAIP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.MAIP_DEATH;
    }

    public void setAnimation(@Nullable AnimationDefinition anim) {
        this.currentAnimation = anim;

        this.roarAnimationState.stop();
        this.attackSmallAnimationState.stop();
        this.attackMediumAnimationState.stop();
        this.attackOneLargeAnimationState.stop();
        this.attackTwoLargeAnimationState.stop();

        // Start appropriate animation based on anim definition
        if (anim != null) {
            if (anim == MaipAnimations.ROAR) {
                this.roarAnimationState.start(this.tickCount);
            } else if (anim == MaipAnimations.ATTACK_S) {
                this.attackSmallAnimationState.start(this.tickCount);
            } else if (anim == MaipAnimations.ATTACK_M) {
                this.attackMediumAnimationState.start(this.tickCount);
            } else if (anim == MaipAnimations.ATTACK_L) {
                if (random.nextBoolean()) {
                    this.attackOneLargeAnimationState.start(this.tickCount);
                } else {
                    this.attackTwoLargeAnimationState.start(this.tickCount);
                }
            }
        }
    }

    // Saddleable implementation
    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTame();
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundSource) {
        setSaddled(true);
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(MAIP_SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(MAIP_SADDLED, saddled);
    }

    // TamableAnimal overrides
    @Override
    public boolean isOrderedToSit() {
        return this.entityData.get(MAIP_SITTING);
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(MAIP_SITTING, sitting);
    }

    public boolean isSitting() {
        return this.entityData.get(MAIP_SITTING);
    }

    public DyeColor getCollarColor() {
        int id = this.entityData.get(COLLAR_COLOR_ID);
        return DyeColor.byId(Mth.clamp(id, 0, DyeColor.values().length - 1));
    }

    public void setCollarColor(DyeColor color) {
        if (color != null) {
            this.entityData.set(COLLAR_COLOR_ID, color.getId());
        }
    }

    public int getSizeVariant() {
        return this.entityData.get(SIZE_VARIANT);
    }

    public void setSizeVariant(int size) {
        this.entityData.set(SIZE_VARIANT, Mth.clamp(size, 0, 2));
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isAngry();
    }

    // NeutralMob implementation
    public boolean isAngry() {
        return persistentAngerTarget != null;
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

    // NBT Save/Load
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
        tag.putBoolean("Saddled", this.isSaddled());
        tag.putBoolean("Sitting", this.isSitting());
        tag.putInt("CollarColor", this.getCollarColor().getId());
        tag.putInt("SizeVariant", this.getSizeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
        this.setSaddled(tag.getBoolean("Saddled"));
        this.setOrderedToSit(tag.getBoolean("Sitting"));
        this.setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        this.setSizeVariant(tag.getInt("SizeVariant"));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        MaipEntity baby = ModEntities.MAIP.get().create(level);
        if (baby != null && this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
            baby.setCollarColor(DyeColor.values()[level.getRandom().nextInt(DyeColor.values().length)]);

            // Inherit size variant with slight variation
            int parentSize = this.getSizeVariant();
            if (level.getRandom().nextInt(10) == 0) {
                parentSize = level.getRandom().nextInt(3);
            }
            baby.setSizeVariant(parentSize);
        }
        return baby;
    }

    @Override
    public float getAgeScale() {
        float baseScale = super.getAgeScale();
        int size = getSizeVariant();
        return baseScale * (0.8f + (size * 0.2f)); // Small: 0.8x, Medium: 1.0x, Large: 1.2x
    }

    // AI Goals
    class MaipMeleeAttackGoal extends MeleeAttackGoal {
        public MaipMeleeAttackGoal() {
            super(MaipEntity.this, 1.25F, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.canPerformAttack(target)) {
                this.resetAttackCooldown();
                MaipEntity.this.performAttackBySize();
                this.mob.doHurtTarget(target);
            } else if (this.mob.distanceToSqr(target) < (double)((target.getBbWidth() + 3.0F) * (target.getBbWidth() + 3.0F))) {
                if (this.isTimeToAttack()) {
                    this.resetAttackCooldown();
                }
            } else {
                this.resetAttackCooldown();
            }
        }

        @Override
        public boolean canUse() {
            return !MaipEntity.this.isVehicle() && super.canUse();
        }
    }

    public static class MaipRoarAggroGoal extends AbstractRoarGoal {
        private final MaipEntity maip;
        private final float roarVolume;
        private final int minRoarInterval;
        private final int maxRoarInterval;
        private int roarCooldown;
        private LivingEntity lastAttacker;

        public MaipRoarAggroGoal(MaipEntity maip, float roarVolume, int minRoarInterval, int maxRoarInterval) {
            super(maip, roarVolume, minRoarInterval, maxRoarInterval);
            this.maip = maip;
            this.roarVolume = roarVolume;
            this.minRoarInterval = minRoarInterval;
            this.maxRoarInterval = maxRoarInterval;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity attacker = maip.getLastHurtByMob();
            return attacker != null && attacker.isAlive() && !maip.isBaby() && roarCooldown <= 0;
        }

        @Override
        public void start() {
            lastAttacker = maip.getLastHurtByMob();
            if (maip.roarAnimationState != null) {
                maip.roarAnimationState.start(maip.tickCount);
            }
            maip.playSound(ModSoundEvents.MAIP_ROAR, roarVolume, 1.0F);
            roarCooldown = Mth.nextInt(maip.getRandom(), minRoarInterval, maxRoarInterval);
        }

        @Override
        public void tick() {
            if (roarCooldown > 0) {
                roarCooldown--;
            }

            if (roarCooldown == 0 && lastAttacker != null) {
                if (lastAttacker.isAlive()) {
                    maip.setTarget(lastAttacker);
                }
                lastAttacker = null;
            }
        }
    }

    class MaipHurtByTargetGoal extends HurtByTargetGoal {
        public MaipHurtByTargetGoal() {
            super(MaipEntity.this);
        }

        @Override
        public void start() {
            super.start();
            if (MaipEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity target) {
            if (mob instanceof MaipEntity && !mob.isBaby()) {
                super.alertOther(mob, target);
            }
        }
    }
}