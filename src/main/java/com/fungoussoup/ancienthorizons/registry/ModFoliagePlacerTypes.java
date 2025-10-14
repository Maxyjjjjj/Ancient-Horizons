package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.worldgen.feature.PalmFoliagePlacer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFoliagePlacerTypes {

    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(BuiltInRegistries.FOLIAGE_PLACER_TYPE, AncientHorizons.MOD_ID);

    public static final Supplier<FoliagePlacerType<PalmFoliagePlacer>> PALM_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("palm_foliage_placer", () -> new FoliagePlacerType<>(PalmFoliagePlacer.CODEC));

    public static void register(IEventBus eventBus) {
        FOLIAGE_PLACERS.register(eventBus);
    }
}

