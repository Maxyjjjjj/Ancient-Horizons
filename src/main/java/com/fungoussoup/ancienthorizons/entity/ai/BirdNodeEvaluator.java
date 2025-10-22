package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.*;

public class BirdNodeEvaluator extends WalkNodeEvaluator {
    private final Mob mob;
    private boolean flyingMode = false;

    public BirdNodeEvaluator(Mob mob) {
        this.mob = mob;
        this.setCanPassDoors(false);
        this.setCanFloat(true);
    }

    public void setFlyingMode(boolean flying) {
        this.flyingMode = flying;
    }

    public boolean isFlyingMode() {
        return flyingMode;
    }

    @Override
    public Node getStart() {
        if (flyingMode) {
            BlockPos pos = mob.blockPosition();
            return this.getNode(pos.getX(), (int)mob.getY(), pos.getZ());
        }
        return super.getStart();
    }

    @Override
    protected Node findAcceptedNode(int x, int y, int z, int verticalDeltaLimit, double nodeFloorLevel, net.minecraft.core.Direction direction, PathType pathType) {
        if (flyingMode) {
            Node node = this.getNode(x, y, z);
            node.type = PathType.OPEN;
            node.costMalus = 1.0F;
            return node;
        }
        return super.findAcceptedNode(x, y, z, verticalDeltaLimit, nodeFloorLevel, direction, pathType);
    }

    @Override
    public int getNeighbors(Node[] outputArray, Node node) {
        if (flyingMode) {
            int i = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx != 0 || dy != 0 || dz != 0) {
                            Node neighbor = this.getNode(node.x + dx, node.y + dy, node.z + dz);
                            neighbor.type = PathType.OPEN;
                            neighbor.costMalus = 1.0F;
                            outputArray[i++] = neighbor;
                        }
                    }
                }
            }
            return i;
        }
        return super.getNeighbors(outputArray, node);
    }

    @Override
    protected PathType getCachedPathType(int x, int y, int z) {
        if (flyingMode) {
            return PathType.OPEN;
        }
        return super.getCachedPathType(x, y, z);
    }

    @Override
    protected boolean canStartAt(BlockPos pos) {
        if (flyingMode) return true;
        return super.canStartAt(pos);
    }
}
