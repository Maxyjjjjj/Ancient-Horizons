package com.fungoussoup.ancienthorizons.entity.ai;

import com.fungoussoup.ancienthorizons.entity.interfaces.Stampedeable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks consecutive hits on stampedeable animals to trigger stampede behavior
 */
public class StampedeTracker {
    private static final StampedeTracker INSTANCE = new StampedeTracker();

    // Map of attacker UUID to their hit streak data
    private final Map<UUID, HitStreak> hitStreaks = new HashMap<>();

    // Default number of consecutive hits needed to trigger stampede
    private static final int DEFAULT_HITS_TO_STAMPEDE = 3;

    // Time in ticks before a streak expires (5 seconds)
    private static final int STREAK_EXPIRY_TIME = 100;

    private StampedeTracker() {}

    public static StampedeTracker getInstance() {
        return INSTANCE;
    }

    /**
     * Called when an animal is hit. Returns true if stampede should trigger.
     */
    public boolean onAnimalHit(LivingEntity attacker, Animal victim) {
        if (!(victim instanceof Stampedeable)) {
            return false;
        }

        UUID attackerId = attacker.getUUID();
        EntityType<?> victimType = victim.getType();
        long currentTime = victim.level().getGameTime();

        // Clean up expired streaks
        hitStreaks.entrySet().removeIf(entry ->
                currentTime - entry.getValue().lastHitTime > STREAK_EXPIRY_TIME
        );

        HitStreak streak = hitStreaks.computeIfAbsent(attackerId, k -> new HitStreak());

        // Check if this hit continues the streak
        if (streak.entityType == null || streak.entityType.equals(victimType)) {
            streak.entityType = victimType;
            streak.count++;
            streak.lastHitTime = currentTime;

            // Check if we've reached the threshold
            if (streak.count >= DEFAULT_HITS_TO_STAMPEDE) {
                triggerStampede(victim);
                hitStreaks.remove(attackerId); // Reset streak after triggering
                return true;
            }
        } else {
            // Different entity type - reset streak
            streak.entityType = victimType;
            streak.count = 1;
            streak.lastHitTime = currentTime;
        }

        return false;
    }

    /**
     * Triggers stampede for the victim and nearby animals of the same type
     */
    private void triggerStampede(Animal victim) {
        if (!(victim instanceof Stampedeable stampedeable)) {
            return;
        }

        // Trigger stampede on the victim
        stampedeable.triggerStampede();

        // Find and trigger stampede on nearby animals of the same type
        AABB searchBox = victim.getBoundingBox().inflate(16.0);
        List<? extends Animal> nearbyAnimals = victim.level().getEntitiesOfClass(
                victim.getClass(),
                searchBox,
                e -> e != victim && e instanceof Stampedeable
        );

        for (Animal animal : nearbyAnimals) {
            if (animal instanceof Stampedeable nearby) {
                nearby.triggerStampede();
            }
        }
    }

    /**
     * Clears all hit streaks (useful for debugging or reset)
     */
    public void clearAllStreaks() {
        hitStreaks.clear();
    }

    private static class HitStreak {
        EntityType<?> entityType;
        int count;
        long lastHitTime;

        HitStreak() {
            this.count = 0;
            this.lastHitTime = 0;
        }
    }
}
