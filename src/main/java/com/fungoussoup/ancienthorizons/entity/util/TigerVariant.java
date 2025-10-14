package com.fungoussoup.ancienthorizons.entity.util;

public enum TigerVariant {
    NORMAL(0),
    WHITE(1),
    GOLDEN(2),
    BLUE(3),
    LEGENDS(4);

    private final int id;

    TigerVariant(int id) {
        this.id = id;
    }

    public static TigerVariant byID(int i) {
        for (TigerVariant variant : values()) {
            if (variant.id == i) {
                return variant;
            }
        }
        return NORMAL; // Default to NORMAL if not found
    }


    public int getId() { return id; }
}
