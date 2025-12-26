package com.fungoussoup.ancienthorizons.block.custom;

import com.fungoussoup.ancienthorizons.worldgen.ModConfiguredFeatures;
import com.fungoussoup.ancienthorizons.worldgen.tree.ModTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Random;

public class StinkhornBlock extends MushroomBlock {

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 12, 12);

    public StinkhornBlock(ResourceKey<ConfiguredFeature<?, ?>> feature, Properties properties) {
        super(ModConfiguredFeatures.HUGE_STINKHORN, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        // Attract creepers nearby
        List<Creeper> creepers = level.getEntitiesOfClass(Creeper.class, state.getShape(level, pos, CollisionContext.empty()).bounds().move(pos.getX(), pos.getY(), pos.getZ()).inflate(8));
        for (Creeper creeper : creepers) {
            // Make creeper target nearest player within range
            Player nearest = level.getNearestPlayer(creeper, 8);
            if (nearest != null) {
                creeper.setTarget(nearest);
            }
        }

        super.randomTick(state, level, pos, (RandomSource) random);
    }
}
