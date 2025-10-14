package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class BirdNavigation extends PathNavigation {
    private final FlyNodeEvaluator flyEvaluator;
    private final WalkNodeEvaluator walkEvaluator;

    private boolean currentlyFlying;

    public BirdNavigation(Mob mob, Level level, int range) {
        super(mob, level);

        // Initialize evaluators
        this.flyEvaluator = new FlyNodeEvaluator();
        this.flyEvaluator.setCanPassDoors(true);
        this.flyEvaluator.setCanOpenDoors(false);
        this.flyEvaluator.setCanFloat(true);

        this.walkEvaluator = new WalkNodeEvaluator();
        this.walkEvaluator.setCanPassDoors(true);
        this.walkEvaluator.setCanOpenDoors(false);

        // Create both pathfinders once
        PathFinder flyingPathFinder = new PathFinder(this.flyEvaluator, range);
        PathFinder groundPathFinder = new PathFinder(this.walkEvaluator, range);

        // Start in ground mode
        this.currentlyFlying = false;
        this.nodeEvaluator = this.walkEvaluator;
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        // Return the appropriate pathfinder based on current mode
        // This is called by the parent constructor, so we use ground as default
        this.nodeEvaluator = this.walkEvaluator;
        return new PathFinder(this.walkEvaluator, range);
    }

    /**
     * Set whether the bird should use flying or walking navigation.
     * Call this when your entity's flying state changes.
     */
    public void setFlying(boolean flying) {
        if (this.currentlyFlying != flying) {
            this.currentlyFlying = flying;

            // Switch evaluator and pathfinder
            if (flying) {
                this.nodeEvaluator = this.flyEvaluator;
            } else {
                this.nodeEvaluator = this.walkEvaluator;
            }

            // Clear current path when switching modes
            this.stop();
        }
    }

    /**
     * Get the current flying state
     */
    public boolean isFlying() {
        return this.currentlyFlying;
    }

    @Override
    protected boolean canUpdatePath() {
        // Allow path updates in both modes
        return true;
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (this.currentlyFlying) {
            // When flying, can navigate to air blocks or any solid surface
            return !this.level.getBlockState(pos).isSolid() ||
                    this.level.getBlockState(pos.below()).isFaceSturdy(
                            this.level, pos.below(), net.minecraft.core.Direction.UP);
        } else {
            // When walking, need solid ground below
            return this.level.getBlockState(pos.below()).isFaceSturdy(
                    this.level, pos.below(), net.minecraft.core.Direction.UP);
        }
    }

    @Override
    protected Vec3 getTempMobPos() {
        if (this.currentlyFlying) {
            // When flying, use center of mob's bounding box
            return new Vec3(
                    this.mob.getX(),
                    this.mob.getY() + this.mob.getBbHeight() * 0.5,
                    this.mob.getZ()
            );
        } else {
            // When walking, use ground position
            return this.mob.position();
        }
    }

    @Override
    protected boolean canMoveDirectly(Vec3 start, Vec3 end) {
        if (this.currentlyFlying) {
            // When flying, can move more directly through air
            return true;
        } else {
            // When walking, use standard pathfinding checks
            return super.canMoveDirectly(start, end);
        }
    }

    @Override
    protected void doStuckDetection(Vec3 pos) {
        if (this.currentlyFlying) {
            // More lenient stuck detection when flying
            if (this.tick - this.lastStuckCheck > 40) {
                if (pos.distanceToSqr(this.lastStuckCheckPos) < 4.0) {
                    this.stop();
                }
                this.lastStuckCheck = this.tick;
                this.lastStuckCheckPos = pos;
            }
        } else {
            // Standard stuck detection on ground
            super.doStuckDetection(pos);
        }
    }

    @Override
    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        if (!this.isDone()) {
            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                Vec3 vec3 = this.getTempMobPos();
                Vec3 vec31 = this.path.getNextEntityPos(this.mob);
                if (vec3.y > vec31.y && !this.mob.onGround() &&
                        Math.floor(vec3.x) == Math.floor(vec31.x) &&
                        Math.floor(vec3.z) == Math.floor(vec31.z)) {
                    this.path.advance();
                }
            }

            this.debugPathFinding();

            if (!this.isDone()) {
                Vec3 currentPos = this.path.getNextEntityPos(this.mob);

                // Move toward the path point
                this.mob.getMoveControl().setWantedPosition(
                        currentPos.x,
                        currentPos.y,
                        currentPos.z,
                        this.speedModifier
                );
            }
        }
    }

    private void debugPathFinding() {
    }

    /**
     * Check if a position is reachable by walking (useful for AI decisions)
     */
    public boolean isWalkableDestination(BlockPos pos) {
        // Check if the block below is solid
        if (!this.level.getBlockState(pos.below()).isFaceSturdy(
                this.level, pos.below(), net.minecraft.core.Direction.UP)) {
            return false;
        }

        // Check if there's space to stand
        return this.level.getBlockState(pos).isPathfindable(PathComputationType.LAND);
    }

    /**
     * Check if a position requires flying to reach
     */
    public boolean requiresFlying(BlockPos targetPos) {
        BlockPos mobPos = this.mob.blockPosition();

        // Check vertical distance
        double verticalDistance = Math.abs(targetPos.getY() - mobPos.getY());
        if (verticalDistance > 3) {
            return true;
        }

        // Check horizontal distance
        double horizontalDistance = Math.sqrt(
                Math.pow(targetPos.getX() - mobPos.getX(), 2) +
                        Math.pow(targetPos.getZ() - mobPos.getZ(), 2)
        );
        if (horizontalDistance > 15) {
            return true;
        }

        // Check if walkable
        return !isWalkableDestination(targetPos);
    }

    /**
     * Get the evaluator for flying mode
     */
    public FlyNodeEvaluator getFlyEvaluator() {
        return this.flyEvaluator;
    }

    /**
     * Get the evaluator for walking mode
     */
    public WalkNodeEvaluator getWalkEvaluator() {
        return this.walkEvaluator;
    }
}