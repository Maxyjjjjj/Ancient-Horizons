package com.fungoussoup.ancienthorizons.entity.custom.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class AbstractUndergroundAnimal extends Animal {
    private static final EntityDataAccessor<Boolean> IS_UNDERGROUND =
            SynchedEntityData.defineId(AbstractUndergroundAnimal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_DIGGING =
            SynchedEntityData.defineId(AbstractUndergroundAnimal.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DIG_PROGRESS =
            SynchedEntityData.defineId(AbstractUndergroundAnimal.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> UNDERGROUND_TIME =
            SynchedEntityData.defineId(AbstractUndergroundAnimal.class, EntityDataSerializers.INT);

    // Underground behavior constants
    protected static final int MAX_UNDERGROUND_TIME = 6000; // 5 minutes
    protected static final int MIN_UNDERGROUND_TIME = 1200; // 1 minute
    protected static final int DIG_DURATION = 60; // 3 seconds
    protected static final double EMERGENCE_DISTANCE = 16.0; // Distance from player to emerge
    protected static final double DIG_DISTANCE = 8.0; // Distance from player to dig

    // Internal state
    private int digCooldown = 0;
    private int emergeCooldown = 0;
    private BlockPos lastSurfacePos;
    private boolean wasUnderground = false;

    protected AbstractUndergroundAnimal(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.LAVA, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_UNDERGROUND, false);
        builder.define(IS_DIGGING, false);
        builder.define(DIG_PROGRESS, 0);
        builder.define(UNDERGROUND_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsUnderground", this.isUnderground());
        tag.putBoolean("IsDigging", this.isDigging());
        tag.putInt("DigProgress", this.getDigProgress());
        tag.putInt("UndergroundTime", this.getUndergroundTime());
        tag.putInt("DigCooldown", this.digCooldown);
        tag.putInt("EmergeCooldown", this.emergeCooldown);

        if (this.lastSurfacePos != null) {
            tag.putInt("LastSurfaceX", this.lastSurfacePos.getX());
            tag.putInt("LastSurfaceY", this.lastSurfacePos.getY());
            tag.putInt("LastSurfaceZ", this.lastSurfacePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setUnderground(tag.getBoolean("IsUnderground"));
        this.setDigging(tag.getBoolean("IsDigging"));
        this.setDigProgress(tag.getInt("DigProgress"));
        this.setUndergroundTime(tag.getInt("UndergroundTime"));
        this.digCooldown = tag.getInt("DigCooldown");
        this.emergeCooldown = tag.getInt("EmergeCooldown");

        if (tag.contains("LastSurfaceX")) {
            this.lastSurfacePos = new BlockPos(
                    tag.getInt("LastSurfaceX"),
                    tag.getInt("LastSurfaceY"),
                    tag.getInt("LastSurfaceZ")
            );
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.handleUndergroundBehavior();
            this.updateCooldowns();
        } else if (this.isUnderground()) {
            this.spawnUndergroundParticles();
        }

        this.handleDiggingEffects();
    }

    private void handleUndergroundBehavior() {
        boolean underground = this.isUnderground();

        if (underground) {
            int time = this.getUndergroundTime();
            this.setUndergroundTime(time + 1);

            // Make entity invisible and non-collidable when underground
            this.setInvisible(true);
            this.setInvulnerable(true);

            // Reduce AI activity while underground
            if (time % 20 == 0) { // Only update every second
                this.updateUndergroundMovement();
            }
        } else {
            this.setInvisible(false);
            this.setInvulnerable(false);
            this.setUndergroundTime(0);
        }

        // Track state changes
        if (underground != this.wasUnderground) {
            this.onUndergroundStateChange(underground);
            this.wasUnderground = underground;
        }
    }

    private void updateCooldowns() {
        if (this.digCooldown > 0) {
            this.digCooldown--;
        }
        if (this.emergeCooldown > 0) {
            this.emergeCooldown--;
        }
    }

    private void handleDiggingEffects() {
        if (this.isDigging()) {
            int progress = this.getDigProgress();

            if (this.level().isClientSide) {
                // Spawn digging particles
                this.spawnDiggingParticles();
            } else {
                // Update dig progress
                this.setDigProgress(progress + 1);

                if (progress >= DIG_DURATION) {
                    this.finishDigging();
                }
            }
        }
    }

    private void spawnDiggingParticles() {
        if (this.level().isClientSide && this.random.nextInt(3) == 0) {
            BlockPos pos = this.blockPosition();
            BlockState state = this.level().getBlockState(pos.below());

            if (!state.isAir()) {
                for (int i = 0; i < 3; i++) {
                    double x = this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
                    double y = this.getY() + 0.1;
                    double z = this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth();

                    this.level().addParticle(ParticleTypes.POOF, x, y, z, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    private void spawnUndergroundParticles() {
        if (!this.level().isClientSide) return;

        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos.below());

        if (!state.isAir() && this.random.nextInt(5) == 0) {
            double x = this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
            double y = this.getY() + 0.05;
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth();

            this.level().addParticle(
                    (ParticleOptions) BlockParticleOption.codec(ParticleTypes.BLOCK),
                    false, x, y, z,
                    (this.random.nextDouble() - 0.5) * 0.05,
                    0.02,
                    (this.random.nextDouble() - 0.5) * 0.05);
        }

        if (this.random.nextInt(40) == 0) {
            double x = this.getX() + (this.random.nextDouble() - 0.5) * this.getBbWidth();
            double y = this.getY() + 0.1;
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * this.getBbWidth();

            this.level().addParticle(ParticleTypes.POOF, x, y, z, 0.0, 0.02, 0.0);
        }
    }

    private void updateUndergroundMovement() {
        // Simple underground movement - move towards last surface position or randomly
        if (this.lastSurfacePos != null) {
            Vec3 targetPos = Vec3.atCenterOf(this.lastSurfacePos);
            Vec3 currentPos = this.position();

            if (currentPos.distanceTo(targetPos) > 2.0) {
                Vec3 direction = targetPos.subtract(currentPos).normalize();
                this.setDeltaMovement(direction.scale(0.1));
            }
        } else {
            // Random underground movement
            if (this.random.nextInt(100) == 0) {
                double x = (this.random.nextDouble() - 0.5) * 0.2;
                double z = (this.random.nextDouble() - 0.5) * 0.2;
                this.setDeltaMovement(x, 0, z);
            }
        }
    }

    protected void onUndergroundStateChange(boolean underground) {
        if (underground) {
            this.lastSurfacePos = this.blockPosition();
            this.playSound(this.getDigSound(), 1.0F, 1.0F);

            if (this.level().isClientSide) {
                for (int i = 0; i < 12; i++) {
                    double x = this.getX() + (this.random.nextDouble() - 0.5) * 0.8;
                    double y = this.getY() + 0.1;
                    double z = this.getZ() + (this.random.nextDouble() - 0.5) * 0.8;
                    this.level().addParticle(ParticleTypes.POOF, x, y, z, 0.0, 0.05, 0.0);
                }
            }

        } else {
            this.playSound(this.getEmergeSound(), 1.0F, 1.0F);

            if (this.level().isClientSide) {
                for (int i = 0; i < 12; i++) {
                    double x = this.getX() + (this.random.nextDouble() - 0.5) * 0.8;
                    double y = this.getY() + 0.1;
                    double z = this.getZ() + (this.random.nextDouble() - 0.5) * 0.8;
                    this.level().addParticle(ParticleTypes.CLOUD, x, y, z, 0.0, 0.05, 0.0);
                }
            }
        }
    }


    public void startDigging() {
        if (this.canDig()) {
            this.setDigging(true);
            this.setDigProgress(0);
            this.digCooldown = 200; // 10 second cooldown
            this.playSound(this.getDigSound(), 1.0F, 1.0F);
        }
    }

    public void finishDigging() {
        this.setDigging(false);
        this.setDigProgress(0);
        this.setUnderground(true);
        this.setUndergroundTime(0);
    }

    public void startEmerging() {
        if (this.canEmerge()) {
            this.setUnderground(false);
            this.emergeCooldown = 100; // 5 second cooldown
            this.playSound(this.getEmergeSound(), 1.0F, 1.0F);
        }
    }

    public boolean canDig() {
        return !this.isUnderground() && !this.isDigging() && this.digCooldown <= 0 && this.canDigAt(this.blockPosition());
    }

    public boolean canEmerge() {
        return this.isUnderground() && this.emergeCooldown <= 0 && this.canEmergeAt(this.blockPosition());
    }

    protected boolean canDigAt(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos.below());
        return state.is(BlockTags.DIRT) || state.is(Blocks.GRASS_BLOCK) ||
                state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.GRAVEL);
    }

    protected boolean canEmergeAt(BlockPos pos) {
        BlockState aboveState = this.level().getBlockState(pos.above());
        return aboveState.isAir() || aboveState.is(BlockTags.REPLACEABLE);
    }

    public Player findNearestPlayer(double distance) {
        return this.level().getNearestPlayer(this, distance);
    }

    public boolean shouldHideFromPlayer() {
        Player player = this.findNearestPlayer(DIG_DISTANCE);
        return player != null && !player.isCreative() && !player.isSpectator();
    }

    public boolean shouldEmergeForPlayer() {
        Player player = this.findNearestPlayer(EMERGENCE_DISTANCE);
        return player != null && this.getUndergroundTime() > MIN_UNDERGROUND_TIME;
    }

    // Getters and setters for synced data
    public boolean isUnderground() {
        return this.entityData.get(IS_UNDERGROUND);
    }

    public void setUnderground(boolean underground) {
        this.entityData.set(IS_UNDERGROUND, underground);
    }

    public boolean isDigging() {
        return this.entityData.get(IS_DIGGING);
    }

    public void setDigging(boolean digging) {
        this.entityData.set(IS_DIGGING, digging);
    }

    public int getDigProgress() {
        return this.entityData.get(DIG_PROGRESS);
    }

    public void setDigProgress(int progress) {
        this.entityData.set(DIG_PROGRESS, progress);
    }

    public int getUndergroundTime() {
        return this.entityData.get(UNDERGROUND_TIME);
    }

    public void setUndergroundTime(int time) {
        this.entityData.set(UNDERGROUND_TIME, time);
    }

    // Sound methods - override in subclasses
    protected SoundEvent getDigSound() {
        return SoundEvents.PLAYER_HURT_DROWN;
    }

    protected SoundEvent getEmergeSound() {
        return SoundEvents.PLAYER_BREATH;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new DigGoal(this, 1.0));
        this.goalSelector.addGoal(2, new EmergeGoal(this, 1.0));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, this::isFood, false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new UndergroundWanderGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Override this method in subclasses to define what food this animal eats
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isUnderground() && source.getEntity() instanceof Player player) {
            // Force emergence if attacked while underground
            this.startEmerging();
        }
        return super.hurt(source, amount);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public static class DigGoal extends Goal {
        private final AbstractUndergroundAnimal animal;
        private final double speed;
        private int digTimer;

        public DigGoal(AbstractUndergroundAnimal animal, double speed) {
            this.animal = animal;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            // Only dig if not already underground/digging and there's a reason to hide
            return !this.animal.isUnderground() && !this.animal.isDigging() &&
                    this.animal.canDig() && this.animal.shouldHideFromPlayer();
        }

        @Override
        public boolean canContinueToUse() {
            return this.animal.isDigging() && this.animal.getDigProgress() < DIG_DURATION;
        }

        @Override
        public void start() {
            this.animal.getNavigation().stop();
            this.animal.startDigging();
            this.digTimer = 0;
        }

        @Override
        public void stop() {
            this.digTimer = 0;
        }

        @Override
        public void tick() {
            this.animal.getNavigation().stop();
            this.animal.getLookControl().setLookAt(this.animal.getX(), this.animal.getY() - 1, this.animal.getZ());
            this.digTimer++;
        }
    }

    public static class EmergeGoal extends Goal {
        private final AbstractUndergroundAnimal animal;
        private final double speed;

        public EmergeGoal(AbstractUndergroundAnimal animal, double speed) {
            this.animal = animal;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.animal.isUnderground() && this.animal.canEmerge() &&
                    (this.animal.shouldEmergeForPlayer() ||
                            this.animal.getUndergroundTime() > MAX_UNDERGROUND_TIME);
        }

        @Override
        public void start() {
            this.animal.startEmerging();
        }

        @Override
        public void tick() {
            // Goal completes immediately when started
        }
    }

    public static class UndergroundWanderGoal extends Goal {
        private final AbstractUndergroundAnimal animal;
        private final double speed;
        private int wanderTimer;

        public UndergroundWanderGoal(AbstractUndergroundAnimal animal, double speed) {
            this.animal = animal;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.animal.isUnderground() && this.animal.getRandom().nextInt(120) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.animal.isUnderground() && this.wanderTimer < 100;
        }

        @Override
        public void start() {
            this.wanderTimer = 0;
        }

        @Override
        public void tick() {
            this.wanderTimer++;
            if (this.wanderTimer % 20 == 0) {
                // Move randomly underground
                double x = this.animal.getX() + (this.animal.getRandom().nextDouble() - 0.5) * 4;
                double y = this.animal.getY();
                double z = this.animal.getZ() + (this.animal.getRandom().nextDouble() - 0.5) * 4;

                this.animal.getNavigation().moveTo(x, y, z, this.speed);
            }
        }
    }
}
