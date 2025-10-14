package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.SleepingAnimal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Generic sleeping goal for large tameable cats (tigers, lions, jaguars, etc.).
 * Supports sleeping position persistence and wake-up interruption.
 */
public class BigCatSleepGoal<T extends TamableAnimal & SleepingAnimal> extends Goal {
    protected final T animal;
    protected int sleepTime;

    public BigCatSleepGoal(T animal) {
        this.animal = animal;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !animal.isOrderedToSit()
                && animal.getTarget() == null
                && !animal.isSleeping()
                && !animal.isInWater()
                && animal.getNavigation().isDone()
                && animal.getRandom().nextFloat() < 0.001F;
    }

    @Override
    public void start() {
        sleepTime = 200 + animal.getRandom().nextInt(200);
        animal.setSleeping(true);
        animal.setSleepingPos(BlockPos.containing(animal.position()));
    }

    @Override
    public boolean canContinueToUse() {
        return sleepTime > 0 && animal.getTarget() == null && animal.isSleeping();
    }

    @Override
    public void tick() {
        if (sleepTime-- <= 0 || animal.getTarget() != null || animal.isInWater()) {
            stop();
        }
    }

    @Override
    public void stop() {
        animal.setSleeping(false);
        sleepTime = 0;
    }
}
