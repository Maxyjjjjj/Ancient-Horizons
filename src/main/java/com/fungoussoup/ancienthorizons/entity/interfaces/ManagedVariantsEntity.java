package com.fungoussoup.ancienthorizons.entity.interfaces;

import com.fungoussoup.ancienthorizons.entity.VariantManager;
import com.fungoussoup.ancienthorizons.entity.interfaces.InterchangeableVariantsEntity;

interface ManagedVariantsEntity extends InterchangeableVariantsEntity {

    /**
     * Get the variant manager for this entity
     * @return the variant manager
     */
    VariantManager getVariantManager();

    /**
     * Initialize default variant conditions for this entity type
     */
    default void initializeVariantConditions() {
        // Override in implementations to add default conditions
    }

    /**
     * Update variants using the manager system
     */
    @Override
    default void updateVariant() {
        if (this instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
            getVariantManager().updateVariants(livingEntity);
        }
    }
}
