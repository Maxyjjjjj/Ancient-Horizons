package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> NEEDS_ALUMINIUM_TOOL = createTag("needs_aluminium_tool");
        public static final TagKey<Block> INCORRECT_FOR_ALUMINIUM_TOOL = createTag("incorrect_for_aluminium_tool");

        public static final TagKey<Block> NEEDS_SILVER_TOOL = createTag("needs_silver_tool");
        public static final TagKey<Block> INCORRECT_FOR_SILVER_TOOL = createTag("incorrect_for_silver_tool");

        public static final TagKey<Block> NEEDS_PLATINUM_TOOL = createTag("needs_platinum_tool");
        public static final TagKey<Block> INCORRECT_FOR_PLATINUM_TOOL = createTag("incorrect_for_platinum_tool");

        public static final TagKey<Block> NEEDS_TUNGSTEN_TOOL = createTag("needs_tungsten_tool");
        public static final TagKey<Block> INCORRECT_FOR_TUNGSTEN_TOOL = createTag("incorrect_for_tungsten_tool");

        public static final TagKey<Block> INCORRECT_FOR_LEAD_TOOL = createTag("incorrect_for_lead_tool");
        public static final TagKey<Block> INCORRECT_FOR_BRONZE_TOOL = createTag("incorrect_for_bronze_tool");
        public static final TagKey<Block> INCORRECT_FOR_NICKEL_TOOL = createTag("incorrect_for_nickel_tool");
        public static final TagKey<Block> INCORRECT_FOR_COBALT_TOOL = createTag("incorrect_for_cobalt_tool");
        public static final TagKey<Block> INCORRECT_FOR_OSMIUM_TOOL = createTag("incorrect_for_osmium_tool");

        public static final TagKey<Block> ALUMINIUM_ORES = createTag("aluminium_ores");

        public static final TagKey<Block> PENGUINS_SPAWN_ON = createTag("penguins_spawn_on");
        public static final TagKey<Block> ROADRUNNERS_SPAWN_ON = createTag("roadrunners_spawn_on");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ALUMINIUM = createTag("aluminium");

        public static final TagKey<Item> PASSERINE_FOOD = createTag("passerine_food");
        public static final TagKey<Item> SEAGULL_FOOD = createTag("seagull_food");
        public static final TagKey<Item> SEAGULL_ROBBABLES = createTag("seagull_robbables");
        public static final TagKey<Item> RACCOON_FOOD = createTag("raccoon_food");
        public static final TagKey<Item> ELEPHANT_FOOD = createTag("elephant_food");
        public static final TagKey<Item> MANTIS_FOOD = createTag("mantis_food");
        public static final TagKey<Item> STOAT_FOOD = createTag("stoat_food");
        public static final TagKey<Item> SAOLA_FOOD = createTag("saola_food");
        public static final TagKey<Item> AZHDARCHID_FOOD = createTag("azhdarchid_food");
        public static final TagKey<Item> PENGUIN_FOOD = createTag("penguin_food");

        public static final TagKey<Item> KATANA_ENCHANTABLE = createTag("enchantable/katana");
        public static final TagKey<Item> WARFANS_ENCHANTABLE = createTag("enchantable/warfans");
        public static final TagKey<Item> KHOPESH_ENCHANTABLE = createTag("enchantable/khopesh");
        public static final TagKey<Item> HAMMER_ENCHANTABLE = createTag("enchantable/hammer");
        public static final TagKey<Item> CHAKRAM_ENCHANTABLE = createTag("enchantable/chakram");
        public static final TagKey<Item> MACUAHUITL_ENCHANTABLE = createTag("enchantable/macuahuitl");
        public static final TagKey<Item> HALBERD_ENCHANTABLE = createTag("enchantable/halberd");
        public static final TagKey<Item> CLUB_ENCHANTABLE = createTag("enchantable/club");
        public static final TagKey<Item> WARSCYTHE_ENCHANTABLE = createTag("enchantable/warscythe");

        public static final TagKey<Item> CROC_FOOD = createTag("croc_food");
        public static final TagKey<Item> PREHISTORIC_HERBIVORE_FOOD = createTag("prehistoric_herbivore_food");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> TIGER_PREY = createTag("tiger_prey");
        public static final TagKey<EntityType<?>> SNOW_LEOPARD_PREY = createTag("snow_leopard_prey");
        public static final TagKey<EntityType<?>> WOLF_PREY = createTag("wolf_prey");
        public static final TagKey<EntityType<?>> TIGER_ENEMIES = createTag("tiger_enemies");
        public static final TagKey<EntityType<?>> MANTIS_PREY = createTag("mantis_prey");
        public static final TagKey<EntityType<?>> EAGLE_PREY = createTag("eagle_prey");
        public static final TagKey<EntityType<?>> POLAR_BEAR_PREY = createTag("polar_bear_prey");
        public static final TagKey<EntityType<?>> FOX_PREY = createTag("fox_prey");
        public static final TagKey<EntityType<?>> STOAT_PREY = createTag("stoat_prey");
        public static final TagKey<EntityType<?>> AZHDARCHID_PREY = createTag("azhdarchid_prey");
        public static final TagKey<EntityType<?>> FOSSIL_ANIMALS = createTag("fossil_animals");

        public static final TagKey<EntityType<?>> PASSERINES = createTag("passerines");
        public static final TagKey<EntityType<?>> FISHER_PREY = createTag("fisher_prey");
        public static final TagKey<EntityType<?>> ANACONDA_PREY = createTag("anaconda_prey");
        public static final TagKey<EntityType<?>> HYPNOVENATOR_PREY = createTag("hypnivenator_prey");

        // DIET TYPES
        public static final TagKey<EntityType<?>> CARNIVORES = createTag("carnivores");
        public static final TagKey<EntityType<?>> HERBIVORES = createTag("herbivores");
        public static final TagKey<EntityType<?>> OMNIVORES = createTag("omnivores");
        public static final TagKey<EntityType<?>> SPECIALISTS = createTag("dietary_specialists");
        public static final TagKey<EntityType<?>> CROCODILE_LAND_PREY = createTag("croc_land_prey");
        public static final TagKey<EntityType<?>> PHILIPPINE_EAGLE_PREY = createTag("philippine_eagle_prey");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> HAS_RAIN = createTag("has_rain");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
        }
    }
}
