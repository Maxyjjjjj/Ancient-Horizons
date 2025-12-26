package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class SemiFlyingMoveControl extends MoveControl {
    private final SemiFlyer flyer;
    private final float flyingSpeed;
    private final float glidingSpeed;
    private int ticksSinceLastMove = 0;
    private Vec3 lastPosition = Vec3.ZERO;
    private static final double MIN_MOVEMENT_THRESHOLD = 0.001;

    public SemiFlyingMoveControl(SemiFlyer flyer, float flyingSpeed, float glidingSpeed) {
        super((Mob) flyer);
        this.flyer = flyer;
        this.flyingSpeed = flyingSpeed;
        this.glidingSpeed = glidingSpeed;
    }

    public SemiFlyingMoveControl(SemiFlyer flyer) {
        this(flyer, 0.6F, 0.4F);
    }

    @Override
    public void tick() {
        if (this.flyer.isFlying()) {
            tickFlyingMovement();
        } else {
            super.tick();
            ticksSinceLastMove = 0;
            lastPosition = ((Mob) flyer).position();
        }
    }

    private void tickFlyingMovement() {
        Mob mob = (Mob) this.flyer;
        Vec3 currentPos = mob.position();

        // Update stuck detection
        if (currentPos.distanceToSqr(lastPosition) < MIN_MOVEMENT_THRESHOLD) {
            ticksSinceLastMove++;
        } else {
            ticksSinceLastMove = 0;
        }
        lastPosition = currentPos;

        if (this.operation != Operation.MOVE_TO) {
            // Idle flying - maintain altitude with minimal drift
            Vec3 movement = mob.getDeltaMovement();

            if (this.flyer.shouldGlide()) {
                // Gentle glide descent
                mob.setDeltaMovement(
                        movement.x * 0.95,
                        Math.max(movement.y - 0.03, -0.5),
                        movement.z * 0.95
                );
            } else {
                // Hovering - very gentle oscillation
                double targetY = movement.y * 0.92;
                if (Math.abs(targetY) < 0.01) {
                    targetY = 0.005 * Math.sin(mob.tickCount * 0.05);
                }
                mob.setDeltaMovement(movement.x * 0.88, targetY, movement.z * 0.88);
            }

            ticksSinceLastMove = 0;
            return;
        }

        // Active movement toward target
        Vec3 targetPos = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
        Vec3 delta = targetPos.subtract(currentPos);
        double distanceSq = delta.lengthSqr();

        // Check if reached destination
        if (distanceSq < 2.0D) {
            this.operation = Operation.WAIT;
            mob.setSpeed(0.0F);
            // Apply gentle braking
            Vec3 vel = mob.getDeltaMovement();
            mob.setDeltaMovement(vel.x * 0.5, vel.y * 0.7, vel.z * 0.5);
            ticksSinceLastMove = 0;
            return;
        }

        // Give up if stuck too long
        if (ticksSinceLastMove > 120) {
            this.operation = Operation.WAIT;
            ticksSinceLastMove = 0;
            return;
        }

        // Calculate movement
        Vec3 direction = delta.normalize();
        float speed = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
        speed *= (float) this.speedModifier;
        speed = Mth.clamp(speed, 0.05F, 1.2F);

        // Smooth acceleration toward target velocity
        Vec3 currentMovement = mob.getDeltaMovement();
        Vec3 targetMovement = direction.scale(speed * 0.08);

        // Gentle lerp - prevents snappy movements
        double lerpFactor = 0.08;
        Vec3 newMovement = new Vec3(
                Mth.lerp(lerpFactor, currentMovement.x, targetMovement.x),
                Mth.lerp(lerpFactor, currentMovement.y, targetMovement.y),
                Mth.lerp(lerpFactor, currentMovement.z, targetMovement.z)
        );

        // Velocity limiting
        double maxSpeed = 1.0;
        double currentSpeed = newMovement.length();
        if (currentSpeed > maxSpeed) {
            newMovement = newMovement.normalize().scale(maxSpeed);
        }

        mob.setDeltaMovement(newMovement);

        // Smooth rotation toward movement direction
        if (delta.horizontalDistanceSqr() > 1.0E-5) {
            float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0F;
            mob.setYRot(this.rotlerp(mob.getYRot(), targetYaw, 3.0F));
        }

        // Smooth pitch adjustment
        double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (horizontalDist > 0.01) {
            float targetPitch = (float) -(Mth.atan2(delta.y, horizontalDist) * 180.0 / Math.PI);
            targetPitch = Mth.clamp(targetPitch, -35.0F, 35.0F);
            mob.setXRot(this.rotlerp(mob.getXRot(), targetPitch, 2.0F));
        }

        mob.setSpeed(speed * 0.3F);
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        this.operation = Operation.MOVE_TO;
        ticksSinceLastMove = 0;
    }

    public boolean canReachTarget(Vec3 targetPos) {
        if (!this.flyer.canFly()) {
            return false;
        }
        Mob mob = (Mob) this.flyer;
        double distance = mob.position().distanceTo(targetPos);
        return distance < 48.0;
    }

    public float getCurrentSpeedModifier() {
        float base = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
        return Mth.clamp(base, 0.1F, 1.5F);
    }
}