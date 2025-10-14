package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> CHIMP_BARTERING = register("gameplay/chimp_bartering");

    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
    }
}