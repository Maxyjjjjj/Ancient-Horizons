package com.fungoussoup.ancienthorizons.entity.util;

public enum ZebraVariant {
    ZEBRA_REGULAR(0),
    ZEBRA_BLONDE(1),
    ZEBRA_POLKADOT(2);

    private int id;

    ZebraVariant(int id){
        this.id=id;
    }

    public static ZebraVariant byID(int i) {
        for (ZebraVariant variant : values()) {
            if (variant.id == i) {
                return variant;
            }
        }
        return ZEBRA_REGULAR; // Default to NORMAL if not found
    }

    public int getId() { return id; }
}
