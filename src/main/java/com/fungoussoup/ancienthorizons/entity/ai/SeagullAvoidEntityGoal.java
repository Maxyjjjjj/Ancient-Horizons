package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.custom.mob.SeagullEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class SeagullAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
    private final SeagullEntity seagull;
    private final Predicate<T> avoidPredicate;

    public SeagullAvoidEntityGoal(SeagullEntity seagull, Class<T> entityClass, float maxDistance,
                                  double walkSpeedModifier, double sprintSpeedModifier,
                                  Predicate<T> avoidPredicate) {
        super(seagull, entityClass, maxDistance, walkSpeedModifier, sprintSpeedModifier);
        this.seagull = seagull;
        this.avoidPredicate = avoidPredicate;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && avoidPredicate.test(this.toAvoid);
    }

    @Override
    public void start() {
        super.start();
        this.seagull.setFlying(true);
        this.seagull.setSitting(false);
    }

    protected Vec3 getFindHiddenPos() {
        Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
        if (pos != null) {
            // Ensure we fly upward when avoiding
            pos = pos.add(0, 3 + this.mob.getRandom().nextInt(4), 0);
        }
        return pos;
    }
}

