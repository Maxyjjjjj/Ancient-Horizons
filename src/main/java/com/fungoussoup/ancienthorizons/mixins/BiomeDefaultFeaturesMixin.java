package com.fungoussoup.ancienthorizons.mixins;

import com.fungoussoup.ancienthorizons.registry.ModEntities;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDefaultFeatures.class)
public class BiomeDefaultFeaturesMixin {

    @Inject(method = "farmAnimals", at = @At("RETURN"))
    private static void addModFarmAnimals(MobSpawnSettings.Builder builder, CallbackInfo ci) {
        builder.addSpawn(
                MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(ModEntities.DOMESTIC_GOAT.get(), 8, 1, 3)
        );
        builder.addSpawn(
                MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(ModEntities.PHEASANT.get(), 10, 2, 4)
        );
    }
}