package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.misc.SemiAquaticAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * FIXED: Semi-aquatic swimming goal
 * - Better water/land transition
 * - Fixed infinite loop potential
 * - Smoother pathfinding
 * - Proper restriction radius handling
 */
public class SemiAquaticSwimGoal extends RandomStrollGoal {
    private final SemiAquaticAnimal semiAquatic;
    private static final int SURFACE_FIND_ATTEMPTS = 5;

    public SemiAquaticSwimGoal(Animal creature, double speed, int chance) {
        super(creature, speed, chance, false);
        if (!(creature instanceof SemiAquaticAnimal)) {
            throw new IllegalArgumentException("SemiAquaticSwimGoal requires SemiAquaticAnimal");
        }
        this.semiAquatic = (SemiAquaticAnimal) creature;
    }

    @Override
    public boolean canUse() {
        // Don't move if should stop
        if (this.mob.isVehicle() || this.semiAquatic.shouldStopMoving()) {
            return false;
        }

        // Don't move if has target
        if (this.mob.getTarget() != null) {
            return false;
        }

        // Only use when in water/lava or should enter water
        boolean inFluid = this.mob.isInWater() || this.mob.isInLava();
        boolean shouldEnterWater = this.semiAquatic.shouldEnterWater();

        if (!inFluid && !shouldEnterWater) {
            return false;
        }

        // Random interval check
        if (!this.forceTrigger) {
            if (this.mob.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
        }

        // Try to find position
        final Vec3 position = this.getPosition();
        if (position == null) {
            return false;
        }

        this.wantedX = position.x;
        this.wantedY = position.y;
        this.wantedZ = position.z;
        this.forceTrigger = false;
        return true;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        // Check restriction radius first
        if (this.mob.hasRestriction()) {
            Vec3 restrictCenter = Vec3.atBottomCenterOf(this.mob.getRestrictCenter());
            double restrictRadius = this.mob.getRestrictRadius();
            double distToCenter = this.mob.distanceToSqr(restrictCenter);

            if (distToCenter > restrictRadius * restrictRadius) {
                // Return to restriction center
                return DefaultRandomPos.getPosTowards(
                        this.mob,
                        7,
                        3,
                        restrictCenter,
                        1.0
                );
            }
        }

        // Occasionally try to reach surface
        if (this.mob.getRandom().nextFloat() < 0.3F) {
            Vec3 surfacePos = findSurfaceTarget();
            if (surfacePos != null) {
                return surfacePos;
            }
        }

        // Random position in water
        Vec3 randomPos = DefaultRandomPos.getPos(this.mob, 7, 3);

        // Validate position is in water or accessible
        if (randomPos != null && isValidSwimPosition(randomPos)) {
            return randomPos;
        }

        // Fallback: try a few more times
        for (int i = 0; i < 3; i++) {
            randomPos = DefaultRandomPos.getPos(this.mob, 5, 2);
            if (randomPos != null && isValidSwimPosition(randomPos)) {
                return randomPos;
            }
        }

        return null;
    }

    /**
     * Check if position is valid for swimming
     */
    private boolean isValidSwimPosition(Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);

        if (!this.mob.level().isLoaded(blockPos)) {
            return false;
        }

        // Should be in water or near water
        boolean isWater = this.mob.level().getFluidState(blockPos).is(FluidTags.WATER);
        boolean isLava = this.mob.level().getFluidState(blockPos).is(FluidTags.LAVA);

        // Or pathable ground near water
        boolean isNearWater = false;
        if (!isWater && !isLava) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos nearPos = blockPos.offset(dx, 0, dz);
                    if (this.mob.level().getFluidState(nearPos).is(FluidTags.WATER)) {
                        isNearWater = true;
                        break;
                    }
                }
                if (isNearWater) break;
            }
        }

        return isWater || isLava || isNearWater;
    }

    /**
     * Find surface position above current location
     */
    @Nullable
    private Vec3 findSurfaceTarget() {
        BlockPos currentPos = this.mob.blockPosition();

        // Safety limit
        int maxAscent = 32;
        BlockPos surfacePos = currentPos;

        // Ascend to find surface
        for (int i = 0; i < maxAscent; i++) {
            surfacePos = surfacePos.above();

            if (!this.mob.level().isLoaded(surfacePos)) {
                return null;
            }

            boolean isWater = this.mob.level().getFluidState(surfacePos).is(FluidTags.WATER);
            boolean isLava = this.mob.level().getFluidState(surfacePos).is(FluidTags.LAVA);
            boolean airAbove = this.mob.level().getBlockState(surfacePos.above()).isAir();

            // Found surface
            if ((isWater || isLava) && airAbove) {
                return Vec3.atCenterOf(surfacePos);
            }

            // Passed through to air
            if (!isWater && !isLava) {
                break;
            }
        }

        return null;
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if should stop moving
        if (this.semiAquatic.shouldStopMoving()) {
            return false;
        }

        return super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();

        // Set water path malus to 0 for efficient swimming
        this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        super.stop();

        // Reset water path malus
        this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
    }
}