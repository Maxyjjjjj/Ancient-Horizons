package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class BirdMoveControl extends MoveControl {
    private final Mob mob;
    private final float maxTurnAngle;
    private final boolean lockBodyYaw;

    // Flying parameters
    private static final float FLY_SPEED_MODIFIER = 1.2F;
    private static final float VERTICAL_SPEED_MODIFIER = 0.8F;
    private static final float HOVER_HEIGHT_TOLERANCE = 0.5F;

    // Walking parameters
    private static final float WALK_SPEED_MODIFIER = 1.0F;

    public BirdMoveControl(Mob mob, float maxTurnAngle, boolean lockBodyYaw) {
        super(mob);
        this.mob = mob;
        this.maxTurnAngle = maxTurnAngle;
        this.lockBodyYaw = lockBodyYaw;
    }

    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            boolean isFlying = shouldFly();

            if (isFlying) {
                tickFlying();
            } else {
                tickWalking();
            }
        } else {
            // If not moving, apply gravity for walking or maintain height for flying
            if (shouldFly()) {
                maintainFlightHeight();
            } else {
                // Let default physics handle standing/idle on ground
                this.mob.setZza(0.0F);
                this.mob.setYya(0.0F);
                this.mob.setXxa(0.0F);
            }
        }
    }

    /**
     * Determine if the bird should use flying movement
     */
    private boolean shouldFly() {
        // Check if using BirdNavigation and it's in flying mode
        if (this.mob.getNavigation() instanceof BirdNavigation birdNav) {
            return birdNav.isFlying();
        }

        // Fallback: fly if not on ground or in water
        return !this.mob.onGround() || this.mob.isInWaterOrBubble();
    }

    private void tickFlying() {
        // Get target and current positions
        Vec3 targetPos = new Vec3(this.wantedX, this.wantedY, this.wantedZ);
        Vec3 currentPos = this.mob.position();
        Vec3 delta = targetPos.subtract(currentPos);

        double distance = delta.length();

        if (distance < 0.2) {
            // Reached destination, stop movement
            this.mob.setZza(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setSpeed(0.0F);
            return;
        }

        // Calculate base movement speed
        double speedAttr = this.mob.getAttributeValue(Attributes.FLYING_SPEED);
        if (speedAttr == 0.0) {
            speedAttr = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        }
        double speed = this.speedModifier * speedAttr * FLY_SPEED_MODIFIER;

        // Normalize the delta vector to get the direction
        Vec3 normalizedDelta = delta.normalize();

        // Calculate target yaw (for visual and horizontal turning)
        double horizontalDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (horizontalDist > 0.0001) {
            float targetYaw = (float)(Mth.atan2(delta.z, delta.x) * (180.0 / Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, this.maxTurnAngle));

            if (lockBodyYaw) {
                this.mob.yBodyRot = this.mob.getYRot();
                this.mob.yHeadRot = this.mob.getYRot();
            }

            // Calculate pitch for visual effect
            float targetPitch = (float)(-(Mth.atan2(delta.y, horizontalDist) * (180.0 / Math.PI)));
            this.mob.setXRot(Mth.clamp(targetPitch, -20.0F, 20.0F));
        }

        // Use forward movement (zza) combined with vertical control
        // This works better with Minecraft's entity movement system
        float moveSpeed = (float)speed;
        this.mob.setSpeed(moveSpeed);

        // Set forward movement
        this.mob.setZza(moveSpeed);

        // Handle vertical movement separately
        Vec3 motion = this.mob.getDeltaMovement();
        if (Math.abs(delta.y) > HOVER_HEIGHT_TOLERANCE) {
            // Apply vertical velocity for climbing/descending
            double verticalSpeed = normalizedDelta.y * speed * VERTICAL_SPEED_MODIFIER;
            this.mob.setDeltaMovement(motion.x, verticalSpeed, motion.z);
        }
    }

    /**
     * Handle walking movement
     */
    private void tickWalking() {
        // Calculate horizontal direction to target
        double deltaX = this.wantedX - this.mob.getX();
        double deltaY = this.wantedY - this.mob.getY();
        double deltaZ = this.wantedZ - this.mob.getZ();

        double horizontalDistSq = deltaX * deltaX + deltaZ * deltaZ;

        if (horizontalDistSq < 2.5E-7) {
            // Very close to target
            this.mob.setZza(0.0F);
            return;
        }

        // Calculate target rotation
        float targetYaw = (float)(Mth.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 90.0F;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, 90.0F));

        // Set movement speed
        double speedAttr = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float moveSpeed = (float)(this.speedModifier * speedAttr * WALK_SPEED_MODIFIER);
        this.mob.setSpeed(moveSpeed);

        // Handle jumping for obstacles
        if (deltaY > (double)this.mob.maxUpStep() && horizontalDistSq < (double)Math.max(1.0F, this.mob.getBbWidth())) {
            // Jump if there's a small obstacle
            this.mob.getJumpControl().jump();
            this.operation = MoveControl.Operation.JUMPING;
        } else if (deltaY > 2.0 && horizontalDistSq < 4.0) {
            // If obstacle is too high for walking, switch to flying
            if (this.mob.getNavigation() instanceof BirdNavigation birdNav) {
                birdNav.setFlying(true);
            }
        }

        this.mob.setZza(moveSpeed);
    }

    private void maintainFlightHeight() {
        Vec3 motion = this.mob.getDeltaMovement();

        // Apply slight resistance to falling when hovering
        if (!this.mob.onGround() && motion.y < 0) {
            // Reduces downward velocity to simulate gliding/hovering
            this.mob.setDeltaMovement(motion.x * 0.91, motion.y * 0.6, motion.z * 0.91);
        }
    }

    /**
     * Smoothly interpolate rotation
     */
    protected float rotlerp(float current, float target, float maxDelta) {
        float delta = Mth.wrapDegrees(target - current);
        delta = Mth.clamp(delta, -maxDelta, maxDelta);
        return current + delta;
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
    }

    /**
     * Force the bird to start flying
     */
    public void setFlying(boolean flying) {
        if (this.mob.getNavigation() instanceof BirdNavigation birdNav) {
            birdNav.setFlying(flying);
        }
    }
}