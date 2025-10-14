package com.fungoussoup.ancienthorizons.entity.ai.troop;

public enum TroopRelation {
    ALLY(0),
    NEUTRAL(1),
    ENEMY(2);

    private final int id;

    TroopRelation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
