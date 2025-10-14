package com.fungoussoup.ancienthorizons.entity;

import com.fungoussoup.ancienthorizons.entity.custom.mob.FlamingoEntity;
import com.fungoussoup.ancienthorizons.entity.interfaces.InterchangeableVariantsEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;

/**
 * System for managing different types of variant conditions
 * This allows for flexible variant changing based on various environmental and behavioral factors
 */
public abstract class VariantCondition {

    protected final String name;
    protected final int priority; // Higher priority conditions override lower ones

    public VariantCondition(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    /**
     * Check if this condition should trigger a variant change
     * @param entity the entity to check
     * @return true if condition is met
     */
    public abstract boolean shouldTrigger(Animal entity);

    /**
     * Get the target variant for this condition
     * @param entity the entity to get variant for
     * @return target variant ID
     */
    public abstract int getTargetVariant(Animal entity);

    /**
     * Get the update frequency for this condition in ticks
     * @return ticks between updates
     */
    public abstract int getUpdateFrequency();

    public String getName() { return name; }
    public int getPriority() { return priority; }

    // Specific condition implementations

    /**
     * Diet-based variant condition - like flamingo pink coloration from shrimp
     */
    public static class DietBasedCondition extends VariantCondition {
        private final int[] foodThresholds;
        private final int[] variants;
        private final long decayTime;

        public DietBasedCondition(String name, int priority, int[] foodThresholds, int[] variants, long decayTime) {
            super(name, priority);
            this.foodThresholds = foodThresholds;
            this.variants = variants;
            this.decayTime = decayTime;
        }

        @Override
        public boolean shouldTrigger(Animal entity) {
            return entity instanceof InterchangeableVariantsEntity;
        }

        @Override
        public int getTargetVariant(Animal entity) {
            if (entity instanceof FlamingoEntity flamingo) {
                int shrimpCount = flamingo.shrimpEatenCount;
                long timeSinceLastFed = entity.level().getGameTime() - flamingo.lastFedTime;

                // Apply decay
                if (timeSinceLastFed > decayTime) {
                    shrimpCount = Math.max(0, shrimpCount - (int)(timeSinceLastFed / decayTime));
                }

                // Find appropriate variant based on food count
                for (int i = foodThresholds.length - 1; i >= 0; i--) {
                    if (shrimpCount >= foodThresholds[i]) {
                        return variants[i];
                    }
                }
            }
            return 0; // Default variant
        }

        @Override
        public int getUpdateFrequency() {
            return 1200; // Update every minute
        }
    }

    /**
     * Seasonal variant condition - changes based on time of year
     */
    public static class SeasonalCondition extends VariantCondition {
        private final int springVariant, summerVariant, autumnVariant, winterVariant;
        private final long seasonLength;

        public SeasonalCondition(String name, int priority, int springVariant, int summerVariant,
                                 int autumnVariant, int winterVariant, long seasonLength) {
            super(name, priority);
            this.springVariant = springVariant;
            this.summerVariant = summerVariant;
            this.autumnVariant = autumnVariant;
            this.winterVariant = winterVariant;
            this.seasonLength = seasonLength;
        }

        @Override
        public boolean shouldTrigger(Animal entity) {
            return true; // Always check seasonal changes
        }

        @Override
        public int getTargetVariant(Animal entity) {
            long worldTime = entity.level().getDayTime();
            long yearTime = worldTime % (seasonLength * 4);
            long season = yearTime / seasonLength;

            return switch ((int) season) {
                case 0 -> springVariant;
                case 1 -> summerVariant;
                case 2 -> autumnVariant;
                case 3 -> winterVariant;
                default -> summerVariant;
            };
        }

        @Override
        public int getUpdateFrequency() {
            return 6000; // Update every 5 minutes
        }
    }

    /**
     * Biome-based variant condition - changes based on current biome
     */
    public static class BiomeBasedCondition extends VariantCondition {
        private final BiomeVariantMapping[] mappings;
        private final int defaultVariant;

        public BiomeBasedCondition(String name, int priority, BiomeVariantMapping[] mappings, int defaultVariant) {
            super(name, priority);
            this.mappings = mappings;
            this.defaultVariant = defaultVariant;
        }

        @Override
        public boolean shouldTrigger(Animal entity) {
            return true;
        }

        @Override
        public int getTargetVariant(Animal entity) {
            Level level = entity.level();
            BlockPos pos = entity.blockPosition();
            Biome biome = level.getBiome(pos).value();

            for (BiomeVariantMapping mapping : mappings) {
                if (mapping.matches(biome)) {
                    return mapping.variant;
                }
            }

            return defaultVariant;
        }

        @Override
        public int getUpdateFrequency() {
            return 100; // Update every 5 seconds
        }

        public static class BiomeVariantMapping {
            public final String biomeName;
            public final int variant;

            public BiomeVariantMapping(String biomeName, int variant) {
                this.biomeName = biomeName;
                this.variant = variant;
            }

            public boolean matches(Biome biome) {
                // You'd need to implement proper biome matching here
                return biome.toString().contains(biomeName.toLowerCase());
            }
        }
    }

    /**
     * Breeding-based variant condition - changes during breeding season
     */
    public static class BreedingCondition extends VariantCondition {
        private final int breedingVariant;
        private final int normalVariant;
        private final int breedingDuration;

        public BreedingCondition(String name, int priority, int breedingVariant, int normalVariant, int breedingDuration) {
            super(name, priority);
            this.breedingVariant = breedingVariant;
            this.normalVariant = normalVariant;
            this.breedingDuration = breedingDuration;
        }

        public boolean shouldTrigger(Animal entity) {
            return entity.canFallInLove();
        }

        public int getTargetVariant(Animal entity) {
            if (entity.isInLove()) {
                return breedingVariant;
            }
            return normalVariant;
        }

        @Override
        public int getUpdateFrequency() {
            return 40; // Update every 2 seconds during potential breeding
        }
    }

    /**
     * Time-based variant condition - changes based on time of day
     */
    public static class TimeBasedCondition extends VariantCondition {
        private final int dayVariant;
        private final int nightVariant;
        private final int dawnDuskVariant;

        public TimeBasedCondition(String name, int priority, int dayVariant, int nightVariant, int dawnDuskVariant) {
            super(name, priority);
            this.dayVariant = dayVariant;
            this.nightVariant = nightVariant;
            this.dawnDuskVariant = dawnDuskVariant;
        }

        @Override
        public boolean shouldTrigger(Animal entity) {
            return true;
        }

        @Override
        public int getTargetVariant(Animal entity) {
            long timeOfDay = entity.level().getDayTime() % 24000;

            if (timeOfDay >= 0 && timeOfDay < 1000) { // Dawn
                return dawnDuskVariant;
            } else if (timeOfDay >= 1000 && timeOfDay < 13000) { // Day
                return dayVariant;
            } else if (timeOfDay >= 13000 && timeOfDay < 14000) { // Dusk
                return dawnDuskVariant;
            } else { // Night
                return nightVariant;
            }
        }

        @Override
        public int getUpdateFrequency() {
            return 200; // Update every 10 seconds
        }
    }
}