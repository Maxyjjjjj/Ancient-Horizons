package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.effect.TranquilizedMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {
    // DeferredRegister for MobEffect
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, AncientHorizons.MOD_ID);

    // Registering the Tranquilized Effect
    public static final DeferredHolder<MobEffect, TranquilizedMobEffect> TRANQUILIZED =
            MOB_EFFECTS.register("tranquilized", TranquilizedMobEffect::new);

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
