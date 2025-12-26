package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModBlocks;
import com.fungoussoup.ancienthorizons.registry.ModEntities;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AncientHorizons.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.ALUMINIUM)
                .add(ModItems.ALUMINIUM_INGOT.get())
                .add(ModItems.ALUMINIUM_NUGGET.get());

        tag(ItemTags.SWORDS)
                .add(ModItems.ALUMINIUM_SWORD.get())
                .add(ModItems.BRONZE_SWORD.get())
                .add(ModItems.SILVER_SWORD.get())
                .add(ModItems.PLATINUM_SWORD.get())
                .add(ModItems.COBALT_SWORD.get())
                .add(ModItems.TUNGSTEN_SWORD.get())
                .add(ModItems.LEAD_SWORD.get());
        tag(ItemTags.PICKAXES)
                .add(ModItems.ALUMINIUM_PICKAXE.get())
                .add(ModItems.BRONZE_PICKAXE.get())
                .add(ModItems.SILVER_PICKAXE.get())
                .add(ModItems.PLATINUM_PICKAXE.get())
                .add(ModItems.COBALT_PICKAXE.get())
                .add(ModItems.TUNGSTEN_PICKAXE.get())
                .add(ModItems.LEAD_PICKAXE.get());
        tag(ItemTags.AXES)
                .add(ModItems.ALUMINIUM_AXE.get())
                .add(ModItems.BRONZE_AXE.get())
                .add(ModItems.SILVER_AXE.get())
                .add(ModItems.PLATINUM_AXE.get())
                .add(ModItems.COBALT_AXE.get())
                .add(ModItems.TUNGSTEN_AXE.get())
                .add(ModItems.LEAD_AXE.get());
        tag(ItemTags.SHOVELS)
                .add(ModItems.ALUMINIUM_SHOVEL.get())
                .add(ModItems.BRONZE_SHOVEL.get())
                .add(ModItems.SILVER_SHOVEL.get())
                .add(ModItems.PLATINUM_SHOVEL.get())
                .add(ModItems.COBALT_SHOVEL.get())
                .add(ModItems.TUNGSTEN_SHOVEL.get())
                .add(ModItems.LEAD_SHOVEL.get());
        tag(ItemTags.HOES)
                .add(ModItems.ALUMINIUM_HOE.get())
                .add(ModItems.BRONZE_HOE.get())
                .add(ModItems.SILVER_HOE.get())
                .add(ModItems.PLATINUM_HOE.get())
                .add(ModItems.COBALT_HOE.get())
                .add(ModItems.TUNGSTEN_HOE.get())
                .add(ModItems.LEAD_HOE.get());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.ALUMINIUM_HELMET.get())
                .add(ModItems.ALUMINIUM_CHESTPLATE.get())
                .add(ModItems.ALUMINIUM_LEGGINGS.get())
                .add(ModItems.ALUMINIUM_BOOTS.get())

                .add(ModItems.BRONZE_HELMET.get())
                .add(ModItems.BRONZE_CHESTPLATE.get())
                .add(ModItems.BRONZE_LEGGINGS.get())
                .add(ModItems.BRONZE_BOOTS.get())

                .add(ModItems.SILVER_HELMET.get())
                .add(ModItems.SILVER_CHESTPLATE.get())
                .add(ModItems.SILVER_LEGGINGS.get())
                .add(ModItems.SILVER_BOOTS.get())

                .add(ModItems.PLATINUM_HELMET.get())
                .add(ModItems.PLATINUM_CHESTPLATE.get())
                .add(ModItems.PLATINUM_LEGGINGS.get())
                .add(ModItems.PLATINUM_BOOTS.get())

                .add(ModItems.COBALT_HELMET.get())
                .add(ModItems.COBALT_CHESTPLATE.get())
                .add(ModItems.COBALT_LEGGINGS.get())
                .add(ModItems.COBALT_BOOTS.get())

                .add(ModItems.TUNGSTEN_HELMET.get())
                .add(ModItems.TUNGSTEN_CHESTPLATE.get())
                .add(ModItems.TUNGSTEN_LEGGINGS.get())
                .add(ModItems.TUNGSTEN_BOOTS.get());

        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.WILLOW_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_LOG.get().asItem())
                .add(ModBlocks.WILLOW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_WILLOW_WOOD.get().asItem())
        
                .add(ModBlocks.HORNBEAM_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_HORNBEAM_LOG.get().asItem())
                .add(ModBlocks.HORNBEAM_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_HORNBEAM_WOOD.get().asItem())
                
                .add(ModBlocks.LINDEN_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_LINDEN_LOG.get().asItem())
                .add(ModBlocks.LINDEN_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_LINDEN_WOOD.get().asItem())
                
                .add(ModBlocks.GINKGO_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_GINKGO_LOG.get().asItem())
                .add(ModBlocks.GINKGO_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_GINKGO_WOOD.get().asItem())
        
                .add(ModBlocks.POPLAR_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_POPLAR_LOG.get().asItem())
                .add(ModBlocks.POPLAR_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_POPLAR_WOOD.get().asItem())
        
                .add(ModBlocks.MAPLE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_MAPLE_LOG.get().asItem())
                .add(ModBlocks.MAPLE_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_MAPLE_WOOD.get().asItem())
                
                .add(ModBlocks.BAOBAB_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_BAOBAB_LOG.get().asItem())
                .add(ModBlocks.BAOBAB_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_BAOBAB_WOOD.get().asItem())
                
                .add(ModBlocks.PALM_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_PALM_LOG.get().asItem())
                .add(ModBlocks.PALM_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_PALM_WOOD.get().asItem())
                
                .add(ModBlocks.ASPEN_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_ASPEN_LOG.get().asItem())
                .add(ModBlocks.ASPEN_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_ASPEN_WOOD.get().asItem())
                
                .add(ModBlocks.ASH_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_ASH_LOG.get().asItem())
                .add(ModBlocks.ASH_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_ASH_WOOD.get().asItem())

                .add(ModBlocks.BEECH_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_BEECH_LOG.get().asItem())
                .add(ModBlocks.BEECH_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_BEECH_WOOD.get().asItem())

                .add(ModBlocks.EUCALYPTUS_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_EUCALYPTUS_LOG.get().asItem())
                .add(ModBlocks.EUCALYPTUS_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_EUCALYPTUS_WOOD.get().asItem())

                .add(ModBlocks.SYCAMORE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_SYCAMORE_LOG.get().asItem())
                .add(ModBlocks.SYCAMORE_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_SYCAMORE_WOOD.get().asItem())

                .add(ModBlocks.REDWOOD_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_REDWOOD_LOG.get().asItem())
                .add(ModBlocks.REDWOOD_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_REDWOOD_WOOD.get().asItem())

                .add(ModBlocks.MONKEY_PUZZLE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_MONKEY_PUZZLE_LOG.get().asItem())
                .add(ModBlocks.MONKEY_PUZZLE_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_MONKEY_PUZZLE_WOOD.get().asItem())

                .add(ModBlocks.YEW_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_YEW_LOG.get().asItem())
                .add(ModBlocks.YEW_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_YEW_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.WILLOW_PLANKS.asItem())
                .add(ModBlocks.HORNBEAM_PLANKS.asItem())
                .add(ModBlocks.LINDEN_PLANKS.asItem())
                .add(ModBlocks.GINKGO_PLANKS.asItem())
                .add(ModBlocks.POPLAR_PLANKS.asItem())
                .add(ModBlocks.MAPLE_PLANKS.asItem())
                .add(ModBlocks.BAOBAB_PLANKS.asItem())
                .add(ModBlocks.PALM_PLANKS.asItem())
                .add(ModBlocks.ASPEN_PLANKS.asItem())
                .add(ModBlocks.ASH_PLANKS.asItem())
                .add(ModBlocks.BEECH_PLANKS.asItem())
                .add(ModBlocks.EUCALYPTUS_PLANKS.asItem())
                .add(ModBlocks.SYCAMORE_PLANKS.asItem())
                .add(ModBlocks.REDWOOD_PLANKS.asItem())
                .add(ModBlocks.MONKEY_PUZZLE_PLANKS.asItem())
                .add(ModBlocks.YEW_PLANKS.asItem());

        // CUSTOM METAL TAG
        tag(ModTags.Items.ALUMINIUM)
                .add(ModItems.ALUMINIUM_INGOT.get())
                .add(ModItems.ALUMINIUM_NUGGET.get());

        // ANIMAL FOOD TAGS
        tag(ModTags.Items.PASSERINE_FOOD)
                .addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(Items.WHEAT_SEEDS)
                .add(Items.BEETROOT_SEEDS)
                .add(Items.MELON_SEEDS)
                .add(Items.PUMPKIN_SEEDS);

        tag(ModTags.Items.SEAGULL_FOOD)
                .addTag(ItemTags.FISHES)
                .add(Items.BREAD)
                .add(Items.COOKIE)
                .add(ModItems.RAW_SHRIMP.get());

        tag(ModTags.Items.SEAGULL_ROBBABLES)
                .addTag(ItemTags.FISHES)
                .add(Items.BREAD)
                .add(Items.COOKIE)
                .add(Items.POTATO)
                .add(Items.CARROT)
                .add(Items.BEETROOT)
                .add(ModItems.RAW_SHRIMP.get());

        tag(ModTags.Items.RACCOON_FOOD)
                .addTag(ItemTags.FISHES)
                .add(Items.APPLE)
                .add(Items.SWEET_BERRIES)
                .add(Items.GLOW_BERRIES)
                .add(Items.MELON_SLICE)
                .add(Items.EGG)
                .add(Items.CHICKEN)
                .add(Items.COOKED_CHICKEN)
                .add(ModItems.BANANA.get());

        tag(ModTags.Items.ELEPHANT_FOOD)
                .add(Items.WHEAT)
                .add(Items.HAY_BLOCK)
                .add(Items.APPLE)
                .add(Items.GOLDEN_APPLE)
                .add(Items.GOLDEN_CARROT)
                .add(Items.CARROT)
                .add(Items.SUGAR_CANE)
                .add(ModItems.BANANA.get());

        tag(ModTags.Items.MANTIS_FOOD)
                .add(Items.SPIDER_EYE)
                .add(Items.FERMENTED_SPIDER_EYE);

        tag(ModTags.Items.STOAT_FOOD)
                .add(Items.CHICKEN)
                .add(Items.COOKED_CHICKEN)
                .add(Items.RABBIT)
                .add(Items.COOKED_RABBIT);

        tag(ModTags.Items.SAOLA_FOOD)
                .addTag(ItemTags.LEAVES)
                .add(Items.FERN)
                .add(Items.BAMBOO);

        tag(ModTags.Items.AZHDARCHID_FOOD)
                .addTag(ItemTags.FISHES)
                .add(Items.CHICKEN)
                .add(Items.RABBIT)
                .add(Items.MUTTON);

        tag(ModTags.Items.PENGUIN_FOOD)
                .addTag(ItemTags.FISHES)
                .add(ModItems.RAW_SHRIMP.get());

        tag(ModTags.Items.PENGUIN_FOOD)
                .addTag(ItemTags.FISHES)
                .add(ModItems.RAW_SHRIMP.get());

        tag(ModTags.Items.CROC_FOOD)
                .addTag(ItemTags.MEAT)
                .addTag(ItemTags.FISHES)
                .add(Items.ROTTEN_FLESH);

        tag(ModTags.Items.PREHISTORIC_HERBIVORE_FOOD)
                .add(Items.FERN)
                .add(Items.LARGE_FERN)
                .add(Items.SPRUCE_LEAVES);

        // WEAPON ENCHANTMENT TAGS
        tag(ModTags.Items.KATANA_ENCHANTABLE)
                .add(ModItems.SILVER_KATANA.get());

        tag(ModTags.Items.HAMMER_ENCHANTABLE)
                .add(ModItems.ALUMINIUM_HAMMER.get());

        tag(ModTags.Items.CHAKRAM_ENCHANTABLE)
                .add(ModItems.COBALT_CHAKRAM.get());

        tag(ModTags.Items.KHOPESH_ENCHANTABLE)
                .add(ModItems.GOLDEN_KHOPESH.get());

        tag(ModTags.Items.CLUB_ENCHANTABLE)
                .add(ModItems.WOODEN_CLUB.get());

        tag(ModTags.Items.HALBERD_ENCHANTABLE)
                .add(ModItems.BRONZE_HALBERD.get());

        tag(ModTags.Items.MACUAHUITL_ENCHANTABLE)
                .add(ModItems.STONE_MACUAHUITL.get());

        tag(ModTags.Items.WARFANS_ENCHANTABLE)
                .add(ModItems.IRON_WARFANS.get());

        tag(ModTags.Items.WARSCYTHE_ENCHANTABLE)
                .add(ModItems.DIAMOND_WARSCYTHE.get());
    }
}
