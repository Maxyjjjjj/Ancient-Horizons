package com.fungoussoup.ancienthorizons.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

import java.util.Optional;
import java.util.function.Supplier;

public enum Mods {
    SERENE_SEASONS("sereneseasons"),
    CREATE("create"),
    FARMERS_DELIGHT("farmersdelight"),
    JADE("jade"),
    AUTOMOBILITY("automobility"),
    TOUGH_AS_NAILS("toughasnails"),
    IMMERSIVE_AIRCRAFT("immersive_aircraft"),
    IMMERSIVE_ENGINEERING("immersiveengineering");

    private final String id;

    Mods(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.id, path);
    }

    public Block getBlock(String id) {
        return BuiltInRegistries.BLOCK.get(this.rl(id));
    }

    public Item getItem(String id) {
        return BuiltInRegistries.ITEM.get(this.rl(id));
    }

    public boolean contains(ItemLike entry) {
        if (!this.isLoaded()) {
            return false;
        } else {
            Item asItem = entry.asItem();
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(asItem);
            return itemKey.getNamespace().equals(this.id);
        }
    }

    public boolean isLoaded() {
        return ModList.get().isLoaded(this.id);
    }

    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        return this.isLoaded() ? Optional.of(toRun.get().get()) : Optional.empty();
    }

    public void executeIfInstalled(Supplier<Runnable> toExecute) {
        if (this.isLoaded()) {
            toExecute.get().run();
        }
    }

    // Additional utility methods for better compatibility handling

    /**
     * Gets a block safely, returning null if the mod isn't loaded or block doesn't exist
     */
    public Block getBlockSafely(String blockId) {
        if (!isLoaded()) return null;
        Block block = getBlock(blockId);
        return block != BuiltInRegistries.BLOCK.get(BuiltInRegistries.BLOCK.getDefaultKey()) ? block : null;
    }

    /**
     * Gets an item safely, returning null if the mod isn't loaded or item doesn't exist
     */
    public Item getItemSafely(String itemId) {
        if (!isLoaded()) return null;
        Item item = getItem(itemId);
        return item != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()) ? item : null;
    }

    /**
     * Checks if a specific block exists in this mod
     */
    public boolean hasBlock(String blockId) {
        return isLoaded() && getBlockSafely(blockId) != null;
    }

    /**
     * Checks if a specific item exists in this mod
     */
    public boolean hasItem(String itemId) {
        return isLoaded() && getItemSafely(itemId) != null;
    }
}
