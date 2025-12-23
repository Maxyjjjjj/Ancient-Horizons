package com.fungoussoup.ancienthorizons.worldgen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_TIMESTONE_ORE = registerKey("add_timestone_ore");
    public static final ResourceKey<BiomeModifier> ADD_ALUMINIUM_ORE = registerKey("add_aluminium_ore");
    public static final ResourceKey<BiomeModifier> ADD_TIN_ORE = registerKey("add_tin_ore");
    public static final ResourceKey<BiomeModifier> ADD_SILVER_ORE = registerKey("add_silver_ore");
    public static final ResourceKey<BiomeModifier> ADD_PLATINUM_ORE = registerKey("add_platinum_ore");
    public static final ResourceKey<BiomeModifier> ADD_COBALT_ORE = registerKey("add_cobalt_ore");
    public static final ResourceKey<BiomeModifier> ADD_TUNGSTEN_ORE = registerKey("add_tungsten_ore");
    public static final ResourceKey<BiomeModifier> ADD_ZIRCON_ORE = registerKey("add_zircon_ore");

    public static final ResourceKey<BiomeModifier> SPAWN_TIGER = registerKey("spawn_tiger");
    public static final ResourceKey<BiomeModifier> SPAWN_SNOW_LEOPARD = registerKey("spawn_snow_leopard");
    public static final ResourceKey<BiomeModifier> SPAWN_GIRAFFE = registerKey("spawn_giraffe");
    public static final ResourceKey<BiomeModifier> SPAWN_PANGOLIN = registerKey("spawn_pangolin");
    public static final ResourceKey<BiomeModifier> SPAWN_SEAGULL = registerKey("spawn_seagull");
    public static final ResourceKey<BiomeModifier> SPAWN_ELEPHANT = registerKey("spawn_elephant");
    public static final ResourceKey<BiomeModifier> SPAWN_RACCOON = registerKey("spawn_raccoon");
    public static final ResourceKey<BiomeModifier> SPAWN_EARTHWORM = registerKey("spawn_earthworm");
    public static final ResourceKey<BiomeModifier> SPAWN_ZEBRA = registerKey("spawn_zebra");
    public static final ResourceKey<BiomeModifier> SPAWN_MANTIS = registerKey("spawn_mantis");
    public static final ResourceKey<BiomeModifier> SPAWN_BACTRIAN_CAMEL = registerKey("spawn_bactrian_camel");
    public static final ResourceKey<BiomeModifier> SPAWN_BELUGA_STURGEON = registerKey("spawn_beluga_sturgeon");
    public static final ResourceKey<BiomeModifier> SPAWN_BROWN_BEAR = registerKey("spawn_brown_bear");
    public static final ResourceKey<BiomeModifier> SPAWN_CHIMP = registerKey("spawn_chimp");
    public static final ResourceKey<BiomeModifier> SPAWN_EAGLE = registerKey("spawn_eagle");
    public static final ResourceKey<BiomeModifier> SPAWN_FLAMINGO = registerKey("spawn_flamingo");
    public static final ResourceKey<BiomeModifier> SPAWN_PENGUIN = registerKey("spawn_penguin");
    public static final ResourceKey<BiomeModifier> SPAWN_SAOLA = registerKey("spawn_saola");
    public static final ResourceKey<BiomeModifier> SPAWN_STOAT = registerKey("spawn_stoat");
    public static final ResourceKey<BiomeModifier> SPAWN_RUFF = registerKey("spawn_ruff");
    public static final ResourceKey<BiomeModifier> SPAWN_ROE_DEER = registerKey("spawn_roe_deer");
    public static final ResourceKey<BiomeModifier> SPAWN_PHILIPPINE_EAGLE = registerKey("spawn_philippine_eagle");
    public static final ResourceKey<BiomeModifier> SPAWN_LION = registerKey("spawn_lion");
    public static final ResourceKey<BiomeModifier> SPAWN_MONKEY = registerKey("spawn_monkey");
    public static final ResourceKey<BiomeModifier> SPAWN_HOATZIN = registerKey("spawn_hoatzin");
    public static final ResourceKey<BiomeModifier> SPAWN_HIPPO = registerKey("spawn_hippo");
    public static final ResourceKey<BiomeModifier> SPAWN_HARE = registerKey("spawn_hare");
    public static final ResourceKey<BiomeModifier> SPAWN_FISHER = registerKey("spawn_fisher");
    public static final ResourceKey<BiomeModifier> SPAWN_WHITE_SHARK = registerKey("spawn_white_shark");
    public static final ResourceKey<BiomeModifier> SPAWN_DEER = registerKey("spawn_deer");
    public static final ResourceKey<BiomeModifier> SPAWN_CROC = registerKey("spawn_croc");
    public static final ResourceKey<BiomeModifier> SPAWN_CICADA = registerKey("spawn_cicada");
    public static final ResourceKey<BiomeModifier> SPAWN_WOLVERINE = registerKey("spawn_wolverine");
    public static final ResourceKey<BiomeModifier> SPAWN_MERGANSER = registerKey("spawn_merganser");
    public static final ResourceKey<BiomeModifier> SPAWN_WORM = registerKey("spawn_worm");

    public static final ResourceKey<BiomeModifier> SPAWN_BLACKCAP = registerKey("spawn_blackcap");
    public static final ResourceKey<BiomeModifier> SPAWN_BLUETHROAT = registerKey("spawn_bluethroat");
    public static final ResourceKey<BiomeModifier> SPAWN_BULLFINCH = registerKey("spawn_bullfinch");
    public static final ResourceKey<BiomeModifier> SPAWN_CANARY = registerKey("spawn_canary");
    public static final ResourceKey<BiomeModifier> SPAWN_CARDINAL = registerKey("spawn_cardinal");
    public static final ResourceKey<BiomeModifier> SPAWN_CHAFFINCH = registerKey("spawn_chaffinch");
    public static final ResourceKey<BiomeModifier> SPAWN_GOLDCREST = registerKey("spawn_goldcrest");
    public static final ResourceKey<BiomeModifier> SPAWN_GOLDFINCH = registerKey("spawn_goldfinch");
    public static final ResourceKey<BiomeModifier> SPAWN_NIGHTINGALE = registerKey("spawn_nightingale");
    public static final ResourceKey<BiomeModifier> SPAWN_REDSTART = registerKey("spawn_redstart");
    public static final ResourceKey<BiomeModifier> SPAWN_REEDLING = registerKey("spawn_reedling");
    public static final ResourceKey<BiomeModifier> SPAWN_ROBIN = registerKey("spawn_robin");
    public static final ResourceKey<BiomeModifier> SPAWN_SISKIN = registerKey("spawn_siskin");
    public static final ResourceKey<BiomeModifier> SPAWN_SKYLARK = registerKey("spawn_skylark");
    public static final ResourceKey<BiomeModifier> SPAWN_SPARROW = registerKey("spawn_sparrow");
    public static final ResourceKey<BiomeModifier> SPAWN_TIT = registerKey("spawn_tit");
    public static final ResourceKey<BiomeModifier> SPAWN_WAGTAIL = registerKey("spawn_wagtail");
    public static final ResourceKey<BiomeModifier> SPAWN_WAXWING = registerKey("spawn_waxwing");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TIMESTONE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TIMESTONE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_ALUMINIUM_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALUMINIUM_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_TIN_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TIN_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_SILVER_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SILVER_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_PLATINUM_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PLATINUM_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_COBALT_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.COBALT_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_TUNGSTEN_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.TUNGSTEN_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ADD_ZIRCON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ZIRCON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));

        context.register(SPAWN_TIGER, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TIGER.get(), 2, 1, 2))));

        context.register(SPAWN_SNOW_LEOPARD, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SNOW_LEOPARD.get(), 7, 1, 2))));

        context.register(SPAWN_GIRAFFE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.GIRAFFE.get(), 5, 2, 3))));

        context.register(SPAWN_ELEPHANT, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.ELEPHANT.get(), 3, 2, 4))));

        context.register(SPAWN_ZEBRA, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.ZEBRA.get(), 10, 3, 5))));

        context.register(SPAWN_SEAGULL, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SEAGULL.get(), 7, 2, 5))));

        context.register(SPAWN_EARTHWORM, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.EARTHWORM.get(), 7, 1, 1))));

        context.register(SPAWN_RACCOON, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.RACCOON.get(), 7, 1, 2))));

        context.register(SPAWN_PANGOLIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PANGOLIN.get(), 7, 1, 2))));

        context.register(SPAWN_MANTIS, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MANTIS.get(), 3, 1, 2))));

        context.register(SPAWN_BACTRIAN_CAMEL, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BACTRIAN_CAMEL.get(), 4, 2, 6))));

        context.register(SPAWN_BELUGA_STURGEON, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OCEAN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BELUGA_STURGEON.get(), 4, 1, 2))));

        context.register(SPAWN_BROWN_BEAR, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BROWN_BEAR.get(), 4, 1, 2))));

        context.register(SPAWN_BLACKCAP, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BLACKCAP.get(), 6, 1, 3))));

        context.register(SPAWN_BLUETHROAT, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BLUETHROAT.get(), 6, 1, 3))));

        context.register(SPAWN_BULLFINCH, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BULLFINCH.get(), 6, 1, 3))));

        context.register(SPAWN_CANARY, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CANARY.get(), 8, 1, 4))));

        context.register(SPAWN_CARDINAL, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CARDINAL.get(), 5, 1, 2))));

        context.register(SPAWN_CHAFFINCH, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CHAFFINCH.get(), 10, 1, 4))));

        context.register(SPAWN_GOLDCREST, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.GOLDCREST.get(), 6, 1, 2))));

        context.register(SPAWN_GOLDFINCH, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.GOLDFINCH.get(), 8, 1, 3))));

        context.register(SPAWN_NIGHTINGALE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.NIGHTINGALE.get(), 5, 1, 2))));

        context.register(SPAWN_REDSTART, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.REDSTART.get(), 5, 1, 2))));

        context.register(SPAWN_REEDLING, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.REEDLING.get(), 6, 1, 3))));

        context.register(SPAWN_ROBIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.ROBIN.get(), 9, 1, 3))));

        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 6, 1, 3))));

        context.register(SPAWN_SKYLARK, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SKYLARK.get(), 10, 1, 3))));

        context.register(SPAWN_SPARROW, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SPARROW.get(), 12, 2, 4))));

        context.register(SPAWN_TIT, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.TIT.get(), 9, 1, 3))));

        context.register(SPAWN_WAGTAIL, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.WAGTAIL.get(), 8, 1, 3))));

        context.register(SPAWN_WAXWING, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_SNOWY),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.WAXWING.get(), 4, 1, 2))));


        context.register(SPAWN_FLAMINGO, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_BEACH),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.FLAMINGO.get(), 8, 3, 5))));

        context.register(SPAWN_EAGLE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.EAGLE.get(), 2, 1, 2))));

        context.register(SPAWN_CHIMP, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CHIMPANZEE.get(), 6, 2, 4))));

        context.register(SPAWN_STOAT, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_COLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.STOAT.get(), 8, 2, 3))));

        context.register(SPAWN_PENGUIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_SNOWY),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PENGUIN.get(), 10, 3, 6))));

        context.register(SPAWN_SAOLA, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BAMBOO_JUNGLE)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SAOLA.get(), 1, 2, 3))));

        context.register(SPAWN_WHITE_SHARK, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OCEAN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.WHITE_SHARK.get(), 3, 1, 1))
        ));

        context.register(SPAWN_CICADA, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CICADA.get(), 10, 3, 6))
        ));

        context.register(SPAWN_CROC, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_SWAMP),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.CROCODILE.get(), 5, 1, 2))
        ));

        context.register(SPAWN_DEER, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.DEER.get(), 8, 2, 4))
        ));

        context.register(SPAWN_FISHER, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.FISHER.get(), 6, 1, 2))
        ));

        context.register(SPAWN_HARE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HARE.get(), 10, 2, 3))
        ));

        context.register(SPAWN_HIPPO, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HIPPOPOTAMUS.get(), 4, 1, 2))
        ));

        context.register(SPAWN_HOATZIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.MANGROVE_SWAMP)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HOATZIN.get(), 5, 2, 4))
        ));

        context.register(SPAWN_LION, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.LION.get(), 3, 1, 3))
        ));

        context.register(SPAWN_MANTIS, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MANTIS.get(), 8, 1, 3))
        ));

        context.register(SPAWN_PHILIPPINE_EAGLE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PHILIPPINE_EAGLE.get(), 2, 1, 1))
        ));

        context.register(SPAWN_ROE_DEER, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.ROE_DEER.get(), 7, 2, 3))
        ));

        context.register(SPAWN_RUFF, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_PLAINS),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.RUFF.get(), 6, 1, 3))
        ));

        context.register(SPAWN_MONKEY, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MONKEY.get(), 8, 2, 5))
        ));

        context.register(SPAWN_WOLVERINE, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.WOLVERINE.get(), 6, 1, 3))
        ));

        context.register(SPAWN_MERGANSER, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_RIVER),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MERGANSER.get(), 6, 1, 2))
        ));

        context.register(SPAWN_WORM, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_RAIN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.EARTHWORM.get(), 10, 1, 2))
        ));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
    }
}
