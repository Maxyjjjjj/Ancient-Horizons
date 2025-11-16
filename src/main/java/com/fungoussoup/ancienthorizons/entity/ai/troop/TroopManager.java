package com.fungoussoup.ancienthorizons.entity.ai.troop;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TroopManager {

    private static final Map<UUID, Troop> troops = new ConcurrentHashMap<>();
    // Index used to spread work across ticks
    private static int roundRobinIndex = 0;

    public static Troop createTroop(TroopMember founder) {
        UUID id = UUID.randomUUID();
        Troop troop = new Troop(id, founder);
        troops.put(id, troop);
        return troop;
    }

    public static Optional<Troop> getTroop(UUID id) {
        return Optional.ofNullable(troops.get(id));
    }

    /**
     * Tick up to {@code maxPerTick} troops each server tick to avoid long stalls.
     * If {@code maxPerTick} is &lt;= 0, all troops will be ticked (legacy behaviour).
     */
    public static void tickAllTroops(int maxPerTick) {
        Collection<Troop> values = troops.values();
        if (values.isEmpty()) return;

        // Take a snapshot of the current troops to avoid ConcurrentModificationException
        // and ensure the round-robin logic is based on a fixed list for this tick.
        List<Troop> list = new ArrayList<>(values);
        int size = list.size();
        if (maxPerTick <= 0 || maxPerTick >= size) {
            // tick all (fall back)
            for (Troop troop : list) troop.tick();
            return;
        }

        int processed = 0;
        // process in round-robin so large sets are spread across ticks
        while (processed < maxPerTick) {
            // Use modulo operator on the fixed size to cycle through the list
            Troop t = list.get(roundRobinIndex % size);
            t.tick();
            // Increment and wrap the index for the next tick
            roundRobinIndex = (roundRobinIndex + 1) % size;
            processed++;
        }
    }

    // Backwards-compatible no-arg method ticks a small fixed amount per tick.
    public static void tickAllTroops() {
        tickAllTroops(10);
    }

    public static void removeTroop(UUID id) {
        troops.remove(id);
    }

    /** Clears all troops (use on server stop to release references). */
    public static void clearAll() {
        troops.clear();
    }
}