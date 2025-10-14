package com.fungoussoup.ancienthorizons.entity.interfaces;

public interface ISemiAquaticAnimal {

    boolean shouldEnterWater();

    boolean shouldLeaveWater();

    boolean shouldStopMoving();

    int getWaterSearchRange();
}
