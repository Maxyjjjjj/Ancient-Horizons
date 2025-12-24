package com.fungoussoup.ancienthorizons.bestiary;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents a single bestiary entry for a mob/entity
 */
public class BestiaryEntry {
    private final ResourceLocation entityType;
    private String name;
    private String scientificName;
    private String period;
    private String description;
    private boolean discovered;

    public BestiaryEntry(ResourceLocation entityType) {
        this.entityType = entityType;
        this.discovered = false;
    }

    public BestiaryEntry(ResourceLocation entityType, String name, String scientificName, String period, String description) {
        this.entityType = entityType;
        this.name = name;
        this.scientificName = scientificName;
        this.period = period;
        this.description = description;
        this.discovered = false;
    }

    // Getters and setters
    public ResourceLocation getEntityType() {
        return entityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    /**
     * Gets the registry path for the bestiary text file
     */
    public String getBestiaryFilePath(String language) {
        return "data/ancienthorizons/bestiary/" + language + "/" + entityType.getPath() + ".txt";
    }

    @Override
    public String toString() {
        return "BestiaryEntry{" +
                "entityType=" + entityType +
                ", name='" + name + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", period='" + period + '\'' +
                ", discovered=" + discovered +
                '}';
    }
}
