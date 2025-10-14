package com.fungoussoup.ancienthorizons.entity;

class VariantConditions {

    public enum ChangeType {
        DIET,
        SEASON,
        BREEDING,
        AGE,
        BIOME,
        WEATHER,
        TIME_OF_DAY,
        HEALTH,
        CUSTOM
    }

    public static class DietBasedVariant {
        private final int requiredFoodCount;
        private final int daysSinceLastFood;
        private final int targetVariant;

        public DietBasedVariant(int requiredFoodCount, int daysSinceLastFood, int targetVariant) {
            this.requiredFoodCount = requiredFoodCount;
            this.daysSinceLastFood = daysSinceLastFood;
            this.targetVariant = targetVariant;
        }

        public int getRequiredFoodCount() { return requiredFoodCount; }
        public int getDaysSinceLastFood() { return daysSinceLastFood; }
        public int getTargetVariant() { return targetVariant; }
    }

    public static class SeasonalVariant {
        private final long dayOfYear;
        private final int winterVariant;
        private final int summerVariant;
        private final int springVariant;
        private final int autumnVariant;

        public SeasonalVariant(int winterVariant, int springVariant, int summerVariant, int autumnVariant) {
            this.dayOfYear = 0; // Would need to be calculated from world time
            this.winterVariant = winterVariant;
            this.springVariant = springVariant;
            this.summerVariant = summerVariant;
            this.autumnVariant = autumnVariant;
        }

        public int getVariantForSeason() {
            // Simplified season calculation - you'd want to use actual world time
            long season = (dayOfYear / 20) % 4; // Assuming 80 day seasons
            return switch ((int) season) {
                case 0 -> springVariant;
                case 1 -> summerVariant;
                case 2 -> autumnVariant;
                case 3 -> winterVariant;
                default -> summerVariant;
            };
        }
    }
}