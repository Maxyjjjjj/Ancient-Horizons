package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Provider for custom enchantments.
 * Enchantments are registered through the bootstrap system.
 */
public class ModEnchantmentProvider extends DatapackBuiltinEntriesProvider {

    public ModEnchantmentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModDatapackProvider.BUILDER, Set.of(AncientHorizons.MOD_ID));
    }
}