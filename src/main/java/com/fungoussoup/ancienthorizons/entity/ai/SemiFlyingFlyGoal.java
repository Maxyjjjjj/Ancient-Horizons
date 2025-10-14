package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SemiFlyingFlyGoal extends Goal {
    private final PathfinderMob mob;
    private final double speedModifier;
    private final int interval;
    private final int maxFlightTime;
    private final int minRestTime;

    // State tracking
    private boolean isFlying;
    private int flightTime;
    private int restTime;

    // Navigation
    private Vec3 targetPos;

    public SemiFlyingFlyGoal(PathfinderMob mob, double speedModifier) {
        this(mob, speedModifier, 120, 400, 100);
    }

    public SemiFlyingFlyGoal(PathfinderMob mob, double speedModifier, int interval,
                             int maxFlightTime, int minRestTime) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.maxFlightTime = maxFlightTime;
        this.minRestTime = minRestTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(mob instanceof SemiFlyer semiFlyer) || !semiFlyer.canFly()) {
            return false;
        }

        if (restTime < minRestTime) {
            return false;
        }

        // Random chance or if been idle too long
        if (mob.getRandom().nextInt(adjustedTickDelay(interval)) != 0 &&
                mob.getNoActionTime() < 100) {
            return false;
        }

        // Find a position to fly to
        targetPos = findFlyingPosition();
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        return flightTime < maxFlightTime && targetPos != null;
    }

    @Override
    public void start() {
        SemiFlyer semiFlyer = (SemiFlyer) mob;
        semiFlyer.startFlying();
        isFlying = true;
        flightTime = 0;
        restTime = 0;
    }

    @Override
    public void stop() {
        SemiFlyer semiFlyer = (SemiFlyer) mob;
        semiFlyer.stopFlying();
        isFlying = false;
        targetPos = null;
    }

    @Override
    public void tick() {
        if (isFlying) {
            flightTime++;

            // Custom flying movement
            if (targetPos != null) {
                moveTowardsTarget();

                // Check if we've reached the target or need a new one
                if (mob.position().distanceTo(targetPos) < 2.0 ||
                        mob.getRandom().nextInt(60) == 0) {
                    targetPos = findFlyingPosition();
                }
            }
        } else {
            restTime++;
        }
    }

    private void moveTowardsTarget() {
        Vec3 currentPos = mob.position();
        Vec3 direction = targetPos.subtract(currentPos).normalize();

        // Apply movement with custom physics
        Vec3 movement = direction.scale(speedModifier * 0.1);

        // Add slight upward bias to keep flying
        movement = movement.add(0, 0.02, 0);

        // Apply the movement
        mob.setDeltaMovement(mob.getDeltaMovement().add(movement));

        // Face the direction we're moving
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, direction);
    }

    @Nullable
    private Vec3 findFlyingPosition() {
        // Try to find a position in the air
        Vec3 pos = HoverRandomPos.getPos(mob, 8, 6, mob.getX(), mob.getZ(),
                (float)Math.PI / 2, 3, 1);

        if (pos != null && isPositionSafe(pos)) {
            return pos;
        }

        // Fallback: find any nearby position above ground
        RandomSource random = mob.getRandom();
        for (int i = 0; i < 10; i++) {
            double x = mob.getX() + (random.nextDouble() - 0.5) * 16;
            double y = mob.getY() + (random.nextDouble() - 0.5) * 8 + 3;
            double z = mob.getZ() + (random.nextDouble() - 0.5) * 16;

            Vec3 testPos = new Vec3(x, y, z);
            if (isPositionSafe(testPos)) {
                return testPos;
            }
        }

        return null;
    }

    private boolean isPositionSafe(Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);
        return mob.level().isEmptyBlock(blockPos) &&
                mob.level().isEmptyBlock(blockPos.above()) &&
                !mob.level().isEmptyBlock(blockPos.below(3)); // Ensure not too high above ground
    }
}