package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.HoatzinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HoatzinFlightGoal extends Goal {

    private final HoatzinEntity hoatzin;
    private BlockPos targetPos;

    private int flightTicks;
    private int cooldown;

    private static final int MAX_FLIGHT_TICKS = 100;
    private static final int FLIGHT_COOLDOWN = 100;

    private static final double MOVEMENT_SPEED = 0.25;
    private static final double ESCAPE_SPEED = 0.35;
    private static final double MAX_VELOCITY = 1.5;

    public HoatzinFlightGoal(HoatzinEntity hoatzin) {
        this.hoatzin = hoatzin;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /* =========================
       START CONDITIONS
       ========================= */

    @Override
    public boolean canUse() {
        if (cooldown > 0) return false;
        if (hoatzin.isFlying() || !hoatzin.canFly()) return false;

        if (hoatzin.getLastHurtByMob() != null) return true;
        if (hoatzin.shouldGlide() && !hoatzin.onGround()) return true;

        if (hoatzin.getRandom().nextInt(300) == 0) {
            targetPos = findNearbyPerchingSpot();
            return targetPos != null;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!hoatzin.isFlying()) return false;
        if (++flightTicks > MAX_FLIGHT_TICKS) return false;

        if (hoatzin.getLastHurtByMob() != null) return true;

        if (targetPos != null) {
            return hoatzin.distanceToSqr(Vec3.atCenterOf(targetPos)) > 4.0;
        }

        return false;
    }

    /* =========================
       LIFECYCLE
       ========================= */

    @Override
    public void start() {
        flightTicks = 0;
        hoatzin.startFlying();

        if (hoatzin.getLastHurtByMob() != null) {
            targetPos = findEscapeSpot();
        }
    }

    @Override
    public void stop() {
        targetPos = null;
        cooldown = FLIGHT_COOLDOWN;
        flightTicks = 0;
        // ❌ NO stopFlying() here
    }

    /* =========================
       TICK
       ========================= */

    @Override
    public void tick() {
        if (!hoatzin.isFlying()) return;

        if (targetPos != null) {
            flyTowardsTarget();
        } else if (hoatzin.getLastHurtByMob() != null) {
            escapeFromDanger();
        } else {
            maintainGlide();
        }

        clampVelocity();
    }

    /* =========================
       MOVEMENT
       ========================= */

    private void flyTowardsTarget() {
        Vec3 dir = Vec3.atCenterOf(targetPos)
                .subtract(hoatzin.position())
                .normalize();

        Vec3 newVel = hoatzin.getDeltaMovement()
                .lerp(dir.scale(MOVEMENT_SPEED), 0.1);

        hoatzin.setDeltaMovement(newVel);
    }

    private void escapeFromDanger() {
        Vec3 away = hoatzin.position()
                .subtract(hoatzin.getLastHurtByMob().position())
                .normalize();

        Vec3 targetVel = new Vec3(
                away.x * ESCAPE_SPEED,
                0.25,
                away.z * ESCAPE_SPEED
        );

        hoatzin.setDeltaMovement(
                hoatzin.getDeltaMovement().lerp(targetVel, 0.15)
        );
    }

    private void maintainGlide() {
        Vec3 v = hoatzin.getDeltaMovement();
        hoatzin.setDeltaMovement(
                v.x * 0.98,
                Math.max(v.y - 0.04, -0.5),
                v.z * 0.98
        );
    }

    private void clampVelocity() {
        Vec3 v = hoatzin.getDeltaMovement();
        if (v.length() > MAX_VELOCITY) {
            hoatzin.setDeltaMovement(v.normalize().scale(MAX_VELOCITY));
        }
    }

    /* =========================
       TARGETING HELPERS
       ========================= */

    private BlockPos findNearbyPerchingSpot() {
        BlockPos base = hoatzin.blockPosition();

        for (int i = 0; i < 8; i++) {
            BlockPos pos = base.offset(
                    hoatzin.getRandom().nextInt(24) - 12,
                    hoatzin.getRandom().nextInt(12) + 2,
                    hoatzin.getRandom().nextInt(24) - 12
            );

            if (hoatzin.level().isLoaded(pos)
                    && isValidPerchingSpot(pos)
                    && hasLineOfSight(pos)) {
                return pos;
            }
        }
        return null;
    }

    private BlockPos findEscapeSpot() {
        Vec3 dir = hoatzin.getLastHurtByMob() != null
                ? hoatzin.position().subtract(hoatzin.getLastHurtByMob().position()).normalize()
                : new Vec3(
                hoatzin.getRandom().nextGaussian(),
                0,
                hoatzin.getRandom().nextGaussian()
        ).normalize();

        BlockPos base = hoatzin.blockPosition().offset(
                (int)(dir.x * 12),
                4 + hoatzin.getRandom().nextInt(4),
                (int)(dir.z * 12)
        );

        return hoatzin.level().isLoaded(base) ? base : hoatzin.blockPosition();
    }

    private boolean isValidPerchingSpot(BlockPos pos) {
        if (!hoatzin.level().isLoaded(pos)) return false;

        return !hoatzin.level().getBlockState(pos).isAir()
                && hoatzin.level().getBlockState(pos.above()).isAir();
    }

    private boolean hasLineOfSight(BlockPos pos) {
        Vec3 from = hoatzin.getEyePosition();
        Vec3 to = Vec3.atCenterOf(pos);

        return hoatzin.level().clip(new ClipContext(
                from, to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                hoatzin
        )).getType() == HitResult.Type.MISS;
    }
}
