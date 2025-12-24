package com.fungoussoup.ancienthorizons.bestiary;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores player bestiary discovery data persistently
 */
public class BestiaryData extends SavedData {
    private static final String DATA_NAME = "ancienthorizons_bestiary";
    private final Set<ResourceLocation> discoveredEntries = new HashSet<>();

    public BestiaryData() {
        super();
    }

    public BestiaryData(CompoundTag tag) {
        super();
        load(tag);
    }

    public BestiaryData(CompoundTag compoundTag, HolderLookup.Provider provider) {
    }

    /**
     * Gets or creates bestiary data for a world
     */
    public static BestiaryData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(
                        BestiaryData::new, BestiaryData::new
                ),
                DATA_NAME
        );
    }

    /**
     * Discovers a new bestiary entry
     */
    public boolean discover(ResourceLocation entityType) {
        boolean wasNew = discoveredEntries.add(entityType);
        if (wasNew) {
            setDirty();
        }
        return wasNew;
    }

    /**
     * Checks if an entry has been discovered
     */
    public boolean isDiscovered(ResourceLocation entityType) {
        return discoveredEntries.contains(entityType);
    }

    /**
     * Gets all discovered entries
     */
    public Set<ResourceLocation> getDiscoveredEntries() {
        return new HashSet<>(discoveredEntries);
    }

    /**
     * Loads data from NBT
     */
    private void load(CompoundTag tag) {
        discoveredEntries.clear();

        if (tag.contains("DiscoveredEntries")) {
            CompoundTag entriesTag = tag.getCompound("DiscoveredEntries");
            for (String key : entriesTag.getAllKeys()) {
                if (entriesTag.getBoolean(key)) {
                    discoveredEntries.add(ResourceLocation.parse(key));
                }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag entriesTag = new CompoundTag();

        for (ResourceLocation entityType : discoveredEntries) {
            entriesTag.putBoolean(entityType.toString(), true);
        }

        tag.put("DiscoveredEntries", entriesTag);
        return tag;
    }
}