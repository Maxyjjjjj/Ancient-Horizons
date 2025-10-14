package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * Custom move control for semi-flying entities that handles both
 * ground-based and aerial movement
 */
public class SemiFlyingMoveControl extends MoveControl {
    private final SemiFlyer flyer;
    private final float flyingSpeed;
    private final float glidingSpeed;

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
            // Custom flight movement logic
            if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;

                Mob mob = (Mob) this.flyer;
                Vec3 targetPos = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
                Vec3 currentPos = mob.position();
                Vec3 delta = targetPos.subtract(currentPos);

                double distanceSq = delta.lengthSqr();

                // Check if we've reached the destination
                if (distanceSq < 2.5D) {
                    mob.setSpeed(0.0F);
                    return;
                }

                // Normalize direction and apply speed
                Vec3 direction = delta.normalize();
                float speed = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
                speed *= (float) this.speedModifier;

                // Apply movement with smoother acceleration
                Vec3 currentMovement = mob.getDeltaMovement();
                Vec3 targetMovement = direction.scale(speed);
                Vec3 newMovement = currentMovement.lerp(targetMovement, 0.25);

                mob.setDeltaMovement(newMovement);

                // Face movement direction
                if (delta.horizontalDistanceSqr() > 1.0E-5) {
                    float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0F;
                    mob.setYRot(this.rotlerp(mob.getYRot(), targetYaw, 10.0F));
                }

                // Adjust pitch based on vertical movement
                double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
                if (horizontalDist > 0.001) {
                    float targetPitch = (float) -(Mth.atan2(delta.y, horizontalDist) * 180.0 / Math.PI);
                    // Clamp pitch to reasonable flying angles
                    targetPitch = Mth.clamp(targetPitch, -60.0F, 60.0F);
                    mob.setXRot(this.rotlerp(mob.getXRot(), targetPitch, 5.0F));
                }

                // Maintain forward momentum
                mob.setSpeed(speed);
                this.operation = Operation.MOVE_TO;
            } else if (this.operation == Operation.STRAFE) {
                // Handle strafing while flying (for combat, etc.)
                this.handleFlyingStrafe();
            } else {
                // Hovering or idle - gentle descent if not actively moving
                Mob mob = (Mob) this.flyer;
                Vec3 movement = mob.getDeltaMovement();

                if (!this.flyer.shouldGlide()) {
                    // Apply gentle hover oscillation
                    double targetY = movement.y * 0.95 + (Math.sin(mob.tickCount * 0.1) * 0.02);
                    mob.setDeltaMovement(movement.x * 0.9, targetY, movement.z * 0.9);
                } else {
                    // Gliding descent
                    mob.setDeltaMovement(movement.x * 0.98, movement.y - 0.08, movement.z * 0.98);
                }
            }
        } else {
            // Ground behavior - use default move control
            super.tick();
        }
    }

    /**
     * Handles strafing movement while flying
     */
    private void handleFlyingStrafe() {
        Mob mob = (Mob) this.flyer;
        float speed = (float) this.speedModifier * (this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed);

        float forward = this.strafeForwards;
        float strafe = this.strafeRight;
        float totalMovement = Mth.sqrt(forward * forward + strafe * strafe);

        if (totalMovement < 1.0F) {
            totalMovement = 1.0F;
        }

        forward = forward / totalMovement;
        strafe = strafe / totalMovement;

        float sin = Mth.sin(mob.getYRot() * ((float) Math.PI / 180F));
        float cos = Mth.cos(mob.getYRot() * ((float) Math.PI / 180F));

        float moveX = (forward * cos - strafe * sin) * speed;
        float moveZ = (strafe * cos + forward * sin) * speed;

        Vec3 movement = mob.getDeltaMovement();
        mob.setDeltaMovement(movement.x + moveX * 0.3, movement.y, movement.z + moveZ * 0.3);
    }

    /**
     * Sets the entity to move to a position with flight-aware pathing
     */
    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        this.operation = Operation.MOVE_TO;
    }

    /**
     * Checks if the entity can reach the target position
     */
    public boolean canReachTarget(Vec3 targetPos) {
        if (!this.flyer.canFly()) {
            return false;
        }

        Mob mob = (Mob) this.flyer;
        double distance = mob.position().distanceTo(targetPos);

        // Flying entities can reach further
        return distance < 128.0;
    }

    /**
     * Gets the current movement speed modifier
     */
    public float getCurrentSpeedModifier() {
        float base = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
        float energy = this.flyer.getFlightEnergy();
        return base * energy;
    }
}