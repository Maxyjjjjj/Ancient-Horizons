package com.fungoussoup.ancienthorizons.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;


public class ModFoodProperties {
    public static final FoodProperties VODKA = (new FoodProperties.Builder())
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 400), 0.99f).build();
    public static final FoodProperties BELUGA_STURGEON_CAVIAR = (new FoodProperties.Builder()
            .nutrition(3).saturationModifier(1).build());
    public static final FoodProperties BANANA = (new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.4f).build());
    public static final FoodProperties RAW_SHRIMP = (new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.3f).build());
}