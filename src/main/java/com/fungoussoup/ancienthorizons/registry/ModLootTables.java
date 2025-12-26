package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> CHIMP_BARTERING = register("gameplay/chimp_bartering");
    public static final ResourceKey<LootTable> CHIMP_BARTERING_FOOD = register("gameplay/chimp_bartering_food");
    public static final ResourceKey<LootTable> CHIMP_BARTERING_TREASURES = register("gameplay/chimp_bartering_treasures");
    public static final ResourceKey<LootTable> CHIMP_BARTERING_TRASH = register("gameplay/chimp_bartering_trash");
    public static final ResourceKey<LootTable> CHIMP_BARTERING_BAMBOO = register("gameplay/chimp_bartering_bamboo");

    private static ResourceKey<LootTable> register(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, path));
    }
}