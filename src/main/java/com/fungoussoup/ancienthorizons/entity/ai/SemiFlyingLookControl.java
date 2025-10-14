package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

/**
 * Custom look control for semi-flying entities that adjusts head rotation
 * differently when flying vs. on ground
 */
public class SemiFlyingLookControl extends LookControl {
    private final SemiFlyer flyer;
    private final float maxYRotSpeed;
    private final float maxXRotSpeed;

    public SemiFlyingLookControl(SemiFlyer flyer, float maxYRotSpeed, float maxXRotSpeed) {
        super((Mob) flyer);
        this.flyer = flyer;
        this.maxYRotSpeed = maxYRotSpeed;
        this.maxXRotSpeed = maxXRotSpeed;
    }

    public SemiFlyingLookControl(SemiFlyer flyer) {
        this(flyer, 10.0F, 10.0F);
    }

    @Override
    public void tick() {
        if (this.flyer.isFlying()) {
            // Flying look behavior - smoother and accounts for 3D movement
            if (this.lookAtCooldown > 0) {
                --this.lookAtCooldown;
                this.getYRotD().ifPresent(yRot -> {
                    this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, yRot, this.maxYRotSpeed);
                });
                this.getXRotD().ifPresent(xRot -> {
                    this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), xRot, this.maxXRotSpeed));
                });
            } else {
                // Align head with body when not actively looking at something
                this.mob.yHeadRot = this.rotateTowards(
                        this.mob.yHeadRot,
                        this.mob.yBodyRot,
                        this.maxYRotSpeed
                );
            }

            // Smooth pitch adjustment while flying
            if (this.flyer.shouldGlide()) {
                // Slight downward angle when gliding
                float targetPitch = 10.0F;
                this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), targetPitch, this.maxXRotSpeed * 0.5F));
            }
        } else {
            // Ground behavior - use default look control
            super.tick();
        }
    }

    /**
     * Sets the entity to look at a specific position, accounting for flight
     */
    @Override
    public void setLookAt(Vec3 lookVector) {
        this.setLookAt(lookVector.x, lookVector.y, lookVector.z);
    }

    /**
     * Sets the entity to look at coordinates with flight-aware adjustments
     */
    @Override
    public void setLookAt(double x, double y, double z) {
        super.setLookAt(x, y, z);

        // Extend look duration when flying (harder to track while in motion)
        if (this.flyer.isFlying()) {
            this.lookAtCooldown = Math.max(this.lookAtCooldown, 10);
        }
    }

    /**
     * Smoothly rotates from current angle to target angle
     */
    protected float rotateTowards(float current, float target, float maxDelta) {
        float delta = Mth.degreesDifference(current, target);
        float change = Mth.clamp(delta, -maxDelta, maxDelta);
        return current + change;
    }

    /**
     * Returns true if the entity is currently looking at its target
     */
    public boolean isLookingAtTarget() {
        if (!this.mob.getLookControl().isLookingAtTarget()) {
            return false;
        }

        float yawDiff = Math.abs(Mth.degreesDifference(
                this.mob.yHeadRot,
                this.getYRotD().orElse(this.mob.yHeadRot)
        ));
        float pitchDiff = Math.abs(Mth.degreesDifference(
                this.mob.getXRot(),
                this.getXRotD().orElse(this.mob.getXRot())
        ));

        // More lenient angle tolerance when flying
        float tolerance = this.flyer.isFlying() ? 15.0F : 5.0F;
        return yawDiff < tolerance && pitchDiff < tolerance;
    }

    /**
     * Calculates the pitch needed to look at a target position
     */
    public float calculatePitchToTarget(Vec3 targetPos) {
        Vec3 currentPos = this.mob.getEyePosition();
        Vec3 direction = targetPos.subtract(currentPos);
        double horizontalDist = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        return (float) -(Mth.atan2(direction.y, horizontalDist) * 180.0 / Math.PI);
    }

    /**
     * Calculates the yaw needed to look at a target position
     */
    public float calculateYawToTarget(Vec3 targetPos) {
        Vec3 currentPos = this.mob.position();
        Vec3 direction = targetPos.subtract(currentPos);
        return (float) (Mth.atan2(direction.z, direction.x) * 180.0 / Math.PI) - 90.0F;
    }
}
