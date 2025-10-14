package com.fungoussoup.ancienthorizons;

import com.fungoussoup.ancienthorizons.entity.ai.troop.TroopManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class ModTickHandler {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        TroopManager.tickAllTroops();
    }
}

