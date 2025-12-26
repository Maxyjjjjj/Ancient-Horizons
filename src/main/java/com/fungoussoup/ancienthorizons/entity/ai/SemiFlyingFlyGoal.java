package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SemiFlyer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;

public class SemiFlyingFlyGoal extends Goal {
    private final PathfinderMob mob;
    private final SemiFlyer flyer;
    private final double speed;

    private final int maxFlightTime;
    private final int minRestTime;

    private int flightTicks;
    private int restTicks;
    private int targetCooldown;
    private int stuckTicks;

    private Vec3 lastPos = Vec3.ZERO;
    private Vec3 targetPos;

    public SemiFlyingFlyGoal(PathfinderMob mob, double speed) {
        this(mob, speed, 400, 100);
    }

    public SemiFlyingFlyGoal(PathfinderMob mob, double speed, int maxFlightTime, int minRestTime) {
        if (!(mob instanceof SemiFlyer semiFlyer)) {
            throw new IllegalArgumentException("SemiFlyingFlyGoal requires SemiFlyer");
        }

        this.mob = mob;
        this.flyer = semiFlyer;
        this.speed = speed;
        this.maxFlightTime = maxFlightTime;
        this.minRestTime = minRestTime;

        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        restTicks++;

        if (!flyer.canFly()) return false;
        if (restTicks < minRestTime) return false;

        if (mob.isPassenger() || mob.isVehicle()) return false;
        if (mob instanceof TamableAnimal t && t.isOrderedToSit()) return false;

        targetPos = findFlyingPosition();
        return targetPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!flyer.isFlying()) return false;

        if (!flyer.canFly()) return false;
        if (flightTicks >= maxFlightTime) return false;

        if (mob.onGround() && flightTicks > 20) return false;
        if (stuckTicks > 80) return false;

        return targetPos != null;
    }

    @Override
    public void start() {
        flyer.startFlying();

        flightTicks = 0;
        restTicks = 0;
        stuckTicks = 0;
        targetCooldown = 0;

        lastPos = mob.position();
    }

    @Override
    public void stop() {
        if (flyer.isFlying()) {
            flyer.stopFlying();
        }

        targetPos = null;
        flightTicks = 0;
        stuckTicks = 0;
    }

    @Override
    public void tick() {
        flightTicks++;
        targetCooldown--;

        checkStuck();

        if (targetPos == null || mob.distanceToSqr(targetPos) < 3.0) {
            if (targetCooldown <= 0) {
                targetPos = findFlyingPosition();
                targetCooldown = 40;
            }
        }

        if (targetPos != null) {
            mob.getMoveControl().setWantedPosition(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    speed
            );
        }
    }

    /* ----------------- HELPERS ----------------- */

    private void checkStuck() {
        Vec3 current = mob.position();

        if (current.distanceToSqr(lastPos) < 0.01) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        lastPos = current;
    }

    @Nullable
    private Vec3 findFlyingPosition() {
        Vec3 pos = HoverRandomPos.getPos(
                mob, 8, 6,
                mob.getX(), mob.getZ(),
                (float) Math.PI / 2,
                3, 1
        );

        if (pos != null && isSafe(pos)) return pos;

        RandomSource rand = mob.getRandom();
        for (int i = 0; i < 3; i++) {
            Vec3 test = mob.position().add(
                    (rand.nextDouble() - 0.5) * 10,
                    rand.nextDouble() * 4 + 2,
                    (rand.nextDouble() - 0.5) * 10
            );

            if (isSafe(test)) return test;
        }

        return null;
    }

    private boolean isSafe(Vec3 pos) {
        BlockPos bp = BlockPos.containing(pos);

        if (!mob.level().isLoaded(bp)) return false;
        if (!mob.level().isEmptyBlock(bp)) return false;
        if (!mob.level().isEmptyBlock(bp.above())) return false;

        for (int i = 1; i <= 15; i++) {
            BlockPos below = bp.below(i);
            if (!mob.level().isLoaded(below)) return false;
            if (!mob.level().isEmptyBlock(below)) return i >= 2;
        }

        return false;
    }
}
