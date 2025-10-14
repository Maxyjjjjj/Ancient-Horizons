package com.fungoussoup.ancienthorizons.worldgen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModBlocks;
import com.fungoussoup.ancienthorizons.worldgen.feature.PalmFoliagePlacer;
import com.fungoussoup.ancienthorizons.worldgen.feature.PalmTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.OptionalInt;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> TIMESTONE_ORE_KEY = registerKey("timestone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALUMINIUM_ORE_KEY = registerKey("aluminium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TIN_ORE_KEY = registerKey("tin_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ORE_KEY = registerKey("silver_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLATINUM_ORE_KEY = registerKey("platinum_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COBALT_ORE_KEY = registerKey("cobalt_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUNGSTEN_ORE_KEY = registerKey("tungsten_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ZIRCON_ORE_KEY = registerKey("zircon_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_KEY = registerKey("willow");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HORNBEAM_KEY = registerKey("hornbeam");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LINDEN_KEY = registerKey("linden");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GINKGO_KEY = registerKey("ginkgo");
    public static final ResourceKey<ConfiguredFeature<?, ?>> POPLAR_KEY = registerKey("poplar");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAPLE_KEY = registerKey("maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BAOBAB_KEY = registerKey("baobab");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASPEN_KEY = registerKey("aspen");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASH_KEY = registerKey("ash");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BEECH_KEY = registerKey("beech");
    public static final ResourceKey<ConfiguredFeature<?, ?>> EUCALYPTUS_KEY = registerKey("eucalyptus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SYCAMORE_KEY = registerKey("sycamore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> REDWOOD_KEY = registerKey("redwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MONKEY_PUZZLE_KEY = registerKey("monkey_puzzle");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YEW_KEY = registerKey("yew");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> timestoneOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.TIMESTONE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_TIMESTONE_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> aluminiumOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.ALUMINIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_ALUMINIUM_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> tinOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.TIN_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_TIN_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> silverOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.SILVER_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_SILVER_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> platinumOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.PLATINUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_PLATINUM_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> cobaltOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.COBALT_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> tungstenOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.TUNGSTEN_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_TUNGSTEN_ORE.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> zirconOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.ZIRCON_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_ZIRCON_ORE.get().defaultBlockState()));

        register(context, TIMESTONE_ORE_KEY, Feature.ORE, new OreConfiguration(timestoneOres, 2));
        register(context, ALUMINIUM_ORE_KEY, Feature.ORE, new OreConfiguration(aluminiumOres, 5));
        register(context, TIN_ORE_KEY, Feature.ORE, new OreConfiguration(tinOres, 5));
        register(context, SILVER_ORE_KEY, Feature.ORE, new OreConfiguration(silverOres, 5));
        register(context, PLATINUM_ORE_KEY, Feature.ORE, new OreConfiguration(platinumOres, 5));
        register(context, COBALT_ORE_KEY, Feature.ORE, new OreConfiguration(cobaltOres, 5));
        register(context, TUNGSTEN_ORE_KEY, Feature.ORE, new OreConfiguration(tungstenOres, 3));
        register(context, ZIRCON_ORE_KEY, Feature.ORE, new OreConfiguration(zirconOres, 3));

        register(context, WILLOW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.WILLOW_LOG.get()),
                new CherryTrunkPlacer(4, 3, 3,
                        UniformInt.of(1, 3),
                        UniformInt.of(2, 4),
                        UniformInt.of(-4, -3),
                        UniformInt.of(-1, 0)),
                BlockStateProvider.simple(ModBlocks.WILLOW_LEAVES.get()),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25F, 0.5F, 0.16666667F, 0.33333334F),

                new TwoLayersFeatureSize(1, 0, 2)).build());

        register(context, HORNBEAM_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.HORNBEAM_LOG.get()),
                new DarkOakTrunkPlacer(4,2,1),
                BlockStateProvider.simple(ModBlocks.HORNBEAM_LEAVES.get()),
                new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)),

                new ThreeLayersFeatureSize(1, 1, 0,1,2,OptionalInt.empty())).build());

        register(context, LINDEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.LINDEN_LOG.get()),
                new StraightTrunkPlacer(6,0,1),
                BlockStateProvider.simple(ModBlocks.LINDEN_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0),5),

                new TwoLayersFeatureSize(1, 1,1)).build());

        register(context, GINKGO_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.GINKGO_LOG.get()),
                new StraightTrunkPlacer(6,1,1),
                BlockStateProvider.simple(ModBlocks.GINKGO_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0),5),

                new TwoLayersFeatureSize(1, 1,1)).build());

        register(context, POPLAR_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.POPLAR_LOG.get()),
                new StraightTrunkPlacer(10,3,3),
                BlockStateProvider.simple(ModBlocks.POPLAR_LEAVES.get()),
                new SpruceFoliagePlacer(ConstantInt.of(1), ConstantInt.of(1), UniformInt.of(1,3)),

                new TwoLayersFeatureSize(1, 0,1)).build());
        
        register(context, MAPLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MAPLE_LOG.get()),
                new CherryTrunkPlacer(5, 3, 3, ConstantInt.of(3), UniformInt.of(2, 4), UniformInt.of(-4, -3), UniformInt.of(-1, 0)),
                BlockStateProvider.simple(ModBlocks.MAPLE_LEAVES.get()),
                new FancyFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(1), 4),

                new TwoLayersFeatureSize(1, 0,1)).build());
        
        register(context, BAOBAB_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.BAOBAB_LOG.get()),
                new DarkOakTrunkPlacer(3, 1, 1),
                BlockStateProvider.simple(ModBlocks.BAOBAB_LEAVES.get()),
                new AcaciaFoliagePlacer(UniformInt.of(3,3), ConstantInt.of(1)),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, PALM_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.PALM_LOG.get()),
                new PalmTrunkPlacer(4, 2, 3), // Custom trunk placer
                BlockStateProvider.simple(ModBlocks.PALM_LEAVES.get()),
                new PalmFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), ConstantInt.of(5)), // Custom foliage placer with frond length of 5
                new TwoLayersFeatureSize(1, 0, 1)).build());

        register(context, ASPEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.ASPEN_LOG.get()),
                new StraightTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.ASPEN_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0), 3),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, ASH_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.ASH_LOG.get()),
                new StraightTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.ASH_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0), 3),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, BEECH_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.BEECH_LOG.get()),
                new StraightTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.BEECH_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0), 3),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, EUCALYPTUS_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.EUCALYPTUS_LOG.get()),
                new StraightTrunkPlacer(8, 6, 4), // Taller, straighter trunk (8-18 blocks high)
                BlockStateProvider.simple(ModBlocks.EUCALYPTUS_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(3, 4), ConstantInt.of(0), 3), // Dense, rounded canopy

                new TwoLayersFeatureSize(2, 0, 2)).build()); // Larger minimum size

        register(context, SYCAMORE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SYCAMORE_LOG.get()),
                new StraightTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.SYCAMORE_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0),3),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, REDWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.REDWOOD_LOG.get()),
                new GiantTrunkPlacer(17, 6, 6),
                BlockStateProvider.simple(ModBlocks.REDWOOD_LEAVES.get()),
                new SpruceFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0),UniformInt.of(5, 12)),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, MONKEY_PUZZLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.MONKEY_PUZZLE_LOG.get()),
                new StraightTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.MONKEY_PUZZLE_LEAVES.get()),
                new SpruceFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0),UniformInt.of(1,3)),

                new TwoLayersFeatureSize(1, 0,1)).build());

        register(context, YEW_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.YEW_LOG.get()),
                new DarkOakTrunkPlacer(4, 3, 3),
                BlockStateProvider.simple(ModBlocks.YEW_LEAVES.get()),
                new BlobFoliagePlacer(UniformInt.of(2,3), ConstantInt.of(0),3),

                new TwoLayersFeatureSize(1, 0,1)).build());

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
