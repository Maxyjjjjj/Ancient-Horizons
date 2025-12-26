package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.PathType;

import java.util.EnumSet;

public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final float minDistance;
    private final float maxDistance;
    private final boolean requiresLineOfSight;
    private Player targetPlayer;
    private int timeToRecalcPath;
    private final PathNavigation navigation;
    private float oldWaterCost;

    private static final int PATH_RECALC_INTERVAL = 10;
    private int ticksSinceLastMove = 0;
    private BlockPos lastPos;

    public FollowPlayerGoal(Mob mob, double speedModifier, float minDistance, float maxDistance) {
        this(mob, speedModifier, minDistance, maxDistance, true);
    }

    public FollowPlayerGoal(Mob mob, double speedModifier, float minDistance, float maxDistance, boolean requiresLineOfSight) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.requiresLineOfSight = requiresLineOfSight;
        this.navigation = mob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.lastPos = mob.blockPosition();
    }

    @Override
    public boolean canUse() {
        Player nearestPlayer = this.mob.level().getNearestPlayer(this.mob, this.maxDistance);

        if (nearestPlayer == null) {
            return false;
        }

        if (this.requiresLineOfSight && !this.mob.getSensing().hasLineOfSight(nearestPlayer)) {
            return false;
        }

        // Don't follow if player is too close
        double distSq = this.mob.distanceToSqr(nearestPlayer);
        if (distSq < this.minDistance * this.minDistance) {
            return false;
        }

        // Check if should follow this player
        if (!shouldFollowPlayer(nearestPlayer)) {
            return false;
        }

        this.targetPlayer = nearestPlayer;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
            return false;
        }

        double distance = this.mob.distanceToSqr(this.targetPlayer);

        // Stop if too far or too close
        if (distance > this.maxDistance * this.maxDistance) {
            return false;
        }

        if (distance < this.minDistance * this.minDistance) {
            return false;
        }

        // Additional conditions
        if (!this.canContinueToFollow()) {
            return false;
        }

        // Stop if stuck for too long
        return ticksSinceLastMove <= 100;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.ticksSinceLastMove = 0;
        this.lastPos = this.mob.blockPosition();

        // Optimize water pathing
        this.oldWaterCost = this.mob.getPathfindingMalus(PathType.WATER);
        this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.navigation.stop();
        this.ticksSinceLastMove = 0;

        // Restore water cost
        this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null) {
            return;
        }

        // Check if stuck
        BlockPos currentPos = this.mob.blockPosition();
        if (currentPos.equals(lastPos)) {
            ticksSinceLastMove++;
        } else {
            ticksSinceLastMove = 0;
        }
        lastPos = currentPos;

        // Look at player
        double distanceToPlayer = this.mob.distanceToSqr(this.targetPlayer);
        if (distanceToPlayer > this.minDistance * this.minDistance) {
            this.mob.getLookControl().setLookAt(
                    this.targetPlayer,
                    10.0F,
                    (float)this.mob.getMaxHeadXRot()
            );
        }

        // Path recalculation
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(PATH_RECALC_INTERVAL);

            // Try direct navigation first
            if (!this.navigation.moveTo(this.targetPlayer, this.speedModifier)) {
                // If direct path fails, try nearby positions
                if (!tryAlternativePath()) {
                    stop();
                }
            }
        }
    }

    /**
     * Try to find alternative path when direct path fails
     */
    private boolean tryAlternativePath() {
        if (this.targetPlayer == null) {
            return false;
        }

        BlockPos targetPos = this.targetPlayer.blockPosition();

        // Try positions around the player
        for (int attempt = 0; attempt < 8; attempt++) {
            BlockPos nearbyPos = targetPos.offset(
                    this.mob.getRandom().nextInt(7) - 3,
                    this.mob.getRandom().nextInt(3) - 1,
                    this.mob.getRandom().nextInt(7) - 3
            );

            // Check if position is valid and pathable
            if (this.mob.level().isLoaded(nearbyPos)) {
                if (this.navigation.moveTo(
                        nearbyPos.getX(),
                        nearbyPos.getY(),
                        nearbyPos.getZ(),
                        this.speedModifier)
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Override this to add conditions for following specific players
     */
    protected boolean shouldFollowPlayer(Player player) {
        return true;
    }

    /**
     * Override this to add additional conditions for continuing to follow
     */
    protected boolean canContinueToFollow() {
        return true;
    }

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }
}