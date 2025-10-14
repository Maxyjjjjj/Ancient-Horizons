package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.HoatzinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HoatzinFlightGoal extends Goal {
    private final HoatzinEntity hoatzin;
    private BlockPos targetPos;
    private int cooldown = 0;

    public HoatzinFlightGoal(HoatzinEntity hoatzin) {
        this.hoatzin = hoatzin;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // Don't fly if already flying, tired, or can't fly
        if (hoatzin.isFlying() || !hoatzin.canFly()) {
            return false;
        }

        // Fly when panicking or trying to escape danger
        if (hoatzin.getLastHurtByMob() != null) {
            return true;
        }

        // Occasionally fly to reach food or interesting spots
        if (hoatzin.getRandom().nextInt(200) == 0) {
            targetPos = findNearbyPerchingSpot();
            return targetPos != null;
        }

        // Fly when falling from height to glide
        return hoatzin.shouldGlide();
    }

    @Override
    public boolean canContinueToUse() {
        // Continue flying while we have stamina and haven't reached target
        if (!hoatzin.isFlying()) {
            return false;
        }

        // If we have a target, check if we've reached it
        if (targetPos != null) {
            double distance = hoatzin.distanceToSqr(Vec3.atCenterOf(targetPos));
            return !(distance < 4.0D);
        }

        return true;
    }

    @Override
    public void start() {
        hoatzin.startFlying();

        // If escaping danger, find a high safe spot
        if (hoatzin.getLastHurtByMob() != null) {
            targetPos = findEscapeSpot();
        }
    }

    @Override
    public void stop() {
        hoatzin.stopFlying();
        targetPos = null;
        cooldown = 100; // 5 second cooldown before trying to fly again
    }

    @Override
    public void tick() {
        if (targetPos != null) {
            // Fly towards target position
            Vec3 targetVec = Vec3.atCenterOf(targetPos);
            Vec3 currentPos = hoatzin.position();
            Vec3 direction = targetVec.subtract(currentPos).normalize();

            // Apply movement towards target
            hoatzin.setDeltaMovement(
                    direction.x * 0.3D,
                    Math.max(direction.y * 0.2D, -0.1D), // Controlled vertical movement
                    direction.z * 0.3D
            );
        } else if (hoatzin.getLastHurtByMob() != null) {
            // Escape behavior - fly away from attacker
            Vec3 escapeDirection = hoatzin.position().subtract(hoatzin.getLastHurtByMob().position()).normalize();
            hoatzin.setDeltaMovement(
                    escapeDirection.x * 0.4D,
                    0.3D, // Fly upward when escaping
                    escapeDirection.z * 0.4D
            );
        }
    }

    private BlockPos findNearbyPerchingSpot() {
        BlockPos currentPos = hoatzin.blockPosition();

        // Look for trees or high spots within 16 blocks
        for (int i = 0; i < 10; i++) {
            int x = currentPos.getX() + hoatzin.getRandom().nextInt(32) - 16;
            int z = currentPos.getZ() + hoatzin.getRandom().nextInt(32) - 16;
            int y = currentPos.getY() + hoatzin.getRandom().nextInt(16) + 2;

            BlockPos checkPos = new BlockPos(x, y, z);

            // Check if it's a valid perching spot (leaves or solid block with air above)
            if (isValidPerchingSpot(checkPos)) {
                return checkPos;
            }
        }

        return null;
    }

    private BlockPos findEscapeSpot() {
        BlockPos currentPos = hoatzin.blockPosition();
        Vec3 escapeDirection;

        if (hoatzin.getLastHurtByMob() != null) {
            escapeDirection = hoatzin.position().subtract(hoatzin.getLastHurtByMob().position()).normalize();
        } else {
            // Random escape direction if no clear attacker
            escapeDirection = new Vec3(
                    hoatzin.getRandom().nextGaussian(),
                    0,
                    hoatzin.getRandom().nextGaussian()
            ).normalize();
        }

        // Find a spot 8-16 blocks away in the escape direction, preferably up high
        int distance = 8 + hoatzin.getRandom().nextInt(8);
        int x = (int) (currentPos.getX() + escapeDirection.x * distance);
        int z = (int) (currentPos.getZ() + escapeDirection.z * distance);
        int y = currentPos.getY() + 5 + hoatzin.getRandom().nextInt(5);

        BlockPos escapePos = new BlockPos(x, y, z);

        // Try to find a valid spot nearby if the first one isn't good
        for (int attempts = 0; attempts < 5; attempts++) {
            if (isValidPerchingSpot(escapePos)) {
                return escapePos;
            }
            escapePos = escapePos.offset(
                    hoatzin.getRandom().nextInt(6) - 3,
                    hoatzin.getRandom().nextInt(4) - 2,
                    hoatzin.getRandom().nextInt(6) - 3
            );
        }

        return escapePos; // Return something even if not perfect
    }

    private boolean isValidPerchingSpot(BlockPos pos) {
        // Check if there's a solid block or leaves to perch on
        // and air above for landing
        boolean hasSolidGround = (!hoatzin.level().getBlockState(pos.below()).isAir());
        boolean hasAirAbove = hoatzin.level().getBlockState(pos).isAir() &&
                hoatzin.level().getBlockState(pos.above()).isAir();

        // Prefer leaves (trees) but accept any solid surface
        boolean isPreferredSurface = hoatzin.level().getBlockState(pos.below()).is(BlockTags.LEAVES);

        return hasSolidGround && hasAirAbove && (isPreferredSurface || hoatzin.getRandom().nextInt(3) == 0);
    }
}