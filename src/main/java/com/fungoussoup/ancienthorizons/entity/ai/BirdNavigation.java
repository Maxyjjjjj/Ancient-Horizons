package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class BirdNavigation extends PathNavigation {
    private BirdNodeEvaluator birdNodeEvaluator;

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
        if (this.birdNodeEvaluator != null) {
            boolean isOnGround = this.mob.onGround();
            boolean isFlyingMode = this.birdNodeEvaluator.isFlyingMode();
            
            if (isOnGround && isFlyingMode) {
                setFlying(false);
            } else if (!isOnGround && !isFlyingMode) {
                setFlying(true);
            }
        }
        super.tick();
    }
}