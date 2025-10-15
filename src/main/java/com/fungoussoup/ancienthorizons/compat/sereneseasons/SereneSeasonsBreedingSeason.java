package com.fungoussoup.ancienthorizons.compat.sereneseasons;

import com.fungoussoup.ancienthorizons.entity.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.*;

import static sereneseasons.api.season.Season.SubSeason.*;

public class SereneSeasonsBreedingSeason {

    // Map of entity types to their breeding seasons
    private static final Map<EntityType<?>, Set<Season.SubSeason>> BREEDING_SEASONS = new HashMap<>();

    static {
        initializeBreedingSeasons();
    }

    // Helper to generate a translation key for an entity's description
    private static String getEntityDescKey(EntityType<?> type) {
        ResourceLocation loc = EntityType.getKey(type);
        return "description.ancienthorizons.sereneseasons." + loc.getNamespace() + "." + loc.getPath();
    }

    // Helper to generate a translation key for a sub-season
    private static String getSubSeasonKey(Season.SubSeason subSeason) {
        return "subseason.ancienthorizons." + subSeason.name().toLowerCase(Locale.ROOT);
    }

    private static void initializeBreedingSeasons() {
        // --- SPRING BREEDERS ---
        addBreedingSeason(EntityType.COW, EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(EntityType.SHEEP, EnumSet.of(EARLY_SPRING));
        addBreedingSeason(EntityType.GOAT, EnumSet.of(EARLY_SPRING));
        addBreedingSeason(EntityType.DONKEY, EnumSet.of(MID_SPRING, LATE_SPRING));
        addBreedingSeason(EntityType.HORSE, EnumSet.of(MID_SPRING, LATE_SPRING));
        addBreedingSeason(EntityType.CAMEL, EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(EntityType.PANDA, EnumSet.of(EARLY_SPRING));
        addBreedingSeason(EntityType.COD, EnumSet.of(EARLY_SPRING));
        addBreedingSeason(EntityType.ARMADILLO, EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(EntityType.TURTLE, EnumSet.of(LATE_SPRING));

        // --- SPRING TO EARLY SUMMER ---
        addBreedingSeason(EntityType.OCELOT, EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(EntityType.FROG, EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));

        // --- LATE SPRING & SUMMER BREEDERS ---
        addBreedingSeason(EntityType.PIG, EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(EntityType.CHICKEN, EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(EntityType.LLAMA, EnumSet.of(EARLY_SUMMER));
        addBreedingSeason(EntityType.RABBIT, EnumSet.of(EARLY_SUMMER, MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(EntityType.BEE, EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(EntityType.TROPICAL_FISH, EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(EntityType.DOLPHIN, EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(EntityType.PUFFERFISH, EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(EntityType.SNIFFER, EnumSet.of(LATE_SPRING, EARLY_SUMMER));

        // --- AUTUMN BREEDERS ---
        addBreedingSeason(EntityType.FOX, EnumSet.of(EARLY_AUTUMN, MID_AUTUMN));
        addBreedingSeason(EntityType.WOLF, EnumSet.of(EARLY_AUTUMN, MID_AUTUMN));
        addBreedingSeason(EntityType.SALMON, EnumSet.of(EARLY_AUTUMN, MID_AUTUMN));

        // --- WINTER BREEDERS ---
        addBreedingSeason(EntityType.POLAR_BEAR, EnumSet.of(EARLY_WINTER, MID_WINTER));

        // --- YEAR-ROUND BREEDERS (Explicity defined for description purposes) ---
        addBreedingSeason(EntityType.CAT, EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(EntityType.AXOLOTL, EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(EntityType.MOOSHROOM, EnumSet.allOf(Season.SubSeason.class));

        // --- MODDED ENTITIES ---
        addBreedingSeason(ModEntities.SNOW_LEOPARD.get(), EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(ModEntities.MANTIS.get(), EnumSet.of(LATE_SPRING));
        addBreedingSeason(ModEntities.RACCOON.get(), EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(ModEntities.GIRAFFE.get(), EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.ZEBRA.get(), EnumSet.of(EARLY_SUMMER, MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.ELEPHANT.get(), EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.BROWN_BEAR.get(), EnumSet.of(MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.DOMESTIC_GOAT.get(), EnumSet.of(EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.SPARROW.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.EAGLE.get(), EnumSet.of(LATE_WINTER, EARLY_SPRING, MID_SPRING));
        addBreedingSeason(ModEntities.TIGER.get(), EnumSet.of(MID_AUTUMN, LATE_AUTUMN));
        addBreedingSeason(ModEntities.PANGOLIN.get(), EnumSet.of(MID_AUTUMN, LATE_AUTUMN));
        addBreedingSeason(ModEntities.PENGUIN.get(), EnumSet.of(EARLY_WINTER, MID_WINTER));
        addBreedingSeason(ModEntities.SEAGULL.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.BLACKCAP.get(), EnumSet.of(EARLY_SPRING, MID_SPRING));
        addBreedingSeason(ModEntities.BLUETHROAT.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.BULLFINCH.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.CANARY.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.CARDINAL.get(), EnumSet.of(MID_SPRING, LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.CHAFFINCH.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.GOLDCREST.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.GOLDFINCH.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.NIGHTINGALE.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.REDSTART.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.REEDLING.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.ROBIN.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.SISKIN.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.SKYLARK.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.TIT.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.WAGTAIL.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.WAXWING.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.BACTRIAN_CAMEL.get(), EnumSet.of(LATE_AUTUMN, EARLY_WINTER));
        addBreedingSeason(ModEntities.BELUGA_STURGEON.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.STOAT.get(), EnumSet.of(MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.PHEASANT.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.CHIMPANZEE.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.SAOLA.get(), EnumSet.of(LATE_SUMMER, EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN));
        addBreedingSeason(ModEntities.FLAMINGO.get(), EnumSet.of(MID_WINTER, LATE_WINTER, EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.ANACONDA.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.RUFF.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.FISHER.get(), EnumSet.of(LATE_WINTER, EARLY_SPRING));
        addBreedingSeason(ModEntities.ROADRUNNER.get(), EnumSet.of(MID_SPRING, LATE_SPRING, EARLY_SUMMER, MID_SUMMER));
        addBreedingSeason(ModEntities.HARE.get(), EnumSet.of(EARLY_SPRING));
        addBreedingSeason(ModEntities.CICADA.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.HOATZIN.get(), EnumSet.of(MID_SPRING, LATE_SPRING, EARLY_SUMMER));
        addBreedingSeason(ModEntities.DEER.get(), EnumSet.of(EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN));
        addBreedingSeason(ModEntities.ROE_DEER.get(), EnumSet.of(MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.CROCODILE.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.HIPPOPOTAMUS.get(), EnumSet.of(EARLY_SPRING, MID_SPRING, LATE_SPRING));
        addBreedingSeason(ModEntities.WHITE_SHARK.get(), EnumSet.of(EARLY_SUMMER, MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.LION.get(), EnumSet.of(LATE_SPRING, EARLY_SUMMER, MID_SUMMER, LATE_SUMMER));
        addBreedingSeason(ModEntities.MONKEY.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.PHILIPPINE_EAGLE.get(), EnumSet.of(LATE_WINTER, EARLY_SPRING));

        // --- MODDED ENTITIES (FOSSIL) ---

        addBreedingSeason(ModEntities.CRYODRAKON.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.DIPLODOCUS.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.HYPNOVENATOR.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.VELOCIRAPTOR.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.GALLIMIMUS.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.EROMANGASAURUS.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.BEIPIAOSAURUS.get(), EnumSet.allOf(Season.SubSeason.class));
        addBreedingSeason(ModEntities.DEARC.get(), EnumSet.allOf(Season.SubSeason.class));
    }

    private static void addBreedingSeason(EntityType<?> entityType, Set<Season.SubSeason> seasons) {
        BREEDING_SEASONS.put(entityType, seasons);
    }

    public static boolean isSereneSeasonsLoaded() {
        return ModList.get().isLoaded("sereneseasons");
    }

    public static boolean canBreedInCurrentSeason(EntityType<?> entityType, Level level) {
        // If Serene Seasons is not loaded, allow breeding unconditionally
        if (!isSereneSeasonsLoaded()) {
            return true;
        }

        Set<Season.SubSeason> breedingSeasons = BREEDING_SEASONS.get(entityType);
        if (breedingSeasons == null) {
            return true; // Default to always breedable if not specified
        }

        Season.SubSeason currentSeason = SeasonHelper.getSeasonState(level).getSubSeason();
        return breedingSeasons.contains(currentSeason);
    }

    public static Set<Season.SubSeason> getBreedingSeasons(EntityType<?> entityType) {
        return BREEDING_SEASONS.getOrDefault(entityType, EnumSet.allOf(Season.SubSeason.class));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // Only run this logic if Serene Seasons is loaded
        if (!isSereneSeasonsLoaded()) {
            return;
        }

        Item item = event.getItemStack().getItem();
        if (!(item instanceof SpawnEggItem spawnEgg)) {
            return;
        }

        EntityType<?> entityType = spawnEgg.getType(event.getItemStack());
        // Only add tooltip for entities we have data for
        if (!BREEDING_SEASONS.containsKey(entityType)) {
            return;
        }

        // Add empty line for spacing
        event.getToolTip().add(Component.empty());

        // Build the list of seasons
        Set<Season.SubSeason> breedingSeasons = getBreedingSeasons(entityType);
        MutableComponent seasonsComponent;

        if (breedingSeasons.size() == values().length) {
            seasonsComponent = Component.translatable("tooltip.ancienthorizons.sereneseasons.year_round");
        } else {
            // Sort seasons for consistent order
            List<Season.SubSeason> sortedSeasons = breedingSeasons.stream().sorted().toList();
            MutableComponent joinedSeasons = Component.empty();
            for (int i = 0; i < sortedSeasons.size(); i++) {
                joinedSeasons.append(Component.translatable(getSubSeasonKey(sortedSeasons.get(i))));
                if (i < sortedSeasons.size() - 1) {
                    joinedSeasons.append(", ");
                }
            }
            seasonsComponent = joinedSeasons;
        }

        // Add breeding seasons
        event.getToolTip().add(Component.translatable("tooltip.ancienthorizons.sereneseasons.breeding_seasons")
                .withStyle(ChatFormatting.GOLD)
                .append(Component.literal(": ")
                        .withStyle(ChatFormatting.GOLD))
                .append(seasonsComponent
                        .withStyle(ChatFormatting.YELLOW)));

        // Add description
        String descriptionKey = getEntityDescKey(entityType);
        event.getToolTip().add(Component.translatable(descriptionKey)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        // Add current season breeding status if the player is in a world
        Player player = event.getEntity();
        if (player != null) {
            Level level = player.level();
            boolean canBreed = canBreedInCurrentSeason(entityType, level);
            Season.SubSeason currentSeason = SeasonHelper.getSeasonState(level).getSubSeason();

            Component canBreedStatus = Component.translatable(canBreed ? "tooltip.ancienthorizons.sereneseasons.can_breed" : "tooltip.ancienthorizons.sereneseasons.cannot_breed")
                    .withStyle(canBreed ? ChatFormatting.GREEN : ChatFormatting.RED);

            Component statusComponent = Component.translatable("tooltip.ancienthorizons.sereneseasons.current_season")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(": "))
                    .append(Component.translatable(getSubSeasonKey(currentSeason)).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
                    .append(canBreedStatus);

            event.getToolTip().add(statusComponent);
        }
    }

    /**
     * Gets breeding difficulty description based on current season. Returns a translatable component.
     */
    public static Component getBreedingDifficultyDescription(EntityType<?> entityType, Level level) {
        if (isSereneSeasonsLoaded() || !BREEDING_SEASONS.containsKey(entityType)) {
            return Component.translatable("difficulty.ancienthorizons.sereneseasons.normal");
        }

        boolean canBreed = canBreedInCurrentSeason(entityType, level);
        String key = canBreed ? "difficulty.ancienthorizons.sereneseasons.in_season" : "difficulty.ancienthorizons.sereneseasons.out_of_season";
        return Component.translatable(key);
    }
}
