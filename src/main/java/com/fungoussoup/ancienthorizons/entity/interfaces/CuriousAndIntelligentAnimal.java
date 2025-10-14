package com.fungoussoup.ancienthorizons.entity.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;

public interface CuriousAndIntelligentAnimal {
    PathNavigation getNavigation();

    double distanceToSqr(double x, double y, double z);

    BlockGetter level();

    RandomSource getRandom();

    BlockPos blockPosition();
}
