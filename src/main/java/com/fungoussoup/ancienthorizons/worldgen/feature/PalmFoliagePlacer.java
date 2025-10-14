package com.fungoussoup.ancienthorizons.worldgen.feature;

import com.fungoussoup.ancienthorizons.registry.ModFoliagePlacerTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class PalmFoliagePlacer extends FoliagePlacer {

    public static final MapCodec<PalmFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance)
                    .and(IntProvider.CODEC.fieldOf("frond_length").forGetter(p -> p.frondLength))
                    .apply(instance, PalmFoliagePlacer::new)
    );


    private final IntProvider frondLength;

    public PalmFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider frondLength) {
        super(radius, offset);
        this.frondLength = frondLength;
    }


    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacerTypes.PALM_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        BlockPos centerPos = attachment.pos();
        int frondCount = 6 + random.nextInt(3); // 6-8 fronds
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < frondCount; i++) {
            float angle = (float) (2 * Math.PI * i / frondCount);
            angle += (random.nextFloat() - 0.5f) * 0.5f; // Slight randomness

            int currentFrondLength = frondLength.sample(random) + random.nextInt(3) - 1;
            currentFrondLength = Math.max(2, currentFrondLength); // Ensure at least 2 length

            for (int j = 1; j <= currentFrondLength; j++) {
                float progress = (float) j / currentFrondLength;
                int x = Math.round((float) Math.cos(angle) * j);
                int z = Math.round((float) Math.sin(angle) * j);
                int y = -(int) (progress * progress * 2); // Quadratic droop

                mutablePos.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                if (TreeFeature.isAirOrLeaves(level, mutablePos)) {
                    foliageSetter.set(mutablePos, config.foliageProvider.getState(random, mutablePos));

                    // Add side leaves
                    if (j > 1 && j < currentFrondLength - 1) {
                        float perpAngle = angle + (float) Math.PI / 2;
                        for (int side = -1; side <= 1; side += 2) {
                            if (random.nextFloat() < 0.6f) {
                                int sideX = Math.round((float) Math.cos(perpAngle) * side);
                                int sideZ = Math.round((float) Math.sin(perpAngle) * side);

                                BlockPos sidePos = mutablePos.offset(sideX, 0, sideZ);
                                if (TreeFeature.isAirOrLeaves(level, sidePos)) {
                                    foliageSetter.set(sidePos, config.foliageProvider.getState(random, sidePos));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return 0; // All foliage is placed at the top
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false; // All custom-placed
    }
}
