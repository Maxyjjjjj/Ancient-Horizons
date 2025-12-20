package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
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
    private int targetUpdateCooldown;
    private int stuckTimer;
    private Vec3 lastPos;

    // Navigation
    private Vec3 targetPos;

    public SemiFlyingFlyGoal(PathfinderMob mob, double speedModifier) {
        this(mob, speedModifier, 120, 400, 100);
    }

    public SemiFlyingFlyGoal(PathfinderMob mob, double speedModifier, int interval, int maxFlightTime, int minRestTime) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.interval = interval;
        this.maxFlightTime = maxFlightTime;
        this.minRestTime = minRestTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.lastPos = mob.position();
    }

    @Override
    public boolean canUse() {
        if (!(mob instanceof SemiFlyer semiFlyer)) {
            return false;
        }
        
        if (!semiFlyer.canFly()) {
            return false;
        }

        if (restTime < minRestTime) {
            return false;
        }

        // Don't interrupt other important goals
        if (mob.isVehicle() || mob.isPassenger() || ((TamableAnimal) mob).isOrderedToSit()) {
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
        if (!(mob instanceof SemiFlyer)) {
            return false;
        }

        // Stop if ordered to sit or mounted
        if (mob.isVehicle() || mob.isPassenger() || ((TamableAnimal) mob).isOrderedToSit()) {
            return false;
        }

        // Stop if flight time exceeded or grounded
        if (flightTime >= maxFlightTime || (mob.onGround() && flightTime > 20)) {
            return false;
        }

        // Stop if stuck
        if (stuckTimer > 40) {
            return false;
        }

        return targetPos != null;
    }

    @Override
    public void start() {
        if (mob instanceof SemiFlyer semiFlyer) {
            semiFlyer.startFlying();
            isFlying = true;
            flightTime = 0;
            restTime = 0;
            targetUpdateCooldown = 0;
            stuckTimer = 0;
            lastPos = mob.position();
        }
    }

    @Override
    public void stop() {
        if (mob instanceof SemiFlyer semiFlyer) {
            semiFlyer.stopFlying();
            isFlying = false;
            targetPos = null;
            targetUpdateCooldown = 0;
            stuckTimer = 0;
        }
    }

    @Override
    public void tick() {
        if (!isFlying) {
            restTime++;
            return;
        }

        flightTime++;
        targetUpdateCooldown--;

        // Check if entity is stuck
        Vec3 currentPos = mob.position();
        if (currentPos.distanceToSqr(lastPos) < 0.01) {
            stuckTimer++;
        } else {
            stuckTimer = 0;
        }
        lastPos = currentPos;

        // Custom flying movement
        if (targetPos != null) {
            moveTowardsTarget();

            // Check if we've reached the target or need a new one
            if (mob.position().distanceTo(targetPos) < 2.0) {
                if (targetUpdateCooldown <= 0) {
                    targetPos = findFlyingPosition();
                    targetUpdateCooldown = 20; // Wait 1 second before next update
                }
            } else if (targetUpdateCooldown <= 0 && mob.getRandom().nextInt(60) == 0) {
                targetPos = findFlyingPosition();
                targetUpdateCooldown = 20;
            }
        } else if (targetUpdateCooldown <= 0) {
            // Lost target, try to find a new one
            targetPos = findFlyingPosition();
            targetUpdateCooldown = 20;
            
            if (targetPos == null) {
                // Can't find target, stop flying
                this.stop();
            }
        }
    }

    private void moveTowardsTarget() {
        if (targetPos == null) return;

        Vec3 currentPos = mob.position();
        Vec3 direction = targetPos.subtract(currentPos).normalize();

        // Apply movement with custom physics (reduced to prevent overshooting)
        Vec3 movement = direction.scale(speedModifier * 0.05);

        // Add slight upward bias to keep flying (reduced)
        movement = movement.add(0, 0.01, 0);

        // Apply the movement with damping to prevent excessive speed
        Vec3 currentVelocity = mob.getDeltaMovement();
        Vec3 newVelocity = currentVelocity.add(movement).scale(0.95);
        
        // Clamp velocity to prevent runaway speeds
        double maxSpeed = speedModifier * 0.5;
        if (newVelocity.length() > maxSpeed) {
            newVelocity = newVelocity.normalize().scale(maxSpeed);
        }
        
        mob.setDeltaMovement(newVelocity);

        // Face the direction we're moving
        if (direction.horizontalDistanceSqr() > 0.0001) {
            float targetYaw = (float)(Math.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0F;
            mob.setYRot(mob.getYRot() + net.minecraft.util.Mth.wrapDegrees(targetYaw - mob.getYRot()) * 0.1F);
        }
    }

    @Nullable
    private Vec3 findFlyingPosition() {
        // Try to find a position in the air using vanilla method
        Vec3 pos = HoverRandomPos.getPos(mob, 8, 6, mob.getX(), mob.getZ(),
                (float)Math.PI / 2, 3, 1);

        if (pos != null && isPositionSafe(pos)) {
            return pos;
        }

        // Fallback: find any nearby position above ground
        RandomSource random = mob.getRandom();
        for (int i = 0; i < 5; i++) { // Reduced attempts to prevent lag
            double x = mob.getX() + (random.nextDouble() - 0.5) * 12;
            double y = mob.getY() + (random.nextDouble() - 0.5) * 6 + 2;
            double z = mob.getZ() + (random.nextDouble() - 0.5) * 12;

            Vec3 testPos = new Vec3(x, y, z);
            if (isPositionSafe(testPos)) {
                return testPos;
            }
        }

        return null;
    }

    private boolean isPositionSafe(Vec3 pos) {
        try {
            BlockPos blockPos = BlockPos.containing(pos);
            
            // Check bounds to prevent crashes
            if (!mob.level().isLoaded(blockPos)) {
                return false;
            }
            
            // Check if position is clear
            if (!mob.level().isEmptyBlock(blockPos) || !mob.level().isEmptyBlock(blockPos.above())) {
                return false;
            }
            
            // Make sure not too high above ground
            boolean groundFound = false;
            for (int i = 1; i <= 20; i++) {
                if (!mob.level().isEmptyBlock(blockPos.below(i))) {
                    groundFound = true;
                    break;
                }
            }
            
            return groundFound;
        } catch (Exception e) {
            // Catch any unexpected errors to prevent crashes
            return false;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}