package com.fungoussoup.ancienthorizons.entity.util;

public enum AnacondaPartIndex {
    HEAD(0F), BODY(0.5F), TAIL(0.4F);

    private final float backOffset;

    AnacondaPartIndex(float partOffset){
        this.backOffset = partOffset;
    }

    public static AnacondaPartIndex fromOrdinal(int i) {
        return switch (i) {
            case 0 -> HEAD;
            case 2 -> TAIL;
            default -> BODY; // case 1 and others
        };
    }

    public static AnacondaPartIndex sizeAt(int partArrayIndex) {
        return switch (partArrayIndex) {
            case 0 -> HEAD; // First part behind the main head
            case 9 -> TAIL; // Last part
            default -> BODY; // All others
        };
    }

    public float getBackOffset() {
        return backOffset;
    }
}
