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
    private static final int ENRICHMENT_COOLDOWN = 600; // 30s
    private static final int SURFACE_TIME_LIMIT = 1200; // 1min
    private static final int ENRICHMENT_DURATION = 100; // 5s

    private int enrichmentCooldown = 0;
    private int surfaceTime = 0;
    private BlockPos targetFarmland = null;
    private boolean isEnriching = false;
    private int enrichmentProgress = 0;

    public EarthwormEntity(EntityType<? extends AbstractUndergroundAnimal> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // do NOT call super.registerGoals() to skip breeding/temptation goals
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DigGoal(this, 1D));
        this.goalSelector.addGoal(2, new UndergroundWanderGoal(this, 1D));
        this.goalSelector.addGoal(3, new EnrichFarmlandGoal(this, 0.8));
        this.goalSelector.addGoal(4, new EmergeGoal(this, 1D));
        this.goalSelector.addGoal(5, new SeekUndergroundGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        if (!this.isUnderground()) {
            surfaceTime++;
        } else {
            surfaceTime = 0;
        }

        if (enrichmentCooldown > 0) enrichmentCooldown--;

        if (isEnriching && targetFarmland != null && this.level() instanceof ServerLevel serverLevel) {
            enrichmentProgress++;
            if (enrichmentProgress % 10 == 0) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        targetFarmland.getX() + 0.5,
                        targetFarmland.getY() + 1.0,
                        targetFarmland.getZ() + 0.5,
                        1, 0.2, 0.0, 0.2, 0.0);
            }
            if (enrichmentProgress >= ENRICHMENT_DURATION) {
                completeEnrichment();
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH_SMALL,
                    SoundSource.NEUTRAL, 0.5f, 1.5f);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mate) {
        return null;
    }

    public boolean canEnrichFarmland() {
        return this.enrichmentCooldown <= 0 && !this.isEnriching && !this.isUnderground();
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
                int currentMoisture = farmlandState.getValue(FarmBlock.MOISTURE);
                if (currentMoisture < 7) {
                    BlockState newState = farmlandState.setValue(FarmBlock.MOISTURE,
                            Math.min(7, currentMoisture + 2));
                    serverLevel.setBlock(this.targetFarmland, newState, 3);
                }
                serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                        this.targetFarmland.getX() + 0.5,
                        this.targetFarmland.getY() + 1.0,
                        this.targetFarmland.getZ() + 0.5,
                        5, 0.3, 0.1, 0.3, 0.1);
                serverLevel.playSound(null, this.targetFarmland,
                        SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.3f, 1.2f);
            }
        }
        this.isEnriching = false;
        this.targetFarmland = null;
        this.enrichmentProgress = 0;
        this.enrichmentCooldown = ENRICHMENT_COOLDOWN;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
        return level.getLevelData().isRaining();
    }

    // ===================== Goals =====================

    public static class EnrichFarmlandGoal extends Goal {
        private final EarthwormEntity worm;
        private final double speed;
        private BlockPos targetPos;
        private int searchCooldown = 0;

        public EnrichFarmlandGoal(EarthwormEntity worm, double speed) {
            this.worm = worm;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!worm.canEnrichFarmland()) return false;
            if (searchCooldown > 0) {
                searchCooldown--;
                return false;
            }
            targetPos = findNearbyFarmland();
            return targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return targetPos != null && !worm.isEnriching &&
                    worm.distanceToSqr(Vec3.atCenterOf(targetPos)) > 2.0;
        }

        @Override
        public void start() {
            if (targetPos != null)
                worm.getNavigation().moveTo(targetPos.getX(), targetPos.getY(), targetPos.getZ(), speed);
        }

        @Override
        public void tick() {
            if (targetPos == null) return;
            worm.getLookControl().setLookAt(Vec3.atCenterOf(targetPos));
            if (worm.distanceToSqr(Vec3.atCenterOf(targetPos)) < 2.0) {
                worm.startEnrichment(targetPos);
                stop();
            }
        }

        @Override
        public void stop() {
            targetPos = null;
            searchCooldown = 200;
            worm.getNavigation().stop();
        }

        private BlockPos findNearbyFarmland() {
            BlockPos origin = worm.blockPosition();
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            for (int x = -ENRICHMENT_RANGE; x <= ENRICHMENT_RANGE; x++) {
                for (int z = -ENRICHMENT_RANGE; z <= ENRICHMENT_RANGE; z++) {
                    for (int y = -2; y <= 2; y++) {
                        mutable.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                        BlockState state = worm.level().getBlockState(mutable);
                        if (state.is(Blocks.FARMLAND) && state.getValue(FarmBlock.MOISTURE) < 7)
                            return mutable.immutable();
                    }
                }
            }
            return null;
        }
    }

    public static class SeekUndergroundGoal extends Goal {
        private final EarthwormEntity worm;
        private final double speed;
        private Vec3 targetPosition;

        public SeekUndergroundGoal(EarthwormEntity worm, double speed) {
            this.worm = worm;
            this.speed = speed;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !worm.isUnderground() && !worm.isEnriching &&
                    (worm.surfaceTime > SURFACE_TIME_LIMIT ||
                            (worm.level().isDay() && !worm.level().isRaining()));
        }

        @Override
        public boolean canContinueToUse() {
            return targetPosition != null && !worm.isUnderground();
        }

        @Override
        public void start() {
            targetPosition = findUndergroundPosition();
            if (targetPosition != null)
                worm.getNavigation().moveTo(targetPosition.x, targetPosition.y, targetPosition.z, speed);
        }

        @Override
        public void tick() {
            if (targetPosition != null && worm.distanceToSqr(targetPosition) < 4.0)
                worm.startDigging();
        }

        private Vec3 findUndergroundPosition() {
            Vec3 origin = worm.position();
            double x = origin.x + (worm.getRandom().nextDouble() - 0.5) * 8;
            double z = origin.z + (worm.getRandom().nextDouble() - 0.5) * 8;
            double y = Math.max(origin.y - 10, worm.level().getMinBuildHeight());
            return new Vec3(x, y, z);
        }
    }
}