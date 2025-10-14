package com.fungoussoup.ancienthorizons.compat.jade;

import com.fungoussoup.ancienthorizons.compat.sereneseasons.SereneSeasonsBreedingSeason;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

class JadeBreedingSeasonProvider implements IEntityComponentProvider {

    public static final JadeBreedingSeasonProvider INSTANCE = new JadeBreedingSeasonProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        if (!(entity instanceof Animal animal)) return;

        Level level = animal.level();
        Component difficulty = SereneSeasonsBreedingSeason.getBreedingDifficultyDescription(animal.getType(), level);
        tooltip.add(difficulty);
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("ancienthorizons", "breeding_difficulty");
    }
}


