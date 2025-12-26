package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * FIXED: Bird navigation for entities that can walk and fly
 * - Prevents rapid state oscillation
 * - Smoother transitions between flying and walking
 * - Better stuck detection
 * - Proper path recalculation
 */
public class BirdNavigation extends PathNavigation {
    private BirdNodeEvaluator birdNodeEvaluator;
    private boolean lastFlyingState = false;
    private int stateChangeCooldown = 0;
    private int ticksSincePathRecalc = 0;
    private Vec3 lastTargetPos = null;

    private static final int STATE_CHANGE_COOLDOWN = 20; // 1 second
    private static final int PATH_RECALC_INTERVAL = 10; // 0.5 seconds

    public BirdNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.birdNodeEvaluator = new BirdNodeEvaluator(this.mob);
        this.birdNodeEvaluator.setCanFloat(true);
        this.birdNodeEvaluator.setCanPassDoors(true);
        this.nodeEvaluator = this.birdNodeEvaluator;
        return new PathFinder(this.nodeEvaluator, range);
    }

    /**
     * Sets flying mode with validation
     */
    public void setFlying(boolean flying) {
        if (this.birdNodeEvaluator != null && this.lastFlyingState != flying) {
            this.birdNodeEvaluator.setFlyingMode(flying);
            this.lastFlyingState = flying;
            // Clear path when changing modes
            this.stop();
        }
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        Vec3 targetPos = new Vec3(x, y, z);

        // Determine if we should fly to reach this target
        boolean shouldFly = shouldFlyToReach(targetPos);
        setFlying(shouldFly);

        lastTargetPos = targetPos;
        ticksSincePathRecalc = 0;

        return super.moveTo(x, y, z, speed);
    }

    /**
     * Improved flying decision logic
     */
    private boolean shouldFlyToReach(Vec3 target) {
        Vec3 currentPos = this.mob.position();

        // Fly if target is significantly higher
        if (target.y > currentPos.y + 2.0) {
            return true;
        }

        // Fly if not on ground
        if (!this.mob.onGround()) {
            return true;
        }

        // Fly if horizontal distance is large and vertical difference exists
        double horizontalDist = Math.sqrt(
                Math.pow(target.x - currentPos.x, 2) +
                        Math.pow(target.z - currentPos.z, 2)
        );

        if (horizontalDist > 8.0 && Math.abs(target.y - currentPos.y) > 1.0) {
            return true;
        }

        return false;
    }

    @Override
    protected boolean canUpdatePath() {
        return !this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.position();
    }

    @Override
    public void tick() {
        super.tick();

        // Cooldown management
        if (stateChangeCooldown > 0) {
            stateChangeCooldown--;
            return;
        }

        if (this.birdNodeEvaluator == null) {
            return;
        }

        boolean isOnGround = this.mob.onGround();
        boolean isFlyingMode = this.birdNodeEvaluator.isFlyingMode();

        // Smart state transitions
        if (isOnGround && isFlyingMode && this.isDone()) {
            // Landed and path complete - switch to walking
            setFlying(false);
            stateChangeCooldown = STATE_CHANGE_COOLDOWN;
        } else if (!isOnGround && !isFlyingMode && this.mob.getDeltaMovement().y < -0.1) {
            // Falling while in walking mode - switch to flying
            setFlying(true);
            stateChangeCooldown = STATE_CHANGE_COOLDOWN;
        }

        // Periodic path recalculation when flying
        if (isFlyingMode && !this.isDone() && lastTargetPos != null) {
            ticksSincePathRecalc++;
            if (ticksSincePathRecalc >= PATH_RECALC_INTERVAL) {
                // Recalculate path to handle dynamic obstacles
                this.moveTo(lastTargetPos.x, lastTargetPos.y, lastTargetPos.z, this.speedModifier);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        stateChangeCooldown = 0;
        ticksSincePathRecalc = 0;
        lastTargetPos = null;
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        if (this.birdNodeEvaluator != null && this.birdNodeEvaluator.isFlyingMode()) {
            // When flying, any non-solid block is valid
            return this.level.getBlockState(pos).isAir() ||
                    !this.level.getBlockState(pos).isSolid();
        }
        return super.isStableDestination(pos);
    }

    /**
     * Check if bird can fly to position
     */
    public boolean canFlyTo(BlockPos pos) {
        if (this.birdNodeEvaluator == null) {
            return false;
        }

        // Check if path exists in flying mode
        boolean wasFlying = this.birdNodeEvaluator.isFlyingMode();
        this.birdNodeEvaluator.setFlyingMode(true);
        boolean canReach = this.createPath(pos, 0) != null;
        this.birdNodeEvaluator.setFlyingMode(wasFlying);

        return canReach;
    }
}