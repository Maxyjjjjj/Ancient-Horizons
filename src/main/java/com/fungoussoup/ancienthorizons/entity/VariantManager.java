package com.fungoussoup.ancienthorizons.entity;

import com.fungoussoup.ancienthorizons.entity.interfaces.InterchangeableVariantsEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

import java.util.*;

/**
 * Manager class for handling multiple variant conditions for entities
 * Allows entities to have multiple overlapping conditions that can affect their appearance
 */
public class VariantManager {
    private final List<VariantCondition> conditions;
    private final Map<String, Long> lastUpdateTimes;
    private final InterchangeableVariantsEntity entity;

    public VariantManager(InterchangeableVariantsEntity entity) {
        this.entity = entity;
        this.conditions = new ArrayList<>();
        this.lastUpdateTimes = new HashMap<>();
    }

    /**
     * Add a variant condition to this manager
     * @param condition the condition to add
     */
    public void addCondition(VariantCondition condition) {
        conditions.add(condition);
        conditions.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority())); // Sort by priority (highest first)
        lastUpdateTimes.put(condition.getName(), 0L);
    }

    /**
     * Remove a variant condition from this manager
     * @param conditionName the name of the condition to remove
     */
    public void removeCondition(String conditionName) {
        conditions.removeIf(condition -> condition.getName().equals(conditionName));
        lastUpdateTimes.remove(conditionName);
    }

    /**
     * Update all conditions and determine the appropriate variant
     * Should be called from the entity's tick method
     * @param livingEntity the entity to update
     */
    public void updateVariants(LivingEntity livingEntity) {
        long currentTime = livingEntity.level().getGameTime();

        // Check each condition to see if it needs updating
        for (VariantCondition condition : conditions) {
            String conditionName = condition.getName();
            long lastUpdate = lastUpdateTimes.get(conditionName);

            // Check if enough time has passed for this condition to update
            if (currentTime - lastUpdate >= condition.getUpdateFrequency()) {
                lastUpdateTimes.put(conditionName, currentTime);

                // If condition should trigger, apply the variant
                if (condition.shouldTrigger((Animal) livingEntity)) {
                    int targetVariant = condition.getTargetVariant((Animal) livingEntity);
                    entity.setVariant(targetVariant);
                    break; // Higher priority conditions take precedence
                }
            }
        }
    }

    /**
     * Get all active conditions
     * @return list of conditions
     */
    public List<VariantCondition> getConditions() {
        return new ArrayList<>(conditions);
    }

    /**
     * Get condition by name
     * @param name the condition name
     * @return the condition, or null if not found
     */
    public VariantCondition getCondition(String name) {
        return conditions.stream()
                .filter(condition -> condition.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if a specific condition exists
     * @param name the condition name
     * @return true if condition exists
     */
    public boolean hasCondition(String name) {
        return getCondition(name) != null;
    }

    /**
     * Clear all conditions
     */
    public void clearConditions() {
        conditions.clear();
        lastUpdateTimes.clear();
    }

    /**
     * Get debug information about current conditions
     * @return debug string
     */
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Variant Manager Debug:\n");
        info.append("Current Variant: ").append(entity.getVariant()).append(" (").append(entity.getVariantName()).append(")\n");
        info.append("Conditions (").append(conditions.size()).append("):\n");

        for (VariantCondition condition : conditions) {
            info.append("  - ").append(condition.getName())
                    .append(" (Priority: ").append(condition.getPriority())
                    .append(", Frequency: ").append(condition.getUpdateFrequency()).append(")\n");
        }

        return info.toString();
    }
}
