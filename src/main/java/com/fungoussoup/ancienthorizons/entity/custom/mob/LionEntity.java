package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.BigCatSleepGoal;
import com.fungoussoup.ancienthorizons.entity.ai.BigCatYawnGoal;
import com.fungoussoup.ancienthorizons.entity.interfaces.SleepingAnimal;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LionEntity extends TamableAnimal implements NeutralMob, VariantHolder<LionEntity.LionVariant>, SleepingAnimal {
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_MALE = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SLEEPING = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_YAWNING = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_PLAYING = SynchedEntityData.defineId(LionEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID prideLeaderUUID;
    private final List<UUID> prideMembers = new ArrayList<>();

    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    private int prideCheckCooldown;

    public LionEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(IS_MALE, this.random.nextBoolean());
        builder.define(IS_SLEEPING, false);
        builder.define(IS_YAWNING, false);
        builder.define(IS_RUNNING, false);
        builder.define(IS_PLAYING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LionSleepGoal(this));
        this.goalSelector.addGoal(2, new LionYawnGoal(this));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2D, 5.0F, 2.0F));
        this.goalSelector.addGoal(3, new LionProtectCubGoal(this));
        this.goalSelector.addGoal(4, new LionPrideFollowGoal(this, 1.2D));
        this.goalSelector.addGoal(5, new LionHuntGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LionCubPlayGoal(this));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, this::isFood, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && !this.isBaby()) {
            if (prideCheckCooldown-- <= 0) {
                prideCheckCooldown = 200 + random.nextInt(200);
                updatePride();
            }
        }

        if (remainingPersistentAngerTime > 0)
            remainingPersistentAngerTime--;
    }

    private void updatePride() {
        if (prideLeaderUUID == null) {
            List<LionEntity> nearby = level().getEntitiesOfClass(LionEntity.class, this.getBoundingBox().inflate(24.0D));
            for (LionEntity other : nearby) {
                if (other != this && other.isMale() && other.prideLeaderUUID == null) {
                    prideLeaderUUID = other.getUUID();
                    other.addPrideMember(this);
                    return;
                }
            }
            if (isMale()) {
                prideLeaderUUID = this.getUUID(); // become leader
            }
        }
    }

    public void addPrideMember(LionEntity member) {
        if (!prideMembers.contains(member.getUUID())) {
            prideMembers.add(member.getUUID());
        }
    }

    @Nullable
    public LionEntity findPrideLeader() {
        if (prideLeaderUUID == null) return null;
        for (LionEntity lion : level().getEntitiesOfClass(LionEntity.class, this.getBoundingBox().inflate(24.0D))) {
            if (lion.getUUID().equals(prideLeaderUUID)) return lion;
        }
        return null;
    }

    public boolean isMale() {
        return this.entityData.get(IS_MALE);
    }

    public boolean isLeader() {
        return prideLeaderUUID != null && prideLeaderUUID.equals(this.getUUID());
    }

    public boolean isInPride() {
        return prideLeaderUUID != null;
    }

    public boolean isSleeping() {
        return this.entityData.get(IS_SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.entityData.set(IS_SLEEPING, sleeping);
    }

    public boolean isYawning() {
        return this.entityData.get(IS_YAWNING);
    }

    public void setYawning(boolean yawning) {
        this.entityData.set(IS_YAWNING, yawning);
    }

    public boolean isRunning() {
        return this.entityData.get(IS_RUNNING);
    }

    public void setRunning(boolean running) {
        this.entityData.set(IS_RUNNING, running);
    }

    public boolean isPlaying() {
        return this.entityData.get(IS_PLAYING);
    }

    public void setPlaying(boolean playing) {
        this.entityData.set(IS_PLAYING, playing);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.MEAT);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSoundEvents.LION_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.LION_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSoundEvents.LION_DEATH;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob other) {
        LionEntity cub = ModEntities.LION.get().create(world);
        if (cub == null) return null;

        if (other instanceof LionEntity mate) {
            boolean fatherWhite = this.getVariant() == LionVariant.WHITE;
            boolean motherWhite = mate.getVariant() == LionVariant.WHITE;

            if (fatherWhite && motherWhite)
                cub.setVariant(LionVariant.WHITE);
            else if (random.nextInt(10) == 0)
                cub.setVariant(LionVariant.WHITE);
            else
                cub.setVariant(LionVariant.NORMAL);

            cub.entityData.set(IS_MALE, random.nextBoolean());
        }

        return cub;
    }

    @Override public int getRemainingPersistentAngerTime() { return remainingPersistentAngerTime; }
    @Override public void setRemainingPersistentAngerTime(int i) { this.remainingPersistentAngerTime = i; }
    @Nullable @Override public UUID getPersistentAngerTarget() { return persistentAngerTarget; }
    @Override public void setPersistentAngerTarget(@Nullable UUID uuid) { this.persistentAngerTarget = uuid; }
    @Override public void startPersistentAngerTimer() { this.remainingPersistentAngerTime = 200 + random.nextInt(200); }

    @Override public void setVariant(LionVariant lionVariant) { this.entityData.set(VARIANT, lionVariant.getId()); }
    @Override public LionVariant getVariant() { return LionVariant.byIdMale(this.entityData.get(VARIANT)); }

    private static class LionSleepGoal extends BigCatSleepGoal<LionEntity> {
        private final LionEntity lion;
        private int sleepTime;

        public LionSleepGoal(LionEntity lion) {
            super(lion);
            this.lion = lion;
        }

        @Override
        public boolean canUse() {
            return (!lion.isPlaying()
                    && !lion.isRunning()
                    && !lion.isYawning()
                    && super.canUse()) || lion.isMale() && lion.getRandom().nextFloat() < 0.01F;
        }

        @Override
        public void start() {
            sleepTime = 200 + lion.getRandom().nextInt(200);
            lion.setSleeping(true);
            lion.playSound(ModSoundEvents.LION_AMBIENT, 0.8F, 0.8F);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            return sleepTime > 0
                    && lion.getTarget() == null
                    && !lion.isInWater()
                    && !lion.isRunning()
                    && !lion.isPlaying();
        }

        @Override
        public void tick() {
            if (sleepTime-- <= 0 || lion.getTarget() != null || lion.isInWater()) stop();
        }

        @Override
        public void stop() {
            lion.setSleeping(false);
            sleepTime = 0;
            super.stop();
        }
    }

    private static class LionYawnGoal extends BigCatYawnGoal<LionEntity> {
        private final LionEntity lion;
        private int yawnTicks;

        public LionYawnGoal(LionEntity lion) {
            super(lion);
            this.lion = lion;
        }

        @Override
        public boolean canUse() {
            return !lion.isSleeping()
                    && !lion.isRunning()
                    && !lion.isPlaying()
                    && !lion.isYawning()
                    && lion.getRandom().nextFloat() < 0.002F
                    && super.canUse();
        }

        @Override
        public void start() {
            lion.setYawning(true);
            lion.playSound(ModSoundEvents.LION_YAWN, 1.0F, 1.0F);
            yawnTicks = 60;
        }

        @Override
        public void tick() {
            if (yawnTicks > 0) yawnTicks--;
            if (yawnTicks <= 0 && lion.isYawning()) lion.setYawning(false);
        }
    }

    public static class LionHuntGoal extends Goal {
        private final LionEntity lion;
        private LivingEntity target;

        public LionHuntGoal(LionEntity lion) {
            this.lion = lion;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (lion.isMale() || lion.isBaby() || lion.isTame())
                return false;

            LivingEntity prey = lion.level().getNearestEntity(
                    Animal.class,
                    TargetingConditions.forCombat().range(20.0D),
                    lion,
                    lion.getX(), lion.getEyeY(), lion.getZ(),
                    lion.getBoundingBox().inflate(20.0D)
            );
            if (prey != null) {
                target = prey;
                return true;
            }
            return false;
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) return;

            double dist = lion.distanceToSqr(target);
            lion.getLookControl().setLookAt(target);

            if (dist > 36.0D)
                lion.getNavigation().moveTo(target, 1.0D);
            else {
                lion.getNavigation().stop();
                lion.doHurtTarget(target);
                lion.playSound(ModSoundEvents.LION_AMBIENT, 1.5F, 1.0F);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive();
        }
    }


    public static class LionCubPlayGoal extends Goal {
        private final LionEntity cub;
        private LivingEntity nearbyAdult;
        private int playTime;

        public LionCubPlayGoal(LionEntity cub) {
            this.cub = cub;
        }

        @Override
        public boolean canUse() {
            if (!cub.isBaby()) return false;
            List<LionEntity> adults = cub.level().getEntitiesOfClass(LionEntity.class, cub.getBoundingBox().inflate(6.0D),
                    adult -> !adult.isBaby());
            if (!adults.isEmpty()) {
                nearbyAdult = adults.get(cub.random.nextInt(adults.size()));
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            playTime = 100 + cub.getRandom().nextInt(100);
        }

        @Override
        public void tick() {
            if (nearbyAdult == null || !nearbyAdult.isAlive()) return;
            cub.getLookControl().setLookAt(nearbyAdult);
            cub.getNavigation().moveTo(nearbyAdult, 1.0D);
            if (cub.distanceTo(nearbyAdult) < 2.0D && cub.getRandom().nextInt(20) == 0)
                cub.playSound(SoundEvents.PARROT_FLY, 0.5F, 1.2F);
            playTime--;
        }

        @Override
        public boolean canContinueToUse() {
            return playTime > 0 && nearbyAdult != null && nearbyAdult.isAlive();
        }
    }

    /** Pride members follow the leader */
    public static class LionPrideFollowGoal extends Goal {
        private final LionEntity lion;
        private final double speed;

        public LionPrideFollowGoal(LionEntity lion, double speed) {
            this.lion = lion;
            this.speed = speed;
        }

        @Override
        public boolean canUse() {
            if (!lion.isInPride() || lion.isLeader()) return false;
            LionEntity leader = lion.findPrideLeader();
            return leader != null && lion.distanceTo(leader) > 8.0D;
        }

        @Override
        public void tick() {
            LionEntity leader = lion.findPrideLeader();
            if (leader != null) {
                lion.getNavigation().moveTo(leader, speed);
            }
        }
    }

    /** Leader protects cubs and females */
    public static class LionProtectCubGoal extends TargetGoal {
        private final LionEntity lion;

        public LionProtectCubGoal(LionEntity lion) {
            super(lion, false);
            this.lion = lion;
        }

        @Override
        public boolean canUse() {
            if (!lion.isLeader()) return false;
            List<LionEntity> cubs = lion.level().getEntitiesOfClass(LionEntity.class, lion.getBoundingBox().inflate(10.0D),
                    LionEntity::isBaby);
            return !cubs.isEmpty();
        }

        @Override
        public void start() {
            LivingEntity threat = lion.level().getNearestEntity(
                    Monster.class,
                    TargetingConditions.forCombat().range(10.0D),
                    lion,
                    lion.getX(), lion.getEyeY(), lion.getZ(),
                    lion.getBoundingBox().inflate(10.0D)
            );
            if (threat != null) {
                lion.setTarget(threat);
                lion.playSound(ModSoundEvents.LION_ROAR, 2.0F, 0.8F);
            }
        }
    }

    public enum LionVariant {
        NORMAL(0),
        WHITE(1);

        private final int id;

        LionVariant(int id) {
            this.id = id;
        }

        public static LionVariant byIdMale(int i) {
            for (LionVariant v : values()) if (v.id == i) return v;
            return NORMAL;
        }

        public int getId() { return id; }
    }
}
