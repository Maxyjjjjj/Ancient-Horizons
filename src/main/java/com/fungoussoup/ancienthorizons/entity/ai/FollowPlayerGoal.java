package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;

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
    }

    @Override
    public boolean canUse() {
        Player nearestPlayer = this.mob.level().getNearestPlayer(this.mob, this.maxDistance);
        if (nearestPlayer == null) return false;

        if (this.requiresLineOfSight && !this.mob.getSensing().hasLineOfSight(nearestPlayer)) {
            return false;
        }

        // Don't follow if player is too close
        if (this.mob.distanceToSqr(nearestPlayer) < this.minDistance * this.minDistance) {
            return false;
        }

        // Check if mob should follow this player (can be overridden)
        if (!shouldFollowPlayer(nearestPlayer)) return false;

        this.targetPlayer = nearestPlayer;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) return false;

        double distance = this.mob.distanceToSqr(this.targetPlayer);
        if (distance > this.maxDistance * this.maxDistance) return false;
        if (distance < this.minDistance * this.minDistance) return false;

        return this.navigation.isDone() || this.canContinueToFollow();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null && this.mob.distanceToSqr(this.targetPlayer) > this.minDistance * this.minDistance) {
            this.mob.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.mob.getMaxHeadXRot());

            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);

                if (!this.navigation.moveTo(this.targetPlayer, this.speedModifier)) {
                    // Try to find a path to a nearby position
                    BlockPos targetPos = this.targetPlayer.blockPosition();
                    for (int i = 0; i < 8; i++) {
                        BlockPos nearbyPos = targetPos.offset(
                                this.mob.getRandom().nextInt(7) - 3,
                                this.mob.getRandom().nextInt(3) - 1,
                                this.mob.getRandom().nextInt(7) - 3
                        );
                        if (this.navigation.moveTo(nearbyPos.getX(), nearbyPos.getY(), nearbyPos.getZ(), this.speedModifier)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    protected boolean shouldFollowPlayer(Player player) {
        return true; // Override in subclasses for specific conditions
    }

    protected boolean canContinueToFollow() {
        return true; // Override for additional conditions
    }

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }
}