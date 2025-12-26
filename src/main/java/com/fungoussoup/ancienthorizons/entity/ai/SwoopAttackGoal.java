package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.AbstractEagleEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SwoopAttackGoal extends Goal {
    private LivingEntity target;
    private AbstractEagleEntity eagle;
    private int phase = 0;

    public SwoopAttackGoal() {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (eagle.swoopCooldown > 0) return false;
        target = eagle.getTarget();
        if (target == null) return false;
        if (!eagle.isFlying()) return false;
        double dsq = eagle.distanceToSqr(target);
        return dsq < 120.0 && eagle.getY() > target.getY() + 2.0 && eagle.getRandom().nextInt(50) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && phase < 3;
    }

    @Override
    public void start() {
        phase = 0;
        eagle.setFlying(true);
        eagle.setAttacking(true);
    }

    @Override
    public void stop() {
        target = null;
        phase = 0;
        eagle.swoopCooldown = eagle.getSwoopCooldownTicks();
        eagle.setAttacking(false);
    }

    @Override
    public void tick() {
        if (target == null) return;
        switch (phase) {
            case 0:
                if (eagle.getY() < target.getY() + 10.0) {
                    eagle.setDeltaMovement(eagle.getDeltaMovement().add(0, 0.15, 0));
                } else {
                    phase = 1;
                }
                break;
            case 1:
                Vec3 to = target.position().subtract(eagle.position()).normalize();
                Vec3 dive = to.add(0, -0.4, 0).normalize().scale(eagle.getSwoopSpeed());
                eagle.setDeltaMovement(dive);
                if (eagle.distanceToSqr(target) < 6.0) {
                    eagle.doHurtTarget(target);
                    if (eagle.isValidPrey(target)) eagle.pickUpPrey(target);
                    phase = 2;
                }
                break;
            case 2:
                eagle.setDeltaMovement(eagle.getDeltaMovement().add(0, 0.3, 0));
                phase = 3;
                break;
        }
    }
}