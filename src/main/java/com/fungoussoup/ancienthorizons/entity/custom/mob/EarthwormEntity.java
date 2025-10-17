package com.fungoussoup.ancienthorizons.entity.custom.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EarthwormEntity extends AbstractUndergroundAnimal {
    private static final int ENRICHMENT_RANGE = 8;
    private static final int ENRICHMENT_COOLDOWN = 600; // 30 seconds
    private static final int SURFACE_TIME_LIMIT = 1200; // 1 minute before seeking underground

    private int enrichmentCooldown = 0;
    private int surfaceTime = 0;
    private BlockPos targetFarmland = null;
    private boolean isEnriching = false;
    private int enrichmentProgress = 0;
    private static final int ENRICHMENT_DURATION = 100; // 5 seconds

    public EarthwormEntity(EntityType<? extends AbstractUndergroundAnimal> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Remove breeding goal since earthworms reproduce differently
        this.goalSelector.removeGoal(new BreedGoal(this, 1.0));

        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new EnrichFarmlandGoal(this, 0.8));
        this.goalSelector.addGoal(3, new SeekUndergroundGoal(this, 1.0));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    public void tick() {
        super.tick();

        // Track surface time
        if (!this.isUnderground()) {
            this.surfaceTime++;
        } else {
            this.surfaceTime = 0;
        }

        // Update cooldowns
        if (this.enrichmentCooldown > 0) {
            this.enrichmentCooldown--;
        }

        // Handle enrichment process
        if (this.isEnriching && this.targetFarmland != null) {
            this.enrichmentProgress++;

            // Create particles during enrichment
            if (this.level() instanceof ServerLevel serverLevel) {
                if (this.enrichmentProgress % 10 == 0) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.targetFarmland.getX() + 0.5,
                            this.targetFarmland.getY() + 1.0,
                            this.targetFarmland.getZ() + 0.5,
                            1, 0.2, 0.0, 0.2, 0.0);
                }
            }

            // Complete enrichment
            if (this.enrichmentProgress >= ENRICHMENT_DURATION) {
                this.completeEnrichment();
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Future feature: collecting earthworms with tools
        if (player.isShiftKeyDown()) {
            // Play a subtle sound effect
            this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH_SMALL,
                    SoundSource.NEUTRAL, 0.5f, 1.5f);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isPushable() {
        return false; // Earthworms are too small to be pushed
    }

    @Override
    public boolean isPushedByFluid() {
        return true; // Water flow affects earthworms
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null; // Earthworms don't breed through conventional means
    }

    // Helper methods
    public boolean isUnderground() {
        return this.blockPosition().getY() < this.level().getSeaLevel() - 10;
    }

    public boolean canEnrichFarmland() {
        return this.enrichmentCooldown <= 0 && !this.isEnriching;
    }

    public void startEnrichment(BlockPos farmlandPos) {
        this.targetFarmland = farmlandPos;
        this.isEnriching = true;
        this.enrichmentProgress = 0;
        this.getNavigation().stop();
    }

    private void completeEnrichment() {
        if (this.targetFarmland != null && this.level() instanceof ServerLevel serverLevel) {
            BlockState farmlandState = serverLevel.getBlockState(this.targetFarmland);

            if (farmlandState.is(Blocks.FARMLAND)) {
                // Increase moisture level
                int currentMoisture = farmlandState.getValue(FarmBlock.MOISTURE);
                if (currentMoisture < 7) {
                    BlockState newState = farmlandState.setValue(FarmBlock.MOISTURE, Math.min(7, currentMoisture + 2));
                    serverLevel.setBlock(this.targetFarmland, newState, 3);
                }

                // Spawn particles to indicate successful enrichment
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        this.targetFarmland.getX() + 0.5,
                        this.targetFarmland.getY() + 1.0,
                        this.targetFarmland.getZ() + 0.5,
                        5, 0.3, 0.1, 0.3, 0.1);

                // Play completion sound
                serverLevel.playSound(null, this.targetFarmland, SoundEvents.COMPOSTER_READY,
                        SoundSource.BLOCKS, 0.3f, 1.2f);
            }
        }

        // Reset enrichment state
        this.isEnriching = false;
        this.targetFarmland = null;
        this.enrichmentProgress = 0;
        this.enrichmentCooldown = ENRICHMENT_COOLDOWN;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        return level.getLevelData().isRaining();
    }

    // Custom AI Goals
    public static class EnrichFarmlandGoal extends Goal {
        private final EarthwormEntity earthworm;
        private final double speed;
        private BlockPos targetPos;
        private int searchCooldown = 0;

        public EnrichFarmlandGoal(EarthwormEntity earthworm, double speed) {
            this.earthworm = earthworm;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.earthworm.canEnrichFarmland()) {
                return false;
            }

            if (this.searchCooldown > 0) {
                this.searchCooldown--;
                return false;
            }

            this.targetPos = this.findNearbyFarmland();
            return this.targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetPos != null && !this.earthworm.isEnriching &&
                    this.earthworm.distanceToSqr(Vec3.atCenterOf(this.targetPos)) > 4.0;
        }

        @Override
        public void start() {
            if (this.targetPos != null) {
                this.earthworm.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speed);
            }
        }

        @Override
        public void tick() {
            if (this.targetPos != null) {
                this.earthworm.getLookControl().setLookAt(Vec3.atCenterOf(this.targetPos));

                // Check if we're close enough to start enrichment
                if (this.earthworm.distanceToSqr(Vec3.atCenterOf(this.targetPos)) < 4.0) {
                    this.earthworm.startEnrichment(this.targetPos);
                    this.stop();
                }
            }
        }

        @Override
        public void stop() {
            this.targetPos = null;
            this.searchCooldown = 200; // 10 seconds before next search
            this.earthworm.getNavigation().stop();
        }

        private BlockPos findNearbyFarmland() {
            BlockPos entityPos = this.earthworm.blockPosition();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int x = -ENRICHMENT_RANGE; x <= ENRICHMENT_RANGE; x++) {
                for (int z = -ENRICHMENT_RANGE; z <= ENRICHMENT_RANGE; z++) {
                    for (int y = -2; y <= 2; y++) {
                        mutablePos.set(entityPos.getX() + x, entityPos.getY() + y, entityPos.getZ() + z);
                        BlockState state = this.earthworm.level().getBlockState(mutablePos);

                        if (state.is(Blocks.FARMLAND)) {
                            int moisture = state.getValue(FarmBlock.MOISTURE);
                            if (moisture < 7) { // Only target farmland that can be improved
                                return mutablePos.immutable();
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class SeekUndergroundGoal extends Goal {
        private final EarthwormEntity earthworm;
        private final double speed;
        private Vec3 targetPosition;

        public SeekUndergroundGoal(EarthwormEntity earthworm, double speed) {
            this.earthworm = earthworm;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            // Seek underground if on surface too long or if it's daytime
            return (this.earthworm.surfaceTime > SURFACE_TIME_LIMIT ||
                    (this.earthworm.level().isDay() && !this.earthworm.isUnderground())) &&
                    !this.earthworm.isEnriching;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetPosition != null && !this.earthworm.isUnderground();
        }

        @Override
        public void start() {
            this.targetPosition = this.findUndergroundPosition();
            if (this.targetPosition != null) {
                this.earthworm.getNavigation().moveTo(this.targetPosition.x, this.targetPosition.y, this.targetPosition.z, this.speed);
            }
        }

        @Override
        public void tick() {
            if (this.targetPosition != null && this.earthworm.distanceToSqr(this.targetPosition) < 4.0) {
                this.stop();
            }
        }

        @Override
        public void stop() {
            this.targetPosition = null;
            this.earthworm.getNavigation().stop();
        }

        private Vec3 findUndergroundPosition() {
            // Find a position underground
            for (int i = 0; i < 10; i++) {
                Vec3 randomPos = Vec3.atLowerCornerOf(RandomPos.generateRandomDirection(this.earthworm.getRandom(), 10, 5));
                BlockPos targetPos = new BlockPos((int) randomPos.x,
                        Math.min((int) randomPos.y, this.earthworm.level().getSeaLevel() - 15),
                        (int) randomPos.z);
                Level level = this.earthworm.level();
                if (level.getBlockState(targetPos).isCollisionShapeFullBlock(level, targetPos)) {
                    return Vec3.atCenterOf(targetPos);
                }
            }
            return null;
        }
    }
}
