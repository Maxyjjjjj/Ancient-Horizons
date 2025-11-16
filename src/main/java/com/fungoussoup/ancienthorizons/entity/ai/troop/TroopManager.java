package com.fungoussoup.ancienthorizons.entity.ai.troop;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;

public class TroopManager {

    private static final Map<UUID, Troop> troops = new HashMap<>();

    public static Troop createTroop(TroopMember founder) {
        UUID id = UUID.randomUUID();
        Troop troop = new Troop(id, founder);
        troops.put(id, troop);
        return troop;
    }

    public static Optional<Troop> getTroop(UUID id) {
        return Optional.ofNullable(troops.get(id));
    }

    public static void tickAllTroops() {
        for (Troop troop : troops.values()) {
            troop.tick();
        }
    }

    public static void removeTroop(UUID id) {
        troops.remove(id);
    }
}
