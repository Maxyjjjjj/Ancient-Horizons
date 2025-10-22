package com.fungoussoup.ancienthorizons.entity.interfaces;

public interface SemiFlyer {

    void startFlying();

    void stopFlying();

    boolean isFlying();

    boolean canFly();

    boolean shouldGlide();

    void setFlying(boolean flying);

    default double getPreferredFlightHeight() {
        return 8.0;
    }

    default boolean canLandOnWater() {
        return false;
    }
}