package com.fungoussoup.ancienthorizons.entity.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;

/**
 * Interface for entities that can change their visual variants based on various conditions
 * such as diet, season, breeding status, age, etc.
 */
public interface InterchangeableVariantsEntity {

    int getVariant();

    void setVariant(int variant);

    int getMaxVariants();

    void updateVariant();

    EntityDataAccessor<Integer> getVariantDataAccessor();

    default void saveVariantToNBT(CompoundTag tag) {
        tag.putInt("Variant", this.getVariant());
    }

    default void loadVariantFromNBT(CompoundTag tag) {
        if (tag.contains("Variant")) {
            this.setVariant(tag.getInt("Variant"));
        }
    }

    default String getVariantName() {
        return "variant_" + this.getVariant();
    }

    boolean shouldUpdateVariant();

    int getTargetVariant();

    default void onVariantChanged(int oldVariant, int newVariant) {
        // Override for custom behavior
    }
}