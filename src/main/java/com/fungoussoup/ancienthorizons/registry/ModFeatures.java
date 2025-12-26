package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, AncientHorizons.MOD_ID);

    public static final Supplier<Feature<HugeMushroomFeatureConfiguration>>
            HUGE_STINKHORN_MUSHROOM = FEATURES.register(
            "huge_stinkhorn_mushroom",
            () -> new AbstractHugeMushroomFeature(HugeMushroomFeatureConfiguration.CODEC) {

                @Override
                protected int getTreeRadiusForHeight(
                        int height, int y, int foliageHeight, int radius
                ) {
                    return 3; // stinkhorn cap radius
                }

                private void emitStink(LevelAccessor level, BlockPos pos, RandomSource random) {
                    if (!level.isClientSide()) {
                        if (level instanceof net.minecraft.server.level.ServerLevel server) {
                            server.sendParticles(
                                    net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                    pos.getX() + 0.5,
                                    pos.getY() + 1.0,
                                    pos.getZ() + 0.5,
                                    8,
                                    0.3, 0.3, 0.3,
                                    0.01
                            );
                        }
                    }
                }

                private void aggroNearbyCreepers(LevelAccessor level, BlockPos pos) {
                    if (!(level instanceof net.minecraft.server.level.ServerLevel server)) return;

                    server.getEntitiesOfClass(
                            net.minecraft.world.entity.monster.Creeper.class,
                            new net.minecraft.world.phys.AABB(pos).inflate(16),
                            creeper -> creeper.isAlive()
                    ).forEach(creeper -> {
                        creeper.setTarget(null); // clear old target
                        creeper.getNavigation().moveTo(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                1.2
                        );
                    });
                }

                @Override
                protected void makeCap(LevelAccessor level, RandomSource random, BlockPos pos, int height, BlockPos.MutableBlockPos mutablePos, HugeMushroomFeatureConfiguration config) {
                    int capHeight = 4;
                    int maxRadius = 3;

                    for (int y = 0; y < capHeight; y++) {
                        float t = (float) y / (capHeight - 1);
                        int radius = Math.max(1, Math.round(maxRadius * (1.0f - t * 0.6f)));

                        for (int dx = -radius; dx <= radius; dx++) {
                            for (int dz = -radius; dz <= radius; dz++) {
                                if (dx * dx + dz * dz <= radius * radius) {
                                    mutablePos.set(
                                            pos.getX() + dx,
                                            pos.getY() + height + y,
                                            pos.getZ() + dz
                                    );

                                    emitStink(level, mutablePos, random);
                                }
                            }
                        }
                    }

                    aggroNearbyCreepers(level, pos);
                }

            }
    );

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
