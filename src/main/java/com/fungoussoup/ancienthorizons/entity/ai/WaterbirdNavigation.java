package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;

public class WaterbirdNavigation extends GroundPathNavigation {

    public WaterbirdNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        // Use a custom node evaluator that handles both ground and shallow water
        this.nodeEvaluator = new WalkNodeEvaluator();
        nodeEvaluator.setCanPassDoors(true);
        nodeEvaluator.setCanFloat(true);
        return new PathFinder(nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        // Allow path updates even if the entity is in water (floating)
        return true;
    }

    @Override
    protected Vec3 getTempMobPos() {
        // Adjust path origin when floating — keep slightly above water
        if (mob.isInWater()) {
            return mob.position().add(0.0D, 0.5D, 0.0D);
        }
        return super.getTempMobPos();
    }

    protected boolean canMoveDirectly(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
        // Allow direct movement over water or land
        BlockPos blockpos = new BlockPos((int) posVec32.x, (int) (posVec32.y + (double)this.mob.getBbHeight() * 0.5D), (int) posVec32.z);
        if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
            return true;
        }
        return super.canMoveDirectly(posVec31, posVec32);
    }

    @Override
    protected void followThePath() {
        // Smooth movement on water — no steep path corrections
        assert this.path != null;
        Vec3 nextPos = this.path.getNextEntityPos(this.mob);
        this.mob.getMoveControl().setWantedPosition(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
    }

    @Override
    protected boolean hasValidPathType(PathType pathType) {
        // Accept paths through walkable or water blocks
        return pathType == PathType.WALKABLE
                || pathType == PathType.WATER
                || super.hasValidPathType(pathType);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        // Consider both solid ground and water surfaces as valid destinations
        return level.getBlockState(pos).entityCanStandOn(level, pos, mob)
                || level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public boolean isInProgress() {
        // Continue navigation even while floating
        return !this.isDone();
    }

    @Override
    public boolean canFloat() {
        return true;
    }

    @Override
    public boolean isStuck() {
        // Prevent false stuck detection when floating in one place
        if (mob.isInWater()) {
            return false;
        }
        return super.isStuck();
    }
}

