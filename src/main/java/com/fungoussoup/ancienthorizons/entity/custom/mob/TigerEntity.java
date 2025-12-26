package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ai.AbstractRoarGoal;
import com.fungoussoup.ancienthorizons.entity.ai.BigCatSleepGoal;
import com.fungoussoup.ancienthorizons.entity.ai.BigCatYawnGoal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SleepingAnimal;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.entity.util.TigerVariant;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.goal.target.*;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.tags.ItemTags;

import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static com.fungoussoup.ancienthorizons.entity.client.tiger.TigerAnimations.*;
import static net.minecraft.world.item.Items.*;

public class TigerEntity extends TamableAnimal implements NeutralMob, VariantHolder<TigerVariant>, SleepingAnimal {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TIGER_SLEEPING = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TIGER_YAWNING = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TIGER_SITTING = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLLAR_COLOR_ID = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_STEALTH = SynchedEntityData.defineId(TigerEntity.class, EntityDataSerializers.BOOLEAN);

    public AnimationState sleepAnimationState = new AnimationState();
    private int sleepAnimationTimeout = 0;
    public AnimationState roarAnimationState = new AnimationState();
    private int roarAnimationTimeout = 0;
    public AnimationState attackAnimationState = new AnimationState();
    private int attackAnimationTimeout = 0;
    public AnimationState yawnAnimationState = new AnimationState();
    private int yawnAnimationTimeout = 0;
    public AnimationState sitAnimationState = new AnimationState();
    public int sitAnimationTimeout = 0;

    private int warningSoundTicks;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public final AnimationState angryAnimationState = new AnimationState();
    private int angryAnimationTimeout = 0;
    private AnimationDefinition currentAnimation = null;

    public TigerEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(TIGER_SLEEPING, false);
        builder.define(TIGER_YAWNING, false);
        builder.define(TIGER_SITTING, false);
        builder.define(COLLAR_COLOR_ID, DyeColor.RED.getId());
        builder.define(IS_STEALTH, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        if (!level().isClientSide) setOrderedToSit(false);
        return super.hurt(source, amount);
    }

    public boolean isAngry() { return persistentAngerTarget != null; }


    @Override public void startPersistentAngerTimer() { setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(random)); }
    @Override public void setRemainingPersistentAngerTime(int time) { remainingPersistentAngerTime = time; }
    @Override public int getRemainingPersistentAngerTime() { return remainingPersistentAngerTime; }
    @Override public void setPersistentAngerTarget(@Nullable UUID target) { persistentAngerTarget = target; }
    @Nullable @Override public UUID getPersistentAngerTarget() { return persistentAngerTarget; }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT) || stack.is(ROTTEN_FLESH);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TigerSleepGoal(this));
        goalSelector.addGoal(2, new TigerYawnGoal(this));
        goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(4, new BreedGoal(this, 1.1D));
        goalSelector.addGoal(5, new TigerRoarGoal(this, 1.0f, 40, 80));
        goalSelector.addGoal(6, new TigerMeleeAttackGoal());
        goalSelector.addGoal(7, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        goalSelector.addGoal(8, new FollowParentGoal(this, 1.0D));
        goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new TigerHurtByTargetGoal());
        targetSelector.addGoal(2, new TigerRoarAggroGoal(this, 1.0F, 20, 40));
        targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, Animal.class, false, target -> target.getType().is(ModTags.EntityTypes.TIGER_PREY)));
        targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new TigerAttackPlayersGoal());
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, e -> e.getType().is(ModTags.EntityTypes.TIGER_ENEMIES)));
        targetSelector.addGoal(4, new NonTameRandomTargetGoal<>(this, LivingEntity.class, true, this::isAngryAt));
        targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, false));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TigerVariant", getVariant().getId());
        tag.putBoolean("Sleeping", isSleeping());
        tag.putBoolean("Yawning", isYawning());
        tag.putBoolean("Sitting", isSitting());
        tag.putInt("CollarColor", getCollarColor().getId());
        if (persistentAngerTarget != null) tag.putUUID("AngerTarget", persistentAngerTarget);
        tag.putInt("AngerTime", remainingPersistentAngerTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(TigerVariant.byID(tag.getInt("TigerVariant")));
        setSleeping(tag.getBoolean("Sleeping"));
        setYawning(tag.getBoolean("Yawning"));
        setOrderedToSit(tag.getBoolean("Sitting"));
        setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        if (tag.hasUUID("AngerTarget")) persistentAngerTarget = tag.getUUID("AngerTarget");
        remainingPersistentAngerTime = tag.getInt("AngerTime");
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomSource = (level instanceof ServerLevel) ? level.getRandom() : RandomSource.create(new Random().nextLong());
        TigerVariant variant = getRandomVariant(randomSource);
        setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    private TigerVariant getRandomVariant(RandomSource random) {
        int i = random.nextInt(1000);
        if (i <= 5) {
            return TigerVariant.BLUE;      // Rarest (0.5%)
        } else if (i <= 45) {
            return TigerVariant.GOLDEN;    // Rare (4.5%)
        } else if (i <= 150) {
            return TigerVariant.WHITE;     // Uncommon (15%)
        } else {
            return TigerVariant.NORMAL;    // Common (80%)
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (isAngry() && !isBaby()) return ModSoundEvents.TIGER_ANGRY;
        return isBaby() ? ModSoundEvents.TIGER_AMBIENT_BABY : ModSoundEvents.TIGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return ModSoundEvents.TIGER_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return ModSoundEvents.TIGER_DEATH; }
    @Override
    protected void playStepSound(BlockPos pos, BlockState block) { playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F); }


    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(ModSoundEvents.TIGER_WARNING, 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
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
                setSleeping(false);
                setOrderedToSit(true);
                level().broadcastEntityEvent(this, (byte) 7);
            } else {
                level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        // Feeding
        if (isTame() && isFood(stack) && getHealth() < getMaxHealth()) {
            FoodProperties food = stack.getFoodProperties(this);
            float heal = (food != null ? food.nutrition() : 1.0F);
            if (!stack.is(Items.ROTTEN_FLESH)) heal *= 3.0F;
            heal(heal);
            stack.consume(1, player);
            gameEvent(GameEvent.EAT);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        // Collar dyeing
        if (isTame() && stack.getItem() instanceof DyeItem dye) {
            if (isOwnedBy(player) && dye.getDyeColor() != getCollarColor()) {
                setCollarColor(dye.getDyeColor());
                stack.consume(1, player);
                return InteractionResult.SUCCESS;
            }
        }

        // Sitting toggle
        if (isOwnedBy(player) && !isFood(stack)) {
            setOrderedToSit(!isOrderedToSit());
            jumping = false;
            navigation.stop();
            setTarget(null);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isOrderedToSit() {
        return this.entityData.get(TIGER_SITTING);
    }

    @Override
    public void setOrderedToSit(boolean sitting) {
        this.entityData.set(TIGER_SITTING, sitting);
    }

    public void setSitting(boolean sitting) {
        this.setOrderedToSit(sitting);
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

    public boolean canBeLeashed() {
        return !this.isAngry();
    }

    public boolean isSitting() {
        return this.entityData.get(TIGER_SITTING);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(COLLAR_COLOR_ID)) {
            this.getCollarColor();
        }
    }

    private void tickAnimationTimeouts() {
        if (angryAnimationTimeout > 0) angryAnimationTimeout--;
        if (sleepAnimationTimeout > 0) sleepAnimationTimeout--;
        if (yawnAnimationTimeout > 0) yawnAnimationTimeout--;
    }

    private void setupAnimationStates() {
        if (isAggressive()) angryAnimationState.start(tickCount); else angryAnimationState.stop();

        if (isSleeping() && sleepAnimationTimeout <= 0) {
            sleepAnimationState.start(tickCount); sleepAnimationTimeout = 200 + random.nextInt(200);
        }
        if (isYawning() && yawnAnimationTimeout <= 0) {
            yawnAnimationState.start(tickCount); yawnAnimationTimeout = 60;
        }
        if (isSitting()) sitAnimationState.start(tickCount); else sitAnimationState.stop();
    }

    private AnimationDefinition getAnimation() {
        return currentAnimation;
    }

    public void setAnimation(@Nullable AnimationDefinition anim) {
        this.currentAnimation = anim;

        // Stop all animation states first
        this.roarAnimationState.stop();
        this.attackAnimationState.stop();
        this.sleepAnimationState.stop();
        this.yawnAnimationState.stop();
        this.angryAnimationState.stop();

        // Start the appropriate animation state
        if (anim == TIGER_ROAR || anim == TIGER_ROAR2) {
            this.roarAnimationState.start(this.tickCount);
        } else if (anim == TIGER_PAW_SWIPE) {
            this.attackAnimationState.start(this.tickCount);
        } else if (anim == TIGER_SLEEP) {
            this.sleepAnimationState.start(this.tickCount);
        } else if (anim == TIGER_YAWN) {
            this.yawnAnimationState.start(this.tickCount);
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isTame() && isOrderedToSit()) navigation.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (warningSoundTicks > 0) warningSoundTicks--;
        tickAnimationTimeouts();
        if (isTame() && getHealth() < getMaxHealth() && tickCount % 100 == 0) heal(1.0F);
        if (!level().isClientSide) updatePersistentAnger((ServerLevel) level(), true);
        setupAnimationStates();
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        TigerEntity baby = ModEntities.TIGER.get().create(level);
        if (baby != null) {
            // Inherit variant from parents with slight mutation chance
            TigerVariant babyVariant = inheritVariant((TigerEntity) otherParent);
            baby.setVariant(babyVariant);

            // Inherit taming from parents
            if (this.isTame()) {
                baby.setOwnerUUID(this.getOwnerUUID());
                baby.setTame(true, true);
            }

            // Random collar color for tamed babies
            if (baby.isTame()) {
                baby.setCollarColor(DyeColor.values()[level.getRandom().nextInt(DyeColor.values().length)]);
            }
        }
        return baby;
    }

    private TigerVariant inheritVariant(TigerEntity otherParent) {
        TigerVariant thisVariant = this.getVariant();
        TigerVariant otherVariant = otherParent.getVariant();

        // 5% chance for random mutation
        if (this.random.nextInt(100) < 5) {
            return getRandomVariant(this.random);
        }

        // 70% chance to inherit from this parent, 30% from other parent
        if (this.random.nextInt(100) < 70) {
            return thisVariant;
        } else {
            return otherVariant;
        }
    }

    public void doSitTick() {
        if (this.isSitting()) {
            this.setAnimation(TIGER_SIT);
            this.setStealth(false);
            this.setRunning(false);
        }
    }

    public boolean isInCombat() {
        return this.getTarget() != null && this.getTarget().isAlive();
    }

    public boolean canPerformIdleAnimation() {
        return !this.isBaby() && !this.isInCombat() && !this.isRunning() && !this.isStealth();
    }

    public void doPounceTick() {
        if (this.isBaby()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.25D, 0));
            this.setJumping(true);
        } else {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                Vec3 direction = target.position().subtract(this.position()).normalize();
                double distance = this.distanceTo(target);

                // Scale leap strength based on distance
                double leapStrength = Math.min(1.2D, 0.6D + (distance * 0.1D));
                double yBoost = Math.min(0.8D, 0.4D + (distance * 00.05D));

                this.setDeltaMovement(
                        direction.x * leapStrength,
                        yBoost,
                        direction.z * leapStrength
                );

                this.setAnimation(TIGER_POUNCE);
                this.setRunning(false);
                this.setStealth(false);
                this.playSound(ModSoundEvents.TIGER_ATTACK, 1.0F, 1.0F);
                this.setJumping(true);
            }
        }
    }

    public void doPawTick() {
        if (this.isBaby()) {
            this.setAnimation(TIGER_PAW_SWIPE);
        } else {
            this.setPawing(true);
            this.setAnimation(TIGER_PAW_SWIPE);
            this.playSound(ModSoundEvents.TIGER_ATTACK, 1.0F, 1.0F);
        }
    }

    public void setLyingDown(boolean b) {
        // Logic to set the lying-down state of the tiger (left as a stub)
    }

    public void setPawing(boolean b) {
        if (b) {
            this.setAnimation(TIGER_PAW_SWIPE);
        } else {
            this.setAnimation(null);
        }
    }

    public int getSleepAnimationTimeout() {
        return sleepAnimationTimeout;
    }

    public void setSleepAnimationTimeout(int sleepAnimationTimeout) {
        this.sleepAnimationTimeout = sleepAnimationTimeout;
    }

    public int getRoarAnimationTimeout() {
        return roarAnimationTimeout;
    }

    public void setRoarAnimationTimeout(int playAnimationTimeout) {
        this.roarAnimationTimeout = playAnimationTimeout;
    }

    public AnimationState getAttackAnimationState() {
        return attackAnimationState;
    }

    public int getAttackAnimationTimeout() {
        return attackAnimationTimeout;
    }

    public void setAttackAnimationTimeout(int attackAnimationTimeout) {
        this.attackAnimationTimeout = attackAnimationTimeout;
    }

    public AnimationState getYawnAnimationState() {
        return yawnAnimationState;
    }

    public int getYawnAnimationTimeout() {
        return yawnAnimationTimeout;
    }

    public int getAngryAnimationTimeout() {
        return angryAnimationTimeout;
    }

    public void setAngryAnimationTimeout(int angryAnimationTimeout) {
        this.angryAnimationTimeout = angryAnimationTimeout;
    }

    @Override
    public TigerVariant getVariant() {
        int variantId = this.entityData.get(VARIANT);
        return TigerVariant.byID(variantId);
    }

    @Override
    public void setVariant(TigerVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    class TigerAttackPlayersGoal extends NonTameRandomTargetGoal<Player> {
        public TigerAttackPlayersGoal() {
            super(TigerEntity.this, Player.class, true, null);
        }

        @Override
        public boolean canUse() {
            if (!TigerEntity.this.isBaby() && super.canUse()) {
                return TigerEntity.this.level().getEntitiesOfClass(TigerEntity.class,
                                TigerEntity.this.getBoundingBox().inflate(8.0, 4.0, 8.0))
                        .stream().anyMatch(TigerEntity::isBaby);
            }
            return false;
        }


        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5;
        }
    }

    public boolean isRunning() { return currentAnimation == TIGER_RUN || currentAnimation == TIGER_RUN_ANGRY; }

    public boolean isStealth() { return entityData.get(IS_STEALTH); }
    public void setStealth(boolean stealth) { entityData.set(IS_STEALTH, stealth); }

    class TigerMeleeAttackGoal extends MeleeAttackGoal {
        public TigerMeleeAttackGoal() {
            super(TigerEntity.this, 1.25F, true);
        }

        protected void checkAndPerformAttack(LivingEntity target) {
            if (this.canPerformAttack(target)) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(target);
            } else if (this.mob.distanceToSqr(target) < (double)((target.getBbWidth() + 3.0F) * (target.getBbWidth() + 3.0F))) {
                if (this.isTimeToAttack()) {
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    TigerEntity.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
            }

        }

        public void stop() {
            super.stop();
        }
    }

    private static class TigerYawnGoal extends BigCatYawnGoal<TigerEntity> {
        private final TigerEntity tiger;
        private int yawnTicks;

        public TigerYawnGoal(TigerEntity tiger) {
            super(tiger);
            this.tiger = tiger;
        }

        @Override
        public boolean canUse() {
            return !tiger.isStealth()
                    && !tiger.isRunning()
                    && !tiger.isYawning()
                    && !tiger.isSleeping()
                    && super.canUse();
        }

        @Override
        protected void playYawnSound() {
            tiger.playSound(ModSoundEvents.TIGER_YAWN, 1.0F, 1.0F);
        }

        @Override
        protected void setYawning(boolean yawning) {
            tiger.setYawning(yawning);
        }

        @Override
        public void start() {
            super.start();
            yawnTicks = 60;
            tiger.setYawning(true);
        }

        @Override
        public void tick() {
            if (yawnTicks > 0) yawnTicks--;
            else if (tiger.isYawning()) tiger.setYawning(false);
        }
    }


    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = super.doHurtTarget(target);
        if (flag && this.isTame()) {
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            target.hurt(this.damageSources().mobAttack(this), damage * 0.2F); // extra 20%
        }
        return flag;
    }

    public static class TigerRoarGoal extends AbstractRoarGoal {
        private final TigerEntity tiger;
        private final float roarVolume;
        private final int minRoarInterval;
        private final int maxRoarInterval;
        private int roarCooldown;
        private AnimationDefinition tigerRoar;

        public TigerRoarGoal(TigerEntity tiger, float roarVolume, int minRoarInterval, int maxRoarInterval) {
            super(tiger, roarVolume, minRoarInterval, maxRoarInterval);
            this.tiger = tiger;
            this.roarVolume = roarVolume;
            this.minRoarInterval = minRoarInterval;
            this.maxRoarInterval = maxRoarInterval;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public AnimationDefinition getTigerRoar() {
            return tigerRoar;
        }

        public void setTigerRoar(AnimationDefinition tigerRoar) {
            this.tigerRoar = tigerRoar;
        }

        @Override
        public boolean canUse() {
            return !tiger.isBaby() && !tiger.isStealth() && !tiger.isRunning() && roarCooldown <= 0;
        }

        @Override
        public void start() {
            setAnimation();
            tiger.playSound(tiger.getRoarSound(), roarVolume, 1.0F);
            roarCooldown = Mth.nextInt(tiger.getRandom(), minRoarInterval, maxRoarInterval);
        }

        private void setAnimation() {
            if (tiger.roarAnimationState != null) {
                tiger.roarAnimationState.start(tiger.getAnimationTick());
            }
        }

        @Override
        public void tick() {
            if (roarCooldown > 0) {
                roarCooldown--;
            }
        }
    }

    public static class TigerRoarAggroGoal extends AbstractRoarGoal {
        private final TigerEntity tiger;
        private final float roarVolume;
        private final int minRoarInterval;
        private final int maxRoarInterval;
        private int roarCooldown;
        private LivingEntity lastAttacker;
        private AnimationDefinition tigerRoar2;

        public TigerRoarAggroGoal(TigerEntity tiger, float roarVolume, int minRoarInterval, int maxRoarInterval) {
            super(tiger, roarVolume, minRoarInterval, maxRoarInterval);
            this.tiger = tiger;
            this.roarVolume = roarVolume;
            this.minRoarInterval = minRoarInterval;
            this.maxRoarInterval = maxRoarInterval;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public AnimationDefinition getTigerAggroRoar() {
            return tigerRoar2;
        }

        public void setTigerAggroRoar(AnimationDefinition tigerRoar2) {
            this.tigerRoar2 = tigerRoar2;
        }

        @Override
        public boolean canUse() {
            LivingEntity attacker = tiger.getLastHurtByMob();
            return attacker != null && attacker.isAlive()
                    && !tiger.isBaby() && !tiger.isStealth() && !tiger.isRunning()
                    && roarCooldown <= 0;
        }

        @Override
        public void start() {
            lastAttacker = tiger.getLastHurtByMob();
            setAnimation();
            tiger.playSound(tiger.getRoarAggroSound(), roarVolume, 1.0F);
            roarCooldown = Mth.nextInt(tiger.getRandom(), minRoarInterval, maxRoarInterval);
        }

        private void setAnimation() {
            if (tiger.roarAnimationState != null) {
                tiger.roarAnimationState.start(tiger.getAnimationTick());
            }
        }

        @Override
        public void tick() {
            if (roarCooldown > 0) {
                roarCooldown--;
            }

            if (roarCooldown == 0 && lastAttacker != null) {
                if (lastAttacker.isAlive()) {
                    tiger.setTarget(lastAttacker);
                }
                lastAttacker = null;
            }
        }
    }

    public final SoundEvent getRoarSound() {
        return ModSoundEvents.TIGER_ROAR;
    }

    public final SoundEvent getRoarAggroSound() {
        return ModSoundEvents.TIGER_ROAR_AGGRO;
    }

    class TigerHurtByTargetGoal extends HurtByTargetGoal {
        public TigerHurtByTargetGoal() {
            super(TigerEntity.this);
        }

        @Override
        public void start() {
            super.start();
            if (TigerEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity target) {
            if (mob instanceof TigerEntity && !mob.isBaby()) {
                super.alertOther(mob, target);
            }
        }
    }

    private static class TigerSleepGoal extends BigCatSleepGoal<TigerEntity> {
        private final TigerEntity tiger;
        private int sleepTime;

        public TigerSleepGoal(TigerEntity tiger) {
            super(tiger);
            this.tiger = tiger;
        }

        @Override
        public boolean canUse() {
            return !tiger.isStealth()
                    && !tiger.isRunning()
                    && !tiger.isYawning()
                    && super.canUse();
        }

        @Override
        public void start() {
            sleepTime = 200 + tiger.getRandom().nextInt(200);
            tiger.setSleeping(true);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            return sleepTime > 0
                    && tiger.getTarget() == null
                    && !tiger.isRunning()
                    && !tiger.isStealth();
        }

        @Override
        public void tick() {
            if (sleepTime-- <= 0 || tiger.getTarget() != null || tiger.isInWater()) {
                stop();
            }
        }

        @Override
        public void stop() {
            tiger.setSleeping(false);
            sleepTime = 0;
            super.stop();
        }
    }

    @Override
    public boolean isSprinting() {
        return isRunning();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        setRunning(sprinting);
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    private int getAnimationTick() {
        return this.tickCount;
    }

    public void setAttackAnimation(boolean b) {
        if (b) {
            this.setAnimation(TIGER_PAW_SWIPE);
        } else {
            this.setAnimation(null);
        }
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    private void updateSpeedAttributes() {
        if (this.isRunning()) {
            if (this.isAngry()) {
                Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.35F);
            } else {
                Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.3F);
            }
        } else if (this.isStealth()) {
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.1F);
        } else {
            Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.25F);
        }
    }

    public void setRunning(boolean running) {
        if (running) {
            this.setStealth(false);
            if (this.isAngry()) {
                setAnimation(TIGER_RUN_ANGRY);
            } else {
                setAnimation(TIGER_RUN);
            }
            updateSpeedAttributes();
        } else {
            if (this.currentAnimation == TIGER_RUN || this.currentAnimation == TIGER_RUN_ANGRY) {
                setAnimation(null);
            }
            updateSpeedAttributes();
        }
    }

    public void setJumping(boolean jumping) {
        this.setAnimation(TIGER_POUNCE);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        // Tigers are agile and can survive falls better than most mobs
        if (fallDistance < 10.0F) {
            return false; // No fall damage under 10 blocks
        }
        // Reduced fall damage for higher falls
        return super.causeFallDamage(fallDistance * 0.3F, damageMultiplier * 0.5F, damageSource);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof TigerEntity tiger)) {
            if (target instanceof Player player) {
                if (owner instanceof Player player1) {
                    if (!player1.canHarmPlayer(player)) {
                        return false;
                    }
                }
            }

            if (target instanceof AbstractHorse abstracthorse) {
                if (abstracthorse.isTamed()) {
                    return false;
                }
            }

            if (target instanceof TamableAnimal tamableanimal) {
                return !tamableanimal.isTame();
            }

            return true;
        } else {
            return !tiger.isTame() || tiger.getOwner() != owner;
        }
    }

    @Override public boolean isSleeping() { return entityData.get(TIGER_SLEEPING); }
    public void setSleeping(boolean sleeping) { entityData.set(TIGER_SLEEPING, sleeping); }

    public boolean isYawning() { return entityData.get(TIGER_YAWNING); }
    public void setYawning(boolean yawning) { entityData.set(TIGER_YAWNING, yawning); }
}