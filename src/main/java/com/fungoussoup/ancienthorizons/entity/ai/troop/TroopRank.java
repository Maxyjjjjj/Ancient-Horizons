package com.fungoussoup.ancienthorizons.entity.ai.troop;

public enum TroopRank {
    JUVENILE(0),
    ADULT(1),
    ALPHA(2);

    private final int id;

    TroopRank(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

