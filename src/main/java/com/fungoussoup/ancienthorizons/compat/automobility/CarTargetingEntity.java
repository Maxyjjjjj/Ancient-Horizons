package com.fungoussoup.ancienthorizons.compat.automobility;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface CarTargetingEntity {

    /**
     * Gets the current automobile target of this entity
     * @return the targeted automobile, or null if none
     */
    @Nullable
    AutomobileEntity getAutomobileTarget();

    /**
     * Sets the automobile target for this entity
     * @param target the automobile to target, or null to clear target
     */
    void setAutomobileTarget(@Nullable AutomobileEntity target);

    /**
     * Checks if this entity can target automobiles
     * @return true if automobile targeting is enabled
     */
    boolean canTargetAutomobiles();

    /**
     * Gets the maximum range this entity can target automobiles from
     * @return the targeting range in blocks
     */
    double getAutomobileTargetRange();

    /**
     * Determines if this entity should target the given automobile
     * @param automobile the automobile to check
     * @return true if the automobile should be targeted
     */
    default boolean shouldTargetAutomobile(AutomobileEntity automobile) {
        if (!canTargetAutomobiles() || automobile == null) {
            return false;
        }

        // Don't target automobiles with passengers by default
        if (automobile.getFirstPassenger() instanceof LivingEntity) {
            return false;
        }

        // Check distance
        double distance = automobile.distanceTo(this instanceof LivingEntity living ? living : null);
        return distance <= getAutomobileTargetRange();
    }

    /**
     * Called when this entity starts targeting an automobile
     * @param automobile the automobile being targeted
     */
    default void onStartTargetingAutomobile(AutomobileEntity automobile) {
        // Override in implementations for custom behavior
    }

    /**
     * Called when this entity stops targeting an automobile
     * @param automobile the automobile that was being targeted
     */
    default void onStopTargetingAutomobile(@Nullable AutomobileEntity automobile) {
        // Override in implementations for custom behavior
    }
}