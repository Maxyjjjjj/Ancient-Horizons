package com.fungoussoup.ancienthorizons.compat.jade;

import net.minecraft.world.entity.animal.Animal;
import snownee.jade.api.*;

@WailaPlugin
public class JadeBreedingSeasonPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(JadeBreedingSeasonProvider.INSTANCE, Animal.class);
    }
}