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
    private int ticksSinceLastMove = 0;

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
            // Ground behavior - use default move control
            super.tick();
            ticksSinceLastMove = 0;
        }
    }

    private void tickFlyingMovement() {
        Mob mob = (Mob) this.flyer;
        
        // Prevent stuck state
        ticksSinceLastMove++;
        
        if (null == this.operation) {
            // Hovering or idle - gentle descent if not actively moving
            Vec3 movement = mob.getDeltaMovement();

            if (!this.flyer.shouldGlide()) {
                // Apply gentle hover (reduced oscillation)
                double targetY = movement.y * 0.95;
                mob.setDeltaMovement(movement.x * 0.9, targetY, movement.z * 0.9);
            } else {
                // Gliding descent
                mob.setDeltaMovement(movement.x * 0.98, movement.y - 0.04, movement.z * 0.98);
            }
            
            ticksSinceLastMove = 0;
        } else switch (this.operation) {
            case MOVE_TO:
                Vec3 targetPos = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
                Vec3 currentPos = mob.position();
                Vec3 delta = targetPos.subtract(currentPos);
                double distanceSq = delta.lengthSqr();
                // Check if we've reached the destination
                if (distanceSq < 2.5D) {
                    this.operation = Operation.WAIT;
                    mob.setSpeed(0.0F);
                    ticksSinceLastMove = 0;
                    return;
                }   // If stuck for too long, give up on this target
                if (ticksSinceLastMove > 100) {
                    this.operation = Operation.WAIT;
                    ticksSinceLastMove = 0;
                    return;
                }   // Normalize direction and apply speed
                Vec3 direction = delta.normalize();
                float speed = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
                speed *= (float) this.speedModifier;
                // Clamp speed to prevent excessive values
                speed = Mth.clamp(speed, 0.1F, 2.0F);
                // Apply movement with smoother acceleration
                Vec3 currentMovement = mob.getDeltaMovement();
                Vec3 targetMovement = direction.scale(speed * 0.1);
                // Lerp towards target movement (reduced factor for stability)
                Vec3 newMovement = currentMovement.lerp(targetMovement, 0.1);
                // Clamp total velocity
                double maxVelocity = 1.5;
                if (newMovement.lengthSqr() > maxVelocity * maxVelocity) {
                    newMovement = newMovement.normalize().scale(maxVelocity);
                }   mob.setDeltaMovement(newMovement);
                // Face movement direction
                if (delta.horizontalDistanceSqr() > 1.0E-5) {
                    float targetYaw = (float) (Mth.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0F;
                    mob.setYRot(this.rotlerp(mob.getYRot(), targetYaw, 5.0F)); // Reduced rotation speed
                }   // Adjust pitch based on vertical movement (gentler)
                double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
                if (horizontalDist > 0.001) {
                    float targetPitch = (float) -(Mth.atan2(delta.y, horizontalDist) * 180.0 / Math.PI);
                    targetPitch = Mth.clamp(targetPitch, -45.0F, 45.0F); // Reduced pitch range
                    mob.setXRot(this.rotlerp(mob.getXRot(), targetPitch, 3.0F));
                }   // Maintain forward momentum
                mob.setSpeed(speed * 0.5F); // Reduced for stability
                break;
            case STRAFE:
                // Handle strafing while flying (for combat, etc.)
                this.handleFlyingStrafe();
                break;
            default:
                // Hovering or idle - gentle descent if not actively moving
                Vec3 movement = mob.getDeltaMovement();
                if (!this.flyer.shouldGlide()) {
                    // Apply gentle hover (reduced oscillation)
                    double targetY = movement.y * 0.95;
                    mob.setDeltaMovement(movement.x * 0.9, targetY, movement.z * 0.9);
                } else {
                    // Gliding descent
                    mob.setDeltaMovement(movement.x * 0.98, movement.y - 0.04, movement.z * 0.98);
                }   ticksSinceLastMove = 0;
                break;
        }
    }

    /**
     * Handles strafing movement while flying
     */
    private void handleFlyingStrafe() {
        Mob mob = (Mob) this.flyer;
        float speed = (float) this.speedModifier * (this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed);
        speed = Mth.clamp(speed, 0.1F, 1.0F);

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
        Vec3 newMovement = movement.add(moveX * 0.15, 0, moveZ * 0.15);
        
        // Clamp velocity
        double maxVelocity = 1.0;
        if (newMovement.lengthSqr() > maxVelocity * maxVelocity) {
            newMovement = newMovement.normalize().scale(maxVelocity);
        }
        
        mob.setDeltaMovement(newMovement);
    }

    /**
     * Sets the entity to move to a position with flight-aware pathing
     */
    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        this.operation = Operation.MOVE_TO;
        ticksSinceLastMove = 0;
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
        return distance < 64.0; // Reduced from 128 for better performance
    }

    /**
     * Gets the current movement speed modifier
     */
    public float getCurrentSpeedModifier() {
        float base = this.flyer.shouldGlide() ? this.glidingSpeed : this.flyingSpeed;
        return Mth.clamp(base, 0.1F, 2.0F);
    }
}