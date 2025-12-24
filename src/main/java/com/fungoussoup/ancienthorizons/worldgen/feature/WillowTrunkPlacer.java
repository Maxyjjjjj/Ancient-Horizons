package com.fungoussoup.ancienthorizons.worldgen.feature;

import com.fungoussoup.ancienthorizons.registry.ModTrunkPlacerTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WillowTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<WillowTrunkPlacer> CODEC =
            RecordCodecBuilder.mapCodec((placer) ->
                    trunkPlacerParts(placer)
                            .and(Codec.INT.fieldOf("branch_length").forGetter(p -> p.branchLength))
                            .and(Codec.INT.fieldOf("branch_count").forGetter(p -> p.branchCount))
                            .apply(placer, WillowTrunkPlacer::new)
            );

    private final int branchLength;
    private final int branchCount;

    public WillowTrunkPlacer(int baseHeight, int heightRandA, int heightRandB,
                             int branchLength, int branchCount) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchLength = branchLength;
        this.branchCount = branchCount;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.WILLOW_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int height,
            BlockPos startPos,
            TreeConfiguration config
    ) {
        setDirtAt(level, blockSetter, random, startPos.below(), config);

        BlockPos.MutableBlockPos pos = startPos.mutable();

        // Main vertical trunk
        for (int y = 0; y < height; y++) {
            placeLog(level, blockSetter, random, pos, config);
            pos.move(Direction.UP);
        }

        List<FoliagePlacer.FoliageAttachment> foliage = new ArrayList<>();

        // Top foliage
        foliage.add(new FoliagePlacer.FoliageAttachment(pos.above(), 0, false));

        // Side drooping branches
        for (int i = 0; i < branchCount; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);

            int startY = height - 2 - random.nextInt(2);
            BlockPos branchStart = startPos.above(startY);

            BlockPos.MutableBlockPos branchPos = branchStart.mutable();

            for (int len = 0; len < branchLength; len++) {
                branchPos.move(dir);

                placeLog(level, blockSetter, random, branchPos, config);

                // Willow droop
                if (random.nextFloat() < 0.7F) {
                    branchPos.move(Direction.DOWN);
                }
            }

            foliage.add(new FoliagePlacer.FoliageAttachment(branchPos.above(), 0, false));
        }

        return foliage;
    }
}
