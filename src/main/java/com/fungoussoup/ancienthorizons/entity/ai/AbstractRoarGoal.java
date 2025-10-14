package com.fungoussoup.ancienthorizons.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

import java.util.EnumSet;

public abstract class AbstractRoarGoal extends Goal {
    protected final Animal animal;
    protected final float volume;
    protected final int minInterval;
    protected final int maxInterval;
    protected int cooldown;

    protected AbstractRoarGoal(Animal animal, float volume, int minInterval, int maxInterval) {
        this.animal = animal;
        this.volume = volume;
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected void resetCooldown() {
        cooldown = Mth.nextInt(animal.getRandom(), minInterval, maxInterval);
    }

    @Override
    public void tick() {
        if (cooldown > 0) cooldown--;
    }

    @Override
    public boolean canUse() {
        return !animal.isBaby() && cooldown <= 0;
    }
}

