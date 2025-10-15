package com.fungoussoup.ancienthorizons.entity.interfaces;

public interface SemiFlyer {
    /**
     * Called when the entity should start flying behavior
     */
    void startFlying();

    /**
     * Called when the entity should stop flying behavior
     */
    void stopFlying();

    /**
     * @return true if the entity is currently in flying mode
     */
    boolean isFlying();

    /**
     * @return true if the entity is capable of flight (not injured, has energy, etc.)
     */
    boolean canFly();

    /**
     * @return true if the entity should glide instead of actively flying
     */
    boolean shouldGlide();

    /**
     * Sets the flying state of the entity
     * @param flying true to enable flying mode, false to disable
     */
    void setFlying(boolean flying);

    /**
     * @return the preferred flight height above ground
     */
    default double getPreferredFlightHeight() {
        return 8.0;
    }

    /**
     * @return true if the entity can land on water
     */
    default boolean canLandOnWater() {
        return false;
    }
}