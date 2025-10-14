package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.advancement.ChimpBarterTrigger;
import com.fungoussoup.ancienthorizons.advancement.OrnithologistTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModAdvancements {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS;
    public static final Supplier<ChimpBarterTrigger> BARTER_WITH_CHIMP;
    public static final Supplier<OrnithologistTrigger> TAME_ALL_BIRDS;

    static {
        TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, AncientHorizons.MOD_ID);
        BARTER_WITH_CHIMP = TRIGGERS.register("monkey_business", ChimpBarterTrigger::new);
        TAME_ALL_BIRDS = TRIGGERS.register("tame_all_birds", OrnithologistTrigger::new);
    }
}