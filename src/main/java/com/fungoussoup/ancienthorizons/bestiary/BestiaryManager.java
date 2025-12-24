package com.fungoussoup.ancienthorizons.bestiary;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Manages loading and accessing bestiary entries from text files
 */
public class BestiaryManager {
    private static final Map<ResourceLocation, BestiaryEntry> ENTRIES = new HashMap<>();
    private static String currentLanguage = "en_us";

    /**
     * Loads all bestiary entries for the current language
     */
    public static void loadBestiaryEntries() {
        ENTRIES.clear();

        Minecraft mc = Minecraft.getInstance();

        ResourceManager resourceManager = mc.getResourceManager();
        currentLanguage = mc.options.languageCode;

        // Get all bestiary files for current language
        String bestiaryPath = "ancienthorizons:bestiary/" + currentLanguage + "/";

        // List of known entities to load (expand this as you add more mobs)
        List<String> knownEntities = Arrays.asList(
                "tiger", "snow_leopard", "lion", "elephant", "giraffe",
                "zebra", "crocodile", "hippopotamus", "penguin", "eagle",
                "saola", "diplodocus", "gallimimus", "velociraptor", "bullfinch",
                "bactrian_camel", "beluga_sturgeon", "blackcap", "brown_bear",
                "bluethroat", "canary", "cardinal", "chaffinch", "chimpanzee", "cicada",
                "deer", "fisher", "flamingo", "goldcrest", "goldfinch", "hare", "hoatzin",
                "mantis", "merganser", "monkey", "nightingale", "pangolin", "penguin",
                "philippine_eagle", "raccoon", "redstart", "reedling", "robin", "roe_deer",
                "ruff", "seagull", "siskin", "skylark", "sparrow", "stoat", "tit", "wagtail",
                "waxwing", "white_shark", "wildebeest", "wolverine", "earthworm", "cryodrakon",
                "beipiaosaurus", "dearc", "domestic_goat", "eromangasaurus", "hypnovenator",
                "latenivenatrix", "maip", "red_panda", "pheasant", "roadrunner", "saichania",
                "pig", "cow", "sheep", "chicken", "wolf", "squid", "armadillo", "axolotl",
                "bee", "camel", "cat", "cod", "dolphin", "fox", "frog", "donkey", "horse",
                "goat", "llama", "ocelot", "panda", "parrot", "polar_bear", "rabbit", "turtle",
                "eagle_owl", "snowy_owl", "grey_owl", "little_owl"
        );

        for (String entityName : knownEntities) {
            ResourceLocation entityId = ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, entityName);
            ResourceLocation fileLocation = ResourceLocation.fromNamespaceAndPath(
                    AncientHorizons.MOD_ID,
                    "bestiary/" + currentLanguage + "/" + entityName + ".txt"
            );

            Optional<Resource> resourceOpt = resourceManager.getResource(fileLocation);
            if (resourceOpt.isPresent()) {
                BestiaryEntry entry = loadEntry(resourceOpt.get(), entityId);
                if (entry != null) {
                    ENTRIES.put(entityId, entry);
                    AncientHorizons.LOGGER.info("Loaded bestiary entry for: {}", entityName);
                }
            } else {
                // Try fallback to English if current language file doesn't exist
                if (!currentLanguage.equals("en_us")) {
                    ResourceLocation fallbackLocation = ResourceLocation.fromNamespaceAndPath(
                            AncientHorizons.MOD_ID,
                            "bestiary/en_us/" + entityName + ".txt"
                    );
                    Optional<Resource> fallbackOpt = resourceManager.getResource(fallbackLocation);
                    if (fallbackOpt.isPresent()) {
                        BestiaryEntry entry = loadEntry(fallbackOpt.get(), entityId);
                        if (entry != null) {
                            ENTRIES.put(entityId, entry);
                            AncientHorizons.LOGGER.info("Loaded bestiary entry (fallback) for: {}", entityName);
                        }
                    }
                }
            }
        }

        AncientHorizons.LOGGER.info("Loaded {} bestiary entries", ENTRIES.size());
    }

    /**
     * Loads a single bestiary entry from a resource
     */
    private static BestiaryEntry loadEntry(Resource resource, ResourceLocation entityId) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {

            BestiaryEntry entry = new BestiaryEntry(entityId);
            String line;
            StringBuilder descriptionBuilder = new StringBuilder();
            boolean readingDescription = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.startsWith("Name:")) {
                    entry.setName(line.substring(5).trim());
                } else if (line.startsWith("Scientific:")) {
                    entry.setScientificName(line.substring(11).trim());
                } else if (line.startsWith("Period:")) {
                    entry.setPeriod(line.substring(7).trim());
                } else if (line.startsWith("Description:")) {
                    readingDescription = true;
                } else if (readingDescription) {
                    if (!descriptionBuilder.isEmpty()) {
                        descriptionBuilder.append("\n");
                    }
                    descriptionBuilder.append(line);
                }
            }

            entry.setDescription(descriptionBuilder.toString().trim());
            return entry;

        } catch (IOException e) {
            AncientHorizons.LOGGER.error("Error reading bestiary file for {}", entityId, e);
            return null;
        }
    }

    /**
     * Gets a bestiary entry by entity resource location
     */
    public static BestiaryEntry getEntry(ResourceLocation entityType) {
        return ENTRIES.get(entityType);
    }

    /**
     * Gets all loaded bestiary entries
     */
    public static Collection<BestiaryEntry> getAllEntries() {
        return ENTRIES.values();
    }

    /**
     * Gets all discovered entries
     */
    public static List<BestiaryEntry> getDiscoveredEntries() {
        return ENTRIES.values().stream()
                .filter(BestiaryEntry::isDiscovered)
                .toList();
    }

    /**
     * Marks an entry as discovered
     */
    public static void discoverEntry(ResourceLocation entityType) {
        BestiaryEntry entry = ENTRIES.get(entityType);
        if (entry != null) {
            entry.setDiscovered(true);
        }
    }

    /**
     * Checks if an entry has been discovered
     */
    public static boolean isDiscovered(ResourceLocation entityType) {
        BestiaryEntry entry = ENTRIES.get(entityType);
        return entry != null && entry.isDiscovered();
    }

    /**
     * Gets the current language code
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Reloads bestiary entries (useful for language changes)
     */
    public static void reload() {
        loadBestiaryEntries();
    }
}
