package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

/**
 * Generic idle yawn goal for big cats.
 * Can be extended to customize sound and animation per species.
 */
public class BigCatYawnGoal<T extends TamableAnimal> extends Goal {
    protected final T animal;
    protected int yawnTicks;

    public BigCatYawnGoal(T animal) {
        this.animal = animal;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !animal.isOrderedToSit()
                && animal.getTarget() == null
                && !animal.isInWater()
                && animal.getNavigation().isDone()
                && animal.getRandom().nextFloat() < 0.001F;
    }

    @Override
    public void start() {
        yawnTicks = 60;
        playYawnSound();
        setYawning(true);
    }

    @Override
    public boolean canContinueToUse() {
        return yawnTicks > 0;
    }

    @Override
    public void tick() {
        if (yawnTicks > 0) yawnTicks--;
        else setYawning(false);
    }

    @Override
    public void stop() {
        setYawning(false);
    }

    /**
     * Hooks for species-specific implementations
     */
    protected void playYawnSound() {}
    protected void setYawning(boolean yawning) {}
}

