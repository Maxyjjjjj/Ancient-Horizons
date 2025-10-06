package com.fungoussoup.ancienthorizons.worldgen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
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
    public static final ResourceKey<BiomeModifier> SPAWN_GOAT = registerKey("spawn_goat");
    public static final ResourceKey<BiomeModifier> SPAWN_EAGLE = registerKey("spawn_eagle");
    public static final ResourceKey<BiomeModifier> SPAWN_FLAMINGO = registerKey("spawn_flamingo");
    public static final ResourceKey<BiomeModifier> SPAWN_PENGUIN = registerKey("spawn_penguin");
    public static final ResourceKey<BiomeModifier> SPAWN_PHEASANT = registerKey("spawn_pheasant");
    public static final ResourceKey<BiomeModifier> SPAWN_SAOLA = registerKey("spawn_saola");
    public static final ResourceKey<BiomeModifier> SPAWN_STOAT = registerKey("spawn_stoat");

    public static final ResourceKey<BiomeModifier> SPAWN_BLACKCAP = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_BLUETHROAT = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_BULLFINCH = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_CANARY = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_CARDINAL = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_CHAFFINCH = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_GOLDCREST = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_GOLDFINCH = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_NIGHTINGALE = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_REDSTART = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_REEDLING = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_ROBIN = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_SISKIN = registerKey("spawn_siskin");
    public static final ResourceKey<BiomeModifier> SPAWN_SKYLARK = registerKey("spawn_skylark");
    public static final ResourceKey<BiomeModifier> SPAWN_SPARROW = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_TIT = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_WAGTAIL = registerKey("spawn_passerine");
    public static final ResourceKey<BiomeModifier> SPAWN_WAXWING = registerKey("spawn_passerine");

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
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.RACCOON.get(), 7, 1, 2))));

        context.register(SPAWN_PANGOLIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_SAVANNA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.PANGOLIN.get(), 7, 1, 2))));

        context.register(SPAWN_MANTIS, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.MANTIS.get(), 3, 1, 2))));

        context.register(SPAWN_BACTRIAN_CAMEL, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DESERT)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BACTRIAN_CAMEL.get(), 4, 2, 6))));

        context.register(SPAWN_BELUGA_STURGEON, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OCEAN),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BELUGA_STURGEON.get(), 4, 1, 2))));

        context.register(SPAWN_BROWN_BEAR, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BROWN_BEAR.get(), 4, 1, 2))));

        context.register(SPAWN_BLACKCAP, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BLACKCAP.get(), 7, 1, 3))));
        context.register(SPAWN_BLUETHROAT, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BLUETHROAT.get(), 7, 1, 3))));
        context.register(SPAWN_BULLFINCH, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.BULLFINCH.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
        context.register(SPAWN_SISKIN, new BiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.SISKIN.get(), 7, 1, 3))));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
    }
}
