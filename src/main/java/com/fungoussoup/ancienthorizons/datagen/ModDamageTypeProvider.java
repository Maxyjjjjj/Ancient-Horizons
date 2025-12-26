package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.registry.ModDamageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeProvider {

    public static void bootstrap(BootstrapContext<DamageType> context) {
        // TAIL_WHIP - Physical attack from dinosaur tails
        context.register(ModDamageTypes.TAIL_WHIP,
                new DamageType("ancienthorizons.tail_whip", 0.1F));

        // Additional damage types can be added here
        // Example formats:
        // new DamageType(msgId, scaling)
        // new DamageType(msgId, scaling, effects)
    }
}