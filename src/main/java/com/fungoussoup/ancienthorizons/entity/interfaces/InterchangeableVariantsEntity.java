package com.fungoussoup.ancienthorizons.entity.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;

/**
 * Interface for entities that can change their visual variants based on various conditions
 * such as diet, season, breeding status, age, etc.
 */
public interface InterchangeableVariantsEntity {

    /**
     * Gets the current variant of this entity
     * @return current variant ID
     */
    int getVariant();

    /**
     * Sets the variant of this entity
     * @param variant the variant ID to set
     */
    void setVariant(int variant);

    /**
     * Gets the maximum number of variants this entity type supports
     * @return maximum variant count
     */
    int getMaxVariants();

    /**
     * Called every tick to update variant based on conditions
     * This is where you'd implement logic for diet-based changes, seasonal changes, etc.
     */
    void updateVariant();

    /**
     * Gets the data accessor for syncing variant data to clients
     * @return EntityDataAccessor for variant
     */
    EntityDataAccessor<Integer> getVariantDataAccessor();

    /**
     * Save variant data to NBT
     * @param tag the compound tag to save to
     */
    default void saveVariantToNBT(CompoundTag tag) {
        tag.putInt("Variant", this.getVariant());
    }

    /**
     * Load variant data from NBT
     * @param tag the compound tag to load from
     */
    default void loadVariantFromNBT(CompoundTag tag) {
        if (tag.contains("Variant")) {
            this.setVariant(tag.getInt("Variant"));
        }
    }

    /**
     * Gets a text description of the current variant for debugging/display
     * @return variant description
     */
    default String getVariantName() {
        return "variant_" + this.getVariant();
    }

    /**
     * Determines if the variant should change based on current conditions
     * @return true if variant should update
     */
    boolean shouldUpdateVariant();

    /**
     * Gets the target variant based on current conditions
     * @return the variant this entity should transition to
     */
    int getTargetVariant();

    /**
     * Called when variant actually changes - useful for particles, sounds, etc.
     * @param oldVariant the previous variant
     * @param newVariant the new variant
     */
    default void onVariantChanged(int oldVariant, int newVariant) {
        // Override for custom behavior
    }
}