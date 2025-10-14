package com.fungoussoup.ancienthorizons.worldgen.feature;

import com.fungoussoup.ancienthorizons.registry.ModTrunkPlacerTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class PalmTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<PalmTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec((placer) -> trunkPlacerParts(placer).apply(placer, PalmTrunkPlacer::new));

    public PalmTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.PALM_TRUNK_PLACER.get();
    }


    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        setDirtAt(pLevel, pBlockSetter, pRandom, pPos.below(), pConfig);

        // Place main trunk
        for (int i = 0; i < pFreeTreeHeight; i++) {
            placeLog(pLevel, pBlockSetter, pRandom, pPos.above(i), pConfig);
        }

        // Add slight curve to the trunk for more natural palm look
        BlockPos topPos = pPos.above(pFreeTreeHeight - 1);

        // Randomly choose a direction for the palm to lean
        Direction leanDirection = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);

        // Place a curved top section
        if (pRandom.nextFloat() < 0.7f) {
            BlockPos curvedPos = topPos.relative(leanDirection);
            if (TreeFeature.isAirOrLeaves(pLevel, curvedPos)) {
                placeLog(pLevel, pBlockSetter, pRandom, curvedPos, pConfig);
                topPos = curvedPos;
            }
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(topPos, 0, false));
    }
}
