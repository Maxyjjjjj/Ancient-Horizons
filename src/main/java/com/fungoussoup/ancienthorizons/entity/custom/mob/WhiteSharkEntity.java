package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class WhiteSharkEntity extends TrulyWaterAnimal implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
            SynchedEntityData.defineId(WhiteSharkEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);


    private static final int CURIOSITY_DISTANCE = 16;
    private static final int FLEE_DISTANCE = 6;
    private static final int HUNT_DISTANCE = 20;

    @Nullable
    private UUID persistentAngerTarget;
    private int curiousityTimer = 0;

    public WhiteSharkEntity(EntityType<? extends TrulyWaterAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(1, new FleeFromPlayerGoal(this, 1.8, FLEE_DISTANCE));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new CuriousApproachPlayerGoal(this, 1.0, CURIOSITY_DISTANCE, FLEE_DISTANCE));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 40));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Drowned.class, true));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.275)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.WHITE_SHARK.get().create(serverLevel);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FISHES);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    // Custom AI Goals

    static class CuriousApproachPlayerGoal extends Goal {
        private final WhiteSharkEntity shark;
        private final double speedModifier;
        private final int approachDistance;
        private final int fleeDistance;
        private Player targetPlayer;

        public CuriousApproachPlayerGoal(WhiteSharkEntity shark, double speed, int approach, int flee) {
            this.shark = shark;
            this.speedModifier = speed;
            this.approachDistance = approach;
            this.fleeDistance = flee;
        }

        @Override
        public boolean canUse() {
            if (this.shark.getTarget() != null) {
                return false;
            }

            this.targetPlayer = this.shark.level().getNearestPlayer(this.shark, approachDistance);

            if (this.targetPlayer == null) {
                return false;
            }

            double distance = this.shark.distanceTo(this.targetPlayer);
            return distance < approachDistance && distance > fleeDistance;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
                return false;
            }

            if (this.shark.getTarget() != null) {
                return false;
            }

            double distance = this.shark.distanceTo(this.targetPlayer);
            return distance < approachDistance && distance > fleeDistance;
        }

        @Override
        public void tick() {
            if (this.targetPlayer != null) {
                this.shark.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);

                this.shark.distanceTo(this.targetPlayer);

                Vec3 toPlayer = this.targetPlayer.position().subtract(this.shark.position()).normalize();
                Vec3 circleOffset = new Vec3(-toPlayer.z, 0, toPlayer.x).scale(0.3);
                Vec3 targetPos = this.targetPlayer.position().subtract(toPlayer.scale(fleeDistance + 2)).add(circleOffset);

                this.shark.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, this.speedModifier);
            }
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.shark.getNavigation().stop();
        }
    }

    static class FleeFromPlayerGoal extends Goal {
        private final WhiteSharkEntity shark;
        private final double speedModifier;
        private final int fleeDistance;
        private Player playerToFleeFrom;

        public FleeFromPlayerGoal(WhiteSharkEntity shark, double speed, int distance) {
            this.shark = shark;
            this.speedModifier = speed;
            this.fleeDistance = distance;
        }

        @Override
        public boolean canUse() {
            if (this.shark.getTarget() != null) {
                return false;
            }

            this.playerToFleeFrom = this.shark.level().getNearestPlayer(this.shark, fleeDistance);
            return this.playerToFleeFrom != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.playerToFleeFrom == null || !this.playerToFleeFrom.isAlive()) {
                return false;
            }

            if (this.shark.getTarget() != null) {
                return false;
            }

            return this.shark.distanceTo(this.playerToFleeFrom) < fleeDistance;
        }

        @Override
        public void tick() {
            if (this.playerToFleeFrom != null) {
                Vec3 fleeVector = this.shark.position().subtract(this.playerToFleeFrom.position()).normalize().scale(3);
                Vec3 fleePos = this.shark.position().add(fleeVector);

                this.shark.getNavigation().moveTo(fleePos.x, fleePos.y, fleePos.z, this.speedModifier);
            }
        }

        @Override
        public void stop() {
            this.playerToFleeFrom = null;
            this.shark.getNavigation().stop();
        }
    }
}