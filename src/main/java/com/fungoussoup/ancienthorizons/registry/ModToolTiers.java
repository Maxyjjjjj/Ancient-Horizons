package com.fungoussoup.ancienthorizons.registry;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {

    // 🔹 Early-game metals
    public static final Tier LEAD = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_LEAD_TOOL,
            90,       // durability (slightly better than stone)
            3.5f,     // mining speed
            1.5f,     // attack damage
            12,       // enchantability
            () -> Ingredient.of(ModItems.LEAD_INGOT)
    );

    public static final Tier BRONZE = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_BRONZE_TOOL,
            220,      // durability (between lead & iron)
            5.0f,     // mining speed (better than iron)
            2.0f,     // attack damage
            15,       // enchantability (decent)
            () -> Ingredient.of(ModItems.BRONZE_ALLOY)
    );

    // 🔹 Mid-game progression
    public static final Tier NICKEL = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_NICKEL_TOOL,
            380,      // between iron & diamond
            6.0f,     // mining speed
            2.5f,     // attack damage
            12,       // enchantability
            () -> Ingredient.of(ModItems.NICKEL_INGOT)
    );

    public static final Tier COBALT = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_COBALT_TOOL,
            1234,     // high durability (between diamond & tungsten)
            7.5f,     // mining speed (faster than diamond)
            3.2f,     // attack damage
            14,       // decent enchantability
            () -> Ingredient.of(ModItems.COBALT_INGOT)
    );

    public static final Tier TUNGSTEN = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_TUNGSTEN_TOOL,
            1820,     // very durable
            5.0f,     // heavy → slower than cobalt
            4.0f,     // hard-hitting
            8,        // low enchantability
            () -> Ingredient.of(ModItems.TUNGSTEN_INGOT)
    );

    public static final Tier OSMIUM = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_OSMIUM_TOOL,
            2450,     // extreme durability (just below Netherite)
            7.0f,     // strong balance of speed
            4.5f,     // high attack
            10,       // moderate enchantability
            () -> Ingredient.of(ModItems.OSMIUM_INGOT)
    );

    // 🔹 Side tiers
    public static final Tier SILVER = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_SILVER_TOOL,
            120,      // weak durability
            4.5f,     // decent speed
            2.6f,     // sharp
            20,       // very enchantable
            () -> Ingredient.of(ModItems.SILVER_INGOT)
    );

    public static final Tier PLATINUM = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_PLATINUM_TOOL,
            900,      // durable
            6.0f,     // solid speed
            3.0f,     // solid attack
            18,       // high enchantability
            () -> Ingredient.of(ModItems.PLATINUM_INGOT)
    );

    // 🔹 Already done in your file
    public static final Tier ALUMINIUM = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_ALUMINIUM_TOOL,
            180,      // fragile
            7.0f,     // lightweight → very fast mining
            1.5f,     // weak damage
            22,       // very enchantable
            () -> Ingredient.of(ModItems.ALUMINIUM_INGOT)
    );
}

