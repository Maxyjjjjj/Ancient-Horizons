package com.fungoussoup.ancienthorizons.compat.automobility;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

public class LargeAnimalTurnAutomobileOver extends MeleeAttackGoal {
    private final PathfinderMob mob;
    private AutomobileEntity automobileTarget;
    private double speedModifier;

    public LargeAnimalTurnAutomobileOver(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Search for nearby automobiles
        double radius = 12.0D; // detection radius
        AABB box = this.mob.getBoundingBox().inflate(radius);
        for (Entity e : this.mob.level().getEntities(this.mob, box)) {
            if (e instanceof AutomobileEntity auto && !auto.isRemoved()) {
                this.automobileTarget = auto;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.automobileTarget != null
                && !this.automobileTarget.isRemoved()
                && this.mob.distanceToSqr(this.automobileTarget) < 256.0D;
    }

    @Override
    public void stop() {
        super.stop();
        this.automobileTarget = null;
    }

    @Override
    public void tick() {
        if (this.automobileTarget == null) return;

        this.mob.getLookControl().setLookAt(this.automobileTarget, 30.0F, 30.0F);
        this.mob.getNavigation().moveTo(this.automobileTarget, this.speedModifier);

        if (this.mob.distanceToSqr(this.automobileTarget) <= this.getAttackReachSqr(this.automobileTarget)) {
            this.mob.doHurtTarget(this.automobileTarget);

            // "Flip" the automobile
            flipAutomobile(this.automobileTarget);
        }
    }

    private void flipAutomobile(AutomobileEntity auto) {
        // Simple knockback/rotation effect — customize as needed
        auto.setDeltaMovement(auto.getDeltaMovement().add(
                (this.mob.getRandom().nextDouble() - 0.5) * 0.8,
                0.5,
                (this.mob.getRandom().nextDouble() - 0.5) * 0.8
        ));
        auto.setYRot(auto.getXRot() + 180.0F); // spin around
        auto.ejectPassengers();
    }

    protected double getAttackReachSqr(Entity target) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + target.getBbWidth();
    }
}
