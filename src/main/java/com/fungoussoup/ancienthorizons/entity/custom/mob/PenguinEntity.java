package com.fungoussoup.ancienthorizons.entity.custom.mob;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.ai.SemiAquaticSwimGoal;
import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;
import com.fungoussoup.ancienthorizons.registry.ModBlocks;
import com.fungoussoup.ancienthorizons.registry.ModSoundEvents;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class PenguinEntity extends SemiAquaticAnimal {

    private static final EntityDataAccessor<Boolean> IS_SLIDING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_INCUBATING = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SLIDE_COOLDOWN = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INCUBATION_TIME = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FISHING_TIME = SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> EGG_POSITION =  SynchedEntityData.defineId(PenguinEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    private int slideTime = 0;
    private int fishingTime = 0;
    private boolean isHungry = false;
    private boolean hasEgg = false;
    static final int INCUBATION_DURATION = 12000; // 10 minutes (12000 ticks)
    private int hungerTimer = 0;
    private static final int HUNGER_INTERVAL = 6000; // 5 minutes


    public PenguinEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_SLIDING, false);
        builder.define(IS_INCUBATING, false);
        builder.define(SLIDE_COOLDOWN, 0);
        builder.define(INCUBATION_TIME, 0);
        builder.define(FISHING_TIME, 0);
        builder.define(EGG_POSITION, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new PenguinIncubationGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, stack -> stack.is(ItemTags.FISHES), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(6, new PenguinSlideGoal(this));
        this.goalSelector.addGoal(7, new PenguinFishingGoal(this));
        this.goalSelector.addGoal(8, new SemiAquaticSwimGoal(this, 1.5D, 7));
        this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, AbstractSchoolingFish.class, false, false) {
            public boolean canUse() {
                return isHungry() && super.canUse();
            }
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    @Override
    public void tick() {
        super.tick();

        // Handle sliding mechanics
        if (this.isSliding()) {
            this.slideTime++;
            if (this.slideTime > 40) { // 2 seconds of sliding
                this.setSliding(false);
                this.slideTime = 0;
            }
        }

        // Handle incubation
        if (this.isIncubating()) {
            this.incrementIncubationTime();
            if (this.getIncubationTime() >= INCUBATION_DURATION) {
                this.hatchEgg();
            }
        }

        // Reduce slide cooldown
        if (this.getSlideCooldown() > 0) {
            this.setSlideCooldown(this.getSlideCooldown() - 1);
        }

        // Boost speed in water
        if (this.isInWater()) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.multiply(1.2, 1.0, 1.2));
        }

        if (++hungerTimer >= HUNGER_INTERVAL) {
            this.isHungry = true;
            hungerTimer = 0;
        }
    }

    public void satisfyHunger() {
        this.isHungry = false;
        this.hungerTimer = this.getRandom().nextInt(2000); // Random delay before next hunger
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(ModTags.Items.PENGUIN_FOOD)) {
            if (!this.level().isClientSide) {
                this.heal(4.0F);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                // Chance to start following player briefly
                if (this.getRandom().nextFloat() < 0.3F) {
                    this.setTarget(null);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.FISHES);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return ModEntities.PENGUIN.get().create(serverLevel);
    }

    // Penguin-specific methods
    public boolean isSliding() {
        return this.entityData.get(IS_SLIDING);
    }

    public void setSliding(boolean sliding) {
        this.entityData.set(IS_SLIDING, sliding);
    }

    public int getSlideCooldown() {
        return this.entityData.get(SLIDE_COOLDOWN);
    }

    public void setSlideCooldown(int cooldown) {
        this.entityData.set(SLIDE_COOLDOWN, cooldown);
    }

    public boolean canSlide() {
        return !this.isSliding() && this.getSlideCooldown() <= 0 && this.onGround();
    }

    public boolean isHungry() {
        return this.isHungry;
    }

    public boolean isIncubating() {
        return this.entityData.get(IS_INCUBATING);
    }

    public void setIncubating(boolean incubating) {
        this.entityData.set(IS_INCUBATING, incubating);
    }

    public int getIncubationTime() {
        return this.entityData.get(INCUBATION_TIME);
    }

    public void setIncubationTime(int time) {
        this.entityData.set(INCUBATION_TIME, time);
    }

    public void incrementIncubationTime() {
        this.setIncubationTime(this.getIncubationTime() + 1);
    }

    public boolean hasEgg() {
        return this.hasEgg;
    }

    public void setHasEgg(boolean hasEgg) {
        this.hasEgg = hasEgg;
    }

    public boolean canStartIncubation() {
        return this.hasEgg() && !this.isIncubating() && !this.isBaby();
    }

    private void layEgg() {
        if (!this.level().isClientSide && this.hasEgg()) {
            BlockPos pos = this.blockPosition();
            if (this.level().getBlockState(pos).isAir()) {
                this.level().setBlock(pos, ModBlocks.PENGUIN_EGG.get().defaultBlockState(), 3);
                this.setHasEgg(false);
                this.setIncubating(true);
            }
        }
    }

    public Optional<BlockPos> getEggPosition() {
        return this.entityData.get(EGG_POSITION);
    }

    public void setEggPosition(@Nullable BlockPos pos) {
        this.entityData.set(EGG_POSITION, Optional.ofNullable(pos));
    }

    // Enhanced hatchEgg method
    private void hatchEgg() {
        if (!this.level().isClientSide) {
            Optional<BlockPos> eggPosOpt = getEggPosition();
            BlockPos eggPos = eggPosOpt.orElse(this.blockPosition());

            // Verify egg still exists before hatching
            if (this.level().getBlockState(eggPos).is(ModBlocks.PENGUIN_EGG.get())) {
                // The egg block handles its own hatching, so we just trigger it
                this.level().destroyBlock(eggPos, false);

                // Create baby penguin
                PenguinEntity baby = ModEntities.PENGUIN.get().create(this.level());
                if (baby != null) {
                    baby.setBaby(true);
                    baby.moveTo(eggPos.getX() + 0.5, eggPos.getY(), eggPos.getZ() + 0.5,
                            this.getYRot(), 0.0F);
                    this.level().addFreshEntity(baby);

                    // Play hatching sound
                    this.level().playSound(null, eggPos, SoundEvents.CHICKEN_EGG,
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            }
        }

        // Reset incubation state
        this.setIncubating(false);
        this.setIncubationTime(0);
        this.setHasEgg(false);
        this.setEggPosition(null);
    }


    public void finalizeSpawn(ServerLevel level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // Adult male penguins have a chance to spawn with an egg during breeding season
        if (!this.isBaby() && this.getRandom().nextFloat() < 0.1F) {
            this.setHasEgg(true);
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        if (partner instanceof PenguinEntity penguin) {
            if (this.getRandom().nextBoolean()) {
                this.setHasEgg(true);
                this.layEgg();
            } else {
                penguin.setHasEgg(true);
                penguin.layEgg();
            }
        }
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) return false;
        if (!(otherAnimal instanceof PenguinEntity)) return false;

        // Don't breed if either has an egg or is incubating
        PenguinEntity other = (PenguinEntity) otherAnimal;
        return this.isInLove() && other.isInLove() &&
                !this.hasEgg() && !other.hasEgg() &&
                !this.isIncubating() && !other.isIncubating();
    }

    // Check if penguin is on ice or snow
    public boolean isOnSlipperyGround() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos.below());
        return state.is(BlockTags.ICE) || state.is(BlockTags.SNOW);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.PENGUIN_IDLE; // Placeholder - you'd want penguin-specific sounds
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.PENGUIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.PENGUIN_DEATH;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsSliding", this.isSliding());
        compound.putBoolean("IsIncubating", this.isIncubating());
        compound.putInt("SlideCooldown", this.getSlideCooldown());
        compound.putInt("IncubationTime", this.getIncubationTime());
        compound.putBoolean("HasEgg", this.hasEgg());

        // Save egg position if present
        getEggPosition().ifPresent(pos -> {
            compound.putInt("EggX", pos.getX());
            compound.putInt("EggY", pos.getY());
            compound.putInt("EggZ", pos.getZ());
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSliding(compound.getBoolean("IsSliding"));
        this.setIncubating(compound.getBoolean("IsIncubating"));
        this.setSlideCooldown(compound.getInt("SlideCooldown"));
        this.setIncubationTime(compound.getInt("IncubationTime"));
        this.setHasEgg(compound.getBoolean("HasEgg"));

        // Load egg position if present
        if (compound.contains("EggX")) {
            BlockPos eggPos = new BlockPos(
                    compound.getInt("EggX"),
                    compound.getInt("EggY"),
                    compound.getInt("EggZ")
            );
            this.setEggPosition(eggPos);
        }
    }

    // Static spawn conditions
    public static boolean checkPenguinSpawnRules(EntityType<PenguinEntity> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.Blocks.PENGUINS_SPAWN_ON) && Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random);
    }

    public int getFishingTime() {
        return this.entityData.get(FISHING_TIME);
    }

    public void setFishingTime(int time) {
        this.entityData.set(FISHING_TIME, time);
    }

    @Override
    public boolean shouldEnterWater() {
        return this.isHungry() && !this.isIncubating();
    }

    @Override
    public boolean shouldLeaveWater() {
        return !this.isHungry() || this.hasEgg();
    }

    @Override
    public boolean shouldStopMoving() {
        return false;
    }

    @Override
    public int getWaterSearchRange() {
        return 0;
    }

    @Override
    public boolean isColliding(BlockPos pos, BlockState state) {
        return !state.is(ModBlocks.PENGUIN_EGG) && super.isColliding(pos, state);
    }

    // Custom AI Goals
    static class PenguinSlideGoal extends Goal {
        private final PenguinEntity penguin;
        private int slideTimer;

        public PenguinSlideGoal(PenguinEntity penguin) {
            this.penguin = penguin;
        }

        @Override
        public boolean canUse() {
            return this.penguin.canSlide() &&
                    this.penguin.isOnSlipperyGround() &&
                    this.penguin.getRandom().nextInt(200) == 0;
        }

        @Override
        public void start() {
            this.penguin.setSliding(true);
            this.penguin.setSlideCooldown(400); // 20 second cooldown
            this.slideTimer = 0;
        }

        @Override
        public void tick() {
            this.slideTimer++;
            // Apply sliding movement
            Vec3 movement = this.penguin.getDeltaMovement();
            this.penguin.setDeltaMovement(movement.multiply(1.5, 1.0, 1.5));
        }

        @Override
        public boolean canContinueToUse() {
            return this.penguin.isSliding() && this.slideTimer < 40;
        }

        @Override
        public void stop() {
            this.penguin.setSliding(false);
        }
    }

    static class PenguinFishingGoal extends Goal {
        private final PenguinEntity penguin;
        private int fishingTimer;

        public PenguinFishingGoal(PenguinEntity penguin) {
            this.penguin = penguin;
        }

        @Override
        public boolean canUse() {
            return this.penguin.isHungry() &&
                    this.penguin.isInWater() &&
                    this.penguin.getRandom().nextInt(100) == 0;
        }

        @Override
        public void start() {
            this.fishingTimer = 0;
            this.penguin.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.fishingTimer++;
            // Dive underwater occasionally
            if (this.fishingTimer % 20 == 0) {
                this.penguin.setDeltaMovement(this.penguin.getDeltaMovement().add(0, -0.1, 0));
            }

            // Chance to "catch" fish and satisfy hunger
            if (this.fishingTimer > 100 && this.penguin.getRandom().nextInt(50) == 0) {
                this.penguin.heal(2.0F);
                this.penguin.satisfyHunger();
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.penguin.isInWater() && this.fishingTimer < 200;
        }
    }

    static class PenguinIncubationGoal extends Goal {
        private final PenguinEntity penguin;
        private BlockPos eggPosition;
        private BlockPos incubationSpot;
        private int stationaryTime = 0;
        private int repositioningCooldown = 0;
        private static final int MAX_STATIONARY_TIME = 600; // 30 seconds
        private static final int REPOSITIONING_COOLDOWN = 200; // 10 seconds
        private static final int EGG_CHECK_INTERVAL = 100; // 5 seconds

        public PenguinIncubationGoal(PenguinEntity penguin) {
            this.penguin = penguin;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            // Can start incubation if:
            // 1. Has an egg to lay OR there's a nearby egg to incubate
            // 2. Not in water
            // 3. Not already incubating (unless we lost track of our egg)

            if (penguin.isInWater() || penguin.isBaby()) {
                return false;
            }

            // If we have an egg to lay
            if (penguin.hasEgg() && !penguin.isIncubating()) {
                return true;
            }

            // If we're already incubating, check if our egg still exists
            if (penguin.isIncubating()) {
                Optional<BlockPos> eggPosOpt = penguin.getEggPosition();
                if (eggPosOpt.isPresent()) {
                    BlockPos eggPos = eggPosOpt.get();
                    if (!penguin.level().getBlockState(eggPos).is(ModBlocks.PENGUIN_EGG.get())) {
                        // Egg is gone, stop incubating
                        penguin.setIncubating(false);
                        penguin.setEggPosition(null);
                        return false;
                    }
                    return true;
                }
            }

            // Look for nearby eggs that need incubation
            BlockPos nearbyEgg = penguin.findNearbyEgg();
            if (nearbyEgg != null && penguin.canReachEgg(nearbyEgg)) {
                // Check if another penguin is already incubating this egg
                boolean eggBeingIncubated = penguin.level().getEntitiesOfClass(
                        PenguinEntity.class,
                        AABB.ofSize(Vec3.atCenterOf(nearbyEgg), 6, 3, 6)
                ).stream().anyMatch(p -> p != penguin && p.isIncubating() &&
                        p.getEggPosition().map(pos -> pos.equals(nearbyEgg)).orElse(false));

                return !eggBeingIncubated;
            }

            return false;
        }

        @Override
        public void start() {
            penguin.getNavigation().stop();
            repositioningCooldown = 0;
            stationaryTime = 0;

            // If we have an egg, lay it first
            if (penguin.hasEgg() && !penguin.isIncubating()) {
                penguin.layEgg();
            }

            // Find our egg position
            Optional<BlockPos> eggPosOpt = penguin.getEggPosition();
            if (eggPosOpt.isPresent()) {
                eggPosition = eggPosOpt.get();
            } else {
                // Look for nearby egg to adopt
                eggPosition = penguin.findNearbyEgg();
                if (eggPosition != null) {
                    penguin.setEggPosition(eggPosition);
                    penguin.setIncubating(true);
                    penguin.setIncubationTime(0);
                }
            }

            if (eggPosition != null) {
                incubationSpot = penguin.findIncubationSpot(eggPosition);
            }
        }

        @Override
        public void tick() {
            if (eggPosition == null) {
                return;
            }

            // Verify egg still exists every so often
            if (penguin.tickCount % EGG_CHECK_INTERVAL == 0) {
                if (!penguin.level().getBlockState(eggPosition).is(ModBlocks.PENGUIN_EGG.get())) {
                    // Egg disappeared, stop incubating
                    this.stop();
                    return;
                }
            }

            // Reduce repositioning cooldown
            if (repositioningCooldown > 0) {
                repositioningCooldown--;
            }

            double distanceToEgg = penguin.blockPosition().distSqr(eggPosition);

            // If we're too far from the egg, move closer
            if (distanceToEgg > 4.0) { // More than 2 blocks away
                if (incubationSpot != null) {
                    penguin.getNavigation().moveTo(
                            incubationSpot.getX() + 0.5,
                            incubationSpot.getY(),
                            incubationSpot.getZ() + 0.5,
                            0.8D
                    );
                } else {
                    penguin.getNavigation().moveTo(
                            eggPosition.getX() + 0.5,
                            eggPosition.getY(),
                            eggPosition.getZ() + 0.5,
                            0.8D
                    );
                }
                stationaryTime = 0;
            } else {
                // We're close to the egg, stay mostly still
                penguin.getNavigation().stop();
                stationaryTime++;

                // Occasional small movements to simulate natural behavior
                if (stationaryTime > MAX_STATIONARY_TIME && repositioningCooldown <= 0) {
                    if (penguin.getRandom().nextFloat() < 0.3F) {
                        // Small repositioning movement
                        double offsetX = (penguin.getRandom().nextDouble() - 0.5) * 1.5;
                        double offsetZ = (penguin.getRandom().nextDouble() - 0.5) * 1.5;

                        BlockPos newSpot = eggPosition.offset(
                                (int) Math.round(offsetX),
                                0,
                                (int) Math.round(offsetZ)
                        );

                        if (penguin.level().getBlockState(newSpot).isAir() &&
                                penguin.level().getBlockState(newSpot.below()).isSolid()) {
                            penguin.getNavigation().moveTo(
                                    newSpot.getX() + 0.5,
                                    newSpot.getY(),
                                    newSpot.getZ() + 0.5,
                                    0.5D
                            );
                        }

                        repositioningCooldown = REPOSITIONING_COOLDOWN;
                    }
                    stationaryTime = 0;
                }
            }

            // Slow down movement significantly during incubation
            if (distanceToEgg <= 4.0) {
                penguin.setDeltaMovement(penguin.getDeltaMovement().multiply(0.3, 1.0, 0.3));
            }

            // Occasionally look around but focus on the egg
            if (penguin.getRandom().nextInt(60) == 0) {
                if (penguin.getRandom().nextFloat() < 0.7F) {
                    // Look at the egg
                    penguin.getLookControl().setLookAt(
                            eggPosition.getX() + 0.5,
                            eggPosition.getY() + 0.5,
                            eggPosition.getZ() + 0.5
                    );
                } else {
                    // Look around occasionally
                    penguin.getLookControl().setLookAt(
                            penguin.getX() + (penguin.getRandom().nextDouble() - 0.5) * 8.0,
                            penguin.getEyeY(),
                            penguin.getZ() + (penguin.getRandom().nextDouble() - 0.5) * 8.0
                    );
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (!penguin.isIncubating() || eggPosition == null) {
                return false;
            }

            // Stop if in water (emergency situation)
            if (penguin.isInWater()) {
                return false;
            }

            // Stop if egg is gone
            if (!penguin.level().getBlockState(eggPosition).is(ModBlocks.PENGUIN_EGG.get())) {
                return false;
            }

            // Stop if incubation is complete
            if (penguin.getIncubationTime() >= PenguinEntity.INCUBATION_DURATION) {
                return false;
            }

            return true;
        }

        @Override
        public void stop() {
            penguin.getNavigation().stop();

            // If incubation wasn't completed naturally, reset state
            if (penguin.getIncubationTime() < PenguinEntity.INCUBATION_DURATION) {
                penguin.setIncubating(false);
                penguin.setIncubationTime(0);
                penguin.setEggPosition(null);
            }

            eggPosition = null;
            incubationSpot = null;
            stationaryTime = 0;
            repositioningCooldown = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    @Nullable
    private BlockPos findIncubationSpot(BlockPos eggPos) {
        // Priority order for incubation spots:
        // 1. Adjacent to egg on solid ground
        // 2. One block away if adjacent spots aren't suitable
        // 3. Fallback to egg position itself

        // Check all 4 adjacent horizontal positions first
        BlockPos[] adjacentPositions = {
                eggPos.north(), eggPos.south(), eggPos.east(), eggPos.west()
        };

        for (BlockPos pos : adjacentPositions) {
            if (isValidIncubationSpot(pos)) {
                return pos;
            }
        }

        // If no adjacent spots work, try positions one block further out
        BlockPos[] extendedPositions = {
                eggPos.north(2), eggPos.south(2), eggPos.east(2), eggPos.west(2),
                eggPos.north().east(), eggPos.north().west(),
                eggPos.south().east(), eggPos.south().west()
        };

        for (BlockPos pos : extendedPositions) {
            if (isValidIncubationSpot(pos)) {
                return pos;
            }
        }

        // Last resort: check if the egg position itself is suitable for standing on
        BlockPos aboveEgg = eggPos.above();
        if (isValidIncubationSpot(aboveEgg)) {
            return aboveEgg;
        }

        // Final fallback - return egg position and let the penguin figure it out
        return eggPos;
    }

    private boolean isValidIncubationSpot(BlockPos pos) {
        // Check if position is suitable for penguin incubation:
        // 1. Air block for the penguin to stand in
        // 2. Solid block below to stand on
        // 3. Not in water or lava
        // 4. Not inside another block

        BlockState stateAtPos = this.level().getBlockState(pos);
        BlockState stateBelow = this.level().getBlockState(pos.below());
        BlockState stateAbove = this.level().getBlockState(pos.above());

        // Must be air or passable (like grass)
        if (!stateAtPos.isAir() && !stateAtPos.canBeReplaced()) {
            return false;
        }

        // Must have solid ground below (but not penguin egg)
        if (!stateBelow.isSolid() || stateBelow.is(ModBlocks.PENGUIN_EGG.get())) {
            return false;
        }

        // Check for water/lava at position
        if (this.level().getFluidState(pos).is(FluidTags.WATER) ||
                this.level().getFluidState(pos).is(FluidTags.LAVA)) {
            return false;
        }

        // Make sure there's enough vertical space (penguin height)
        if (!stateAbove.isAir() && !stateAbove.canBeReplaced()) {
            return false;
        }

        // Prefer spots that are somewhat sheltered (optional enhancement)
        // Count adjacent solid blocks - more shelter is better, but not required
        int shelterCount = 0;
        for (BlockPos checkPos : new BlockPos[]{
                pos.north(), pos.south(), pos.east(), pos.west()
        }) {
            if (this.level().getBlockState(checkPos).isSolid()) {
                shelterCount++;
            }
        }

        // Any valid spot is acceptable, but this could be used for prioritization
        return true;
    }

    @Nullable
    private BlockPos findNearbyEgg() {
        BlockPos penguinPos = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                penguinPos.offset(-3, -1, -3),
                penguinPos.offset(3, 1, 3))) {
            if (this.level().getBlockState(pos).is(ModBlocks.PENGUIN_EGG.get())) {
                return pos;
            }
        }
        return null;
    }

    public boolean hasNearbyEgg() {
        return findNearbyEgg() != null;
    }

    // Check if penguin can reach and access the egg
    private boolean canReachEgg(BlockPos eggPos) {
        double distance = this.blockPosition().distSqr(eggPos);
        if (distance > 16.0) { // More than 4 blocks away
            return false;
        }

        // Check if there's a valid path to the egg
        Path path = this.getNavigation().createPath(eggPos, 0);
        if (path == null) {
            return false;
        }

        // Additional check: make sure the egg position is accessible (not blocked by walls)
        BlockPos penguinPos = this.blockPosition();

        // Simple line-of-sight check for nearby eggs
        if (distance <= 4.0) {
            return true; // Close enough, assume reachable
        }

        // For further eggs, check if navigation can create a reasonable path
        return path.canReach();
    }
}