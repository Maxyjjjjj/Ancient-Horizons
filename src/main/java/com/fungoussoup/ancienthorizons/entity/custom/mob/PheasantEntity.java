package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class PheasantEntity extends Animal {

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private float nextFlap = 1.0F;

    public PheasantEntity(EntityType<? extends PheasantEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2D, stack -> stack.is(ItemTags.CHICKEN_FOOD), false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new PheasantJostleGoal(this));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Nullable
    @Override
    public PheasantEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob parent) {
        return ModEntities.PHEASANT.get().create(serverLevel);
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return stack.is(ItemTags.CHICKEN_FOOD);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.PHEASANT_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.PHEASANT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.PHEASANT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState blockIn) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    public void aiStep() {
        super.aiStep();
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < (double)0.0F) {
            this.setDeltaMovement(vec3.multiply((double)1.0F, 0.6, (double)1.0F));
        }

        this.flap += this.flapping * 2.0F;
    }

    @Override
    public float getAgeScale() {
        return super.getAgeScale();
    }

    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnData);
    }

    public static class PheasantJostleGoal extends Goal {
        private final PheasantEntity pheasant;
        private PheasantEntity target;
        private int jostleTime;

        public PheasantJostleGoal(PheasantEntity pheasant) {
            this.pheasant = pheasant;
            this.setFlags(EnumSet.of(Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (pheasant.isBaby() || pheasant.getRandom().nextInt(600) != 0) return false;
            List<PheasantEntity> nearby = pheasant.level().getEntitiesOfClass(PheasantEntity.class, pheasant.getBoundingBox().inflate(4.0), e -> e != pheasant && !e.isBaby());
            if (!nearby.isEmpty()) {
                this.target = nearby.get(pheasant.getRandom().nextInt(nearby.size()));
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            this.jostleTime = 40 + pheasant.getRandom().nextInt(20);
            pheasant.getNavigation().moveTo(target, 1.0);
        }

        @Override
        public void tick() {
            pheasant.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (pheasant.distanceToSqr(target) < 1.0D) {
                // Nudge the target sideways a bit
                double dx = target.getX() - pheasant.getX();
                double dz = target.getZ() - pheasant.getZ();
                double magnitude = Math.sqrt(dx * dx + dz * dz) + 0.01;
                target.push(dx / magnitude * 0.1, 0.05, dz / magnitude * 0.1);

                // Optional: play a flapping or squawk sound
                if (pheasant.level().random.nextInt(20) == 0) {
                    pheasant.playSound(Objects.requireNonNull(pheasant.getAmbientSound()), 1.0F, 1.2F + pheasant.getRandom().nextFloat() * 0.4F);
                }
            }
            jostleTime--;
        }

        @Override
        public boolean canContinueToUse() {
            return jostleTime > 0 && target != null && target.isAlive() && pheasant.distanceToSqr(target) < 16.0D;
        }

        @Override
        public void stop() {
            target = null;
        }
    }

}

