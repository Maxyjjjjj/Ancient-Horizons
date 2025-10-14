package com.fungoussoup.ancienthorizons.registry;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class ModArmourTypes {
    public static final Map<Item, String> SNOW_LEOPARD_ARMOR_TYPES = new HashMap<>();

    public static void register(Item armor, String type) {
        SNOW_LEOPARD_ARMOR_TYPES.put(armor, type);
    }

    public static boolean isSnowLeopardArmor(Item item) {
        return SNOW_LEOPARD_ARMOR_TYPES.containsKey(item);
    }
}
