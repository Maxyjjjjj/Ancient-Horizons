package com.fungoussoup.ancienthorizons.entity.util;

public enum SizeCategory {
    INSECT, TINY, SMALL, MEDIUM, LARGE, HUGE, WHALE;

    public boolean isBetween(SizeCategory min, SizeCategory max) {
        return this.ordinal() >= min.ordinal() && this.ordinal() <= max.ordinal();
    }
}