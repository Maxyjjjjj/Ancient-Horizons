package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class BirdNavigation extends PathNavigation {
    private BirdNodeEvaluator birdNodeEvaluator;
    private boolean lastFlyingState = false;
    private int stateChangeCooldown = 0;

    public BirdNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        this.birdNodeEvaluator = new BirdNodeEvaluator(this.mob);
        this.birdNodeEvaluator.setCanFloat(true);
        this.birdNodeEvaluator.setCanPassDoors(false);
        this.nodeEvaluator = this.birdNodeEvaluator;
        return new PathFinder(this.nodeEvaluator, range);
    }

    public void setFlying(boolean flying) {
        if (this.birdNodeEvaluator != null) {
            this.birdNodeEvaluator.setFlyingMode(flying);
            this.lastFlyingState = flying;
        }
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        return this.moveTo(this.createPath(x, y, z, 0), speed);
    }

    public boolean createPath(Vec3 target, double speed) {
        BlockPos targetPos = BlockPos.containing(target);

        boolean shouldFly = target.y > this.mob.getY() + 1.5 || !this.mob.onGround();
        setFlying(shouldFly);

        return this.moveTo(this.createPath(targetPos, 0), speed);
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
        // Add cooldown to prevent rapid state changes
        if (stateChangeCooldown > 0) {
            stateChangeCooldown--;
            super.tick();
            return;
        }

        if (this.birdNodeEvaluator != null) {
            boolean isOnGround = this.mob.onGround();
            boolean isFlyingMode = this.birdNodeEvaluator.isFlyingMode();
            
            // Only change state if it's been stable for a few ticks
            if (isOnGround && isFlyingMode && lastFlyingState) {
                setFlying(false);
                stateChangeCooldown = 10; // 0.5 second cooldown
                this.stop(); // Clear current path when landing
            } else if (!isOnGround && !isFlyingMode && !lastFlyingState) {
                setFlying(true);
                stateChangeCooldown = 10;
            }
        }
        super.tick();
    }

    @Override
    public void stop() {
        super.stop();
        // Reset cooldown when manually stopped
        stateChangeCooldown = 0;
    }
}