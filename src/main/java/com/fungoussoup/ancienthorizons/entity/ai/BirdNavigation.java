package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
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

    public boolean createPath(Vec3 target, double speed) {
        BlockPos targetPos = BlockPos.containing(target);

        boolean shouldFly = target.y > this.mob.getY() + 1.5 || !this.mob.onGround();
        setFlying(shouldFly);

        return super.createPath(targetPos, 0) != null;
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
        if (this.mob.onGround() && this.birdNodeEvaluator.isFlyingMode()) {
            setFlying(false);
        } else if (!this.mob.onGround() && !this.birdNodeEvaluator.isFlyingMode()) {
            setFlying(true);
        }
        super.tick();
    }
}