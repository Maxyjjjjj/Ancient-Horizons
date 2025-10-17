package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.entity.ModEntities;
import com.fungoussoup.ancienthorizons.entity.custom.vehicle.ModBoat;
import com.fungoussoup.ancienthorizons.item.*;
import com.fungoussoup.ancienthorizons.item.weapon.ChakramItem;
import com.fungoussoup.ancienthorizons.item.weapon.HammerItem;
import com.fungoussoup.ancienthorizons.item.weapon.KatanaItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.food.Foods;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AncientHorizons.MOD_ID);


    // TIMESTONE
    public static final DeferredItem<Item> TIME_STONE = ITEMS.register("time_stone",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_TIME_STONE = ITEMS.register("raw_time_stone",
            () -> new Item(new Item.Properties()));


    // ALUMINIUM
    public static final DeferredItem<Item> ALUMINIUM_INGOT = ITEMS.register("aluminium_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_ALUMINIUM = ITEMS.register("raw_aluminium",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ALUMINIUM_NUGGET = ITEMS.register("aluminium_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> ALUMINIUM_SWORD = ITEMS.register("aluminium_sword",
            () -> new SwordItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.ALUMINIUM,5.0f,-2.4f))));
    public static final DeferredItem<PickaxeItem> ALUMINIUM_PICKAXE = ITEMS.register("aluminium_pickaxe",
            () -> new PickaxeItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ALUMINIUM,1.4f,-2.4f))));
    public static final DeferredItem<ShovelItem> ALUMINIUM_SHOVEL = ITEMS.register("aluminium_shovel",
            () -> new ShovelItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.ALUMINIUM,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> ALUMINIUM_AXE = ITEMS.register("aluminium_axe",
            () -> new AxeItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.ALUMINIUM,6.0f,-3.2f))));
    public static final DeferredItem<HoeItem> ALUMINIUM_HOE = ITEMS.register("aluminium_hoe",
            () -> new HoeItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.ALUMINIUM,0f,-3.0f))));
    public static final DeferredItem<HammerItem> ALUMINIUM_HAMMER = ITEMS.register("aluminium_hammer",
            () -> new HammerItem(ModToolTiers.ALUMINIUM, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ALUMINIUM,2.8f,-2.8f))));

    public static final DeferredItem<ArmorItem> ALUMINIUM_HELMET = ITEMS.register("aluminium_helmet",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(15))));
    public static final DeferredItem<ArmorItem> ALUMINIUM_CHESTPLATE = ITEMS.register("aluminium_chestplate",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15))));
    public static final DeferredItem<ArmorItem> ALUMINIUM_LEGGINGS = ITEMS.register("aluminium_leggings",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(15))));
    public static final DeferredItem<ArmorItem> ALUMINIUM_BOOTS = ITEMS.register("aluminium_boots",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(15))));


    // TIN
    public static final DeferredItem<Item> RAW_TIN = ITEMS.register("raw_tin",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TIN_NUGGET = ITEMS.register("tin_nugget",
            () -> new Item(new Item.Properties()));


    // BRONZE
    public static final DeferredItem<Item> BRONZE_ALLOY = ITEMS.register("bronze_alloy",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRONZE_NUGGET = ITEMS.register("bronze_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> BRONZE_SWORD = ITEMS.register("bronze_sword",
            () -> new SwordItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.ALUMINIUM,3f,-2.4f))));
    public static final DeferredItem<PickaxeItem> BRONZE_PICKAXE = ITEMS.register("bronze_pickaxe",
            () -> new PickaxeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.ALUMINIUM,1.5f,-3.0f))));
    public static final DeferredItem<ShovelItem> BRONZE_SHOVEL = ITEMS.register("bronze_shovel",
            () -> new ShovelItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.ALUMINIUM,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> BRONZE_AXE = ITEMS.register("bronze_axe",
            () -> new AxeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.ALUMINIUM,6.0f,-3.1f))));
    public static final DeferredItem<HoeItem> BRONZE_HOE = ITEMS.register("bronze_hoe",
            () -> new HoeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.ALUMINIUM,-2.0f,-1.0f))));

    public static final DeferredItem<ArmorItem> BRONZE_HELMET = ITEMS.register("bronze_helmet",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(17))));
    public static final DeferredItem<ArmorItem> BRONZE_CHESTPLATE = ITEMS.register("bronze_chestplate",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(17))));
    public static final DeferredItem<ArmorItem> BRONZE_LEGGINGS = ITEMS.register("bronze_leggings",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(17))));
    public static final DeferredItem<ArmorItem> BRONZE_BOOTS = ITEMS.register("bronze_boots",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(17))));

    // SILVER
    public static final DeferredItem<Item> RAW_SILVER = ITEMS.register("raw_silver",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.register("silver_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_NUGGET = ITEMS.register("silver_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> SILVER_SWORD = ITEMS.register("silver_sword",
            () -> new SwordItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.SILVER,3.0f,-2.4f))));
    public static final DeferredItem<PickaxeItem> SILVER_PICKAXE = ITEMS.register("silver_pickaxe",
            () -> new PickaxeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.SILVER,1.0f,-2.8f))));
    public static final DeferredItem<ShovelItem> SILVER_SHOVEL = ITEMS.register("silver_shovel",
            () -> new ShovelItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.SILVER,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> SILVER_AXE = ITEMS.register("silver_axe",
            () -> new AxeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.SILVER,5.0f,-3f))));
    public static final DeferredItem<HoeItem> SILVER_HOE = ITEMS.register("silver_hoe",
            () -> new HoeItem(ModToolTiers.SILVER, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.SILVER,-3f,-0f))));
    public static final DeferredItem<KatanaItem> SILVER_KATANA = ITEMS.register("silver_katana",
            () -> new KatanaItem(new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.SILVER,2.3f,-0.5f))));

    public static final DeferredItem<ArmorItem> SILVER_HELMET = ITEMS.register("silver_helmet",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(18))));
    public static final DeferredItem<ArmorItem> SILVER_CHESTPLATE = ITEMS.register("silver_chestplate",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(18))));
    public static final DeferredItem<ArmorItem> SILVER_LEGGINGS = ITEMS.register("silver_leggings",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(18))));
    public static final DeferredItem<ArmorItem> SILVER_BOOTS = ITEMS.register("silver_boots",
            () -> new ArmorItem(ArmorMaterials.IRON, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(18))));


    // PLATINUM
    public static final DeferredItem<Item> RAW_PLATINUM = ITEMS.register("raw_platinum",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PLATINUM_INGOT = ITEMS.register("platinum_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PLATINUM_NUGGET = ITEMS.register("platinum_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> PLATINUM_SWORD = ITEMS.register("platinum_sword",
            () -> new SwordItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.PLATINUM,3.0f,-2.4f))));
    public static final DeferredItem<PickaxeItem> PLATINUM_PICKAXE = ITEMS.register("platinum_pickaxe",
            () -> new PickaxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.PLATINUM,1.0f,-2.8f))));
    public static final DeferredItem<ShovelItem> PLATINUM_SHOVEL = ITEMS.register("platinum_shovel",
            () -> new ShovelItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.PLATINUM,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> PLATINUM_AXE = ITEMS.register("platinum_axe",
            () -> new AxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.PLATINUM,5.0f,-3f))));
    public static final DeferredItem<HoeItem> PLATINUM_HOE = ITEMS.register("platinum_hoe",
            () -> new HoeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.PLATINUM,-3f,-0f))));

    public static final DeferredItem<ArmorItem> PLATINUM_HELMET = ITEMS.register("platinum_helmet",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));
    public static final DeferredItem<ArmorItem> PLATINUM_CHESTPLATE = ITEMS.register("platinum_chestplate",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));
    public static final DeferredItem<ArmorItem> PLATINUM_LEGGINGS = ITEMS.register("platinum_leggings",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(20))));
    public static final DeferredItem<ArmorItem> PLATINUM_BOOTS = ITEMS.register("platinum_boots",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(20))));


    // COBALT
    public static final DeferredItem<Item> RAW_COBALT = ITEMS.register("raw_cobalt",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.register("cobalt_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COBALT_NUGGET = ITEMS.register("cobalt_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> COBALT_SWORD = ITEMS.register("cobalt_sword",
            () -> new SwordItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.PLATINUM,3.0f,-2.4f))));
    public static final DeferredItem<PickaxeItem> COBALT_PICKAXE = ITEMS.register("cobalt_pickaxe",
            () -> new PickaxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.PLATINUM,1.0f,-2.8f))));
    public static final DeferredItem<ShovelItem> COBALT_SHOVEL = ITEMS.register("cobalt_shovel",
            () -> new ShovelItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.PLATINUM,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> COBALT_AXE = ITEMS.register("cobalt_axe",
            () -> new AxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.PLATINUM,5.0f,-3f))));
    public static final DeferredItem<HoeItem> COBALT_HOE = ITEMS.register("cobalt_hoe",
            () -> new HoeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.PLATINUM,-3f,-0f))));

    public static final DeferredItem<ArmorItem> COBALT_HELMET = ITEMS.register("cobalt_helmet",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));
    public static final DeferredItem<ArmorItem> COBALT_CHESTPLATE = ITEMS.register("cobalt_chestplate",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));
    public static final DeferredItem<ArmorItem> COBALT_LEGGINGS = ITEMS.register("cobalt_leggings",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(20))));
    public static final DeferredItem<ArmorItem> COBALT_BOOTS = ITEMS.register("cobalt_boots",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(20))));



    // TUNGSTEN
    public static final DeferredItem<Item> RAW_TUNGSTEN = ITEMS.register("raw_tungsten",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TUNGSTEN_INGOT = ITEMS.register("tungsten_ingot",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TUNGSTEN_NUGGET = ITEMS.register("tungsten_nugget",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> TUNGSTEN_SWORD = ITEMS.register("tungsten_sword",
            () -> new SwordItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.PLATINUM,3.0f,-2.4f))));
    public static final DeferredItem<PickaxeItem> TUNGSTEN_PICKAXE = ITEMS.register("tungsten_pickaxe",
            () -> new PickaxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(ModToolTiers.PLATINUM,1.0f,-2.8f))));
    public static final DeferredItem<ShovelItem> TUNGSTEN_SHOVEL = ITEMS.register("tungsten_shovel",
            () -> new ShovelItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(ShovelItem.createAttributes(ModToolTiers.PLATINUM,1.5f,-3.0f))));
    public static final DeferredItem<AxeItem> TUNGSTEN_AXE = ITEMS.register("tungsten_axe",
            () -> new AxeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(AxeItem.createAttributes(ModToolTiers.PLATINUM,5.0f,-3f))));
    public static final DeferredItem<HoeItem> TUNGSTEN_HOE = ITEMS.register("tungsten_hoe",
            () -> new HoeItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(HoeItem.createAttributes(ModToolTiers.PLATINUM,-3f,-0f))));
    public static final DeferredItem<SwordItem> TUNGSTEN_FLAIL = ITEMS.register("tungsten_flail",
            () -> new SwordItem(ModToolTiers.PLATINUM, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModToolTiers.PLATINUM,8.5f,-3.5f))));

    public static final DeferredItem<ArmorItem> TUNGSTEN_HELMET = ITEMS.register("tungsten_helmet",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));
    public static final DeferredItem<ArmorItem> TUNGSTEN_CHESTPLATE = ITEMS.register("tungsten_chestplate",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));
    public static final DeferredItem<ArmorItem> TUNGSTEN_LEGGINGS = ITEMS.register("tungsten_leggings",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(20))));
    public static final DeferredItem<ArmorItem> TUNGSTEN_BOOTS = ITEMS.register("tungsten_boots",
            () -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(20))));

    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> OSMIUM_INGOT = ITEMS.register("osmium_ingot", () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> LEAD_SWORD = ITEMS.register("lead_sword",
            () -> new SwordItem(ModToolTiers.LEAD, new Item.Properties()));
    public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = ITEMS.register("lead_pickaxe",
            () -> new PickaxeItem(ModToolTiers.LEAD, new Item.Properties()));
    public static final DeferredItem<AxeItem> LEAD_AXE = ITEMS.register("lead_axe",
            () -> new AxeItem(ModToolTiers.LEAD, new Item.Properties()));
    public static final DeferredItem<ShovelItem> LEAD_SHOVEL = ITEMS.register("lead_shovel",
            () -> new ShovelItem(ModToolTiers.LEAD, new Item.Properties()));
    public static final DeferredItem<HoeItem> LEAD_HOE = ITEMS.register("lead_hoe",
            () -> new HoeItem(ModToolTiers.LEAD, new Item.Properties()));


    // ZIRCON
    public static final DeferredItem<Item> ZIRCON = ITEMS.register("zircon",
            () -> new Item(new Item.Properties()));

    // TIGER
    public static final DeferredItem<Item> TIGER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.TIGER, 0xed7211,0x000000,
                    new Item.Properties()));

    public static final DeferredItem<Item> TIGER_ARMOR = ITEMS.register("tiger_armor",
            () -> new ModAnimalArmourItem(ModArmourMaterials.CROC, ModAnimalArmourItem.BodyType.PANTHERINE, true, new Item.Properties().durability(ArmorItem.Type.BODY.getDurability(4))));


    //SNOW LEOPARD
    public static final DeferredItem<Item> SNOW_LEOPARD_SPAWN_EGG = ITEMS.register("snow_leopard_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SNOW_LEOPARD, 0xa29d95,0x000000,
                    new Item.Properties()));

    public static final DeferredItem<Item> SNOW_LEOPARD_ARMOR = ITEMS.register("snow_leopard_armor",
            () -> new ModAnimalArmourItem(ModArmourMaterials.PANGOLIN, ModAnimalArmourItem.BodyType.FELINE, true, new Item.Properties().durability(ArmorItem.Type.BODY.getDurability(4))));

    // RACCOON
    public static final DeferredItem<Item> RACCOON_SPAWN_EGG = ITEMS.register("raccoon_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RACCOON, 0x808080,0x000000,
                    new Item.Properties()));

    // EARTHWORM
    public static final DeferredItem<Item> EARTHWORM_SPAWN_EGG = ITEMS.register("earthworm_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.EARTHWORM, 0xf18686,0xb76767,
                    new Item.Properties()));

    // PANGOLIN
    public static final DeferredItem<Item> PANGOLIN_SPAWN_EGG = ITEMS.register("pangolin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PANGOLIN, 0x493a26,0x342716,
                    new Item.Properties()));

    public static final DeferredItem<Item> PANGOLIN_SCALE = ITEMS.register("pangolin_scale",
            () -> new Item(new Item.Properties()));


    // DOMESTIC GOAT
    public static final DeferredItem<Item> DOMESTIC_GOAT_SPAWN_EGG = ITEMS.register("domestic_goat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.DOMESTIC_GOAT, 0x8b6a4f,0xf5f5dc,
                    new Item.Properties()));

    // SEAGULL
    public static final DeferredItem<Item> SEAGULL_SPAWN_EGG = ITEMS.register("seagull_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SEAGULL, 0xcccccc,0x000000,
                    new Item.Properties()));

    // PASSERINE SPAWN EGGS
    public static final DeferredItem<Item> BLACKCAP_SPAWN_EGG = ITEMS.register("blackcap_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.BLACKCAP, 0x4a423e, 0x1a1a1a,
                    new Item.Properties()));

    public static final DeferredItem<Item> BLUETHROAT_SPAWN_EGG = ITEMS.register("bluethroat_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.BLUETHROAT, 0x1a56eb, 0x1a56eb,
                    new Item.Properties()));

    public static final DeferredItem<Item> BULLFINCH_SPAWN_EGG = ITEMS.register("bullfinch_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.BULLFINCH, 0x9e1f1f, 0x232323,
                    new Item.Properties()));

    public static final DeferredItem<Item> CANARY_SPAWN_EGG = ITEMS.register("canary_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.CANARY, 0xd8bc12, 0xd8bc12,
                    new Item.Properties()));

    public static final DeferredItem<Item> CARDINAL_SPAWN_EGG = ITEMS.register("cardinal_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.CARDINAL, 0xd52525, 0xd52525,
                    new Item.Properties()));

    public static final DeferredItem<Item> CHAFFINCH_SPAWN_EGG = ITEMS.register("chaffinch_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.CHAFFINCH, 0xc2763e, 0x506273,
                    new Item.Properties()));

    public static final DeferredItem<Item> GOLDCREST_SPAWN_EGG = ITEMS.register("goldcrest_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.GOLDCREST, 0x09161c, 0xf7f320,
                    new Item.Properties()));

    public static final DeferredItem<Item> GOLDFINCH_SPAWN_EGG = ITEMS.register("goldfinch_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.GOLDFINCH, 0xa87552, 0xd52525,
                    new Item.Properties()));

    public static final DeferredItem<Item> NIGHTINGALE_SPAWN_EGG = ITEMS.register("nightingale_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.NIGHTINGALE, 0x6c575c, 0xb5bcd9,
                    new Item.Properties()));

    public static final DeferredItem<Item> REDSTART_SPAWN_EGG = ITEMS.register("redstart_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.REDSTART, 0x7e8097, 0xbf7641,
                    new Item.Properties()));

    public static final DeferredItem<Item> REEDLING_SPAWN_EGG = ITEMS.register("reedling_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.REEDLING, 0xb37222, 0xa6a6a6,
                    new Item.Properties()));

    public static final DeferredItem<Item> ROBIN_SPAWN_EGG = ITEMS.register("robin_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.ROBIN, 0x927764, 0xd57f40,
                    new Item.Properties()));

    public static final DeferredItem<Item> SISKIN_SPAWN_EGG = ITEMS.register("siskin_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.SISKIN, 0x151515, 0xd8bc12,
                    new Item.Properties()));

    public static final DeferredItem<Item> SKYLARK_SPAWN_EGG = ITEMS.register("skylark_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.SKYLARK, 0x986c4c, 0xd7c6ac,
                    new Item.Properties()));

    public static final DeferredItem<Item> SPARROW_SPAWN_EGG = ITEMS.register("sparrow_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.SPARROW, 0x732f1a, 0xd4c5b2,
                    new Item.Properties()));

    public static final DeferredItem<Item> TIT_SPAWN_EGG = ITEMS.register("tit_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.TIT, 0x1b4172, 0xd6960c,
                    new Item.Properties()));

    public static final DeferredItem<Item> WAGTAIL_SPAWN_EGG = ITEMS.register("wagtail_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.WAGTAIL, 0x181b2c, 0xeff1ff,
                    new Item.Properties()));

    public static final DeferredItem<Item> WAXWING_SPAWN_EGG = ITEMS.register("waxwing_spawn_egg",
            () ->new DeferredSpawnEggItem(ModEntities.WAXWING, 0x806c65, 0x542c24,
                    new Item.Properties()));

    // ZEBRA, ZORSE and ZONKEY
    public static final DeferredItem<Item> ZEBRA_SPAWN_EGG = ITEMS.register("zebra_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ZEBRA, 0x000000,0xffffff,
                    new Item.Properties()));

    public static final DeferredItem<Item> ZORSE_SPAWN_EGG = ITEMS.register("zorse_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ZORSE, 0x191919,0xbcab9d,
                    new Item.Properties()));

    public static final DeferredItem<Item> ZONKEY_SPAWN_EGG = ITEMS.register("zonkey_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ZONKEY, 0x1e1e1e,0x806e5e,
                    new Item.Properties()));

    // ELEPHANT
    public static final DeferredItem<Item> ELEPHANT_SPAWN_EGG = ITEMS.register("elephant_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ELEPHANT, 0x755d53,0xd1bfb7,
                    new Item.Properties()));

    // GIRAFFE
    public static final DeferredItem<Item> GIRAFFE_SPAWN_EGG = ITEMS.register("giraffe_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GIRAFFE, 0xf1e0cd, 0x8d581d,
                    new Item.Properties()));

    // BROWN BEAR
    public static final DeferredItem<Item> BROWN_BEAR_SPAWN_EGG = ITEMS.register("brown_bear_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BROWN_BEAR, 0x564740, 0x564740,
                    new Item.Properties()));

    // MANTIS
    public static final DeferredItem<Item> MANTIS_SPAWN_EGG = ITEMS.register("mantis_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MANTIS, 0x4da82d, 0x9dd08b,
                    new Item.Properties()));

    // PENGUIN
    public static final DeferredItem<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PENGUIN, 0x1f1f1f, 0xdaad5f,
                new Item.Properties()));

    // EAGLE
    public static final DeferredItem<Item> EAGLE_SPAWN_EGG = ITEMS.register("eagle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.EAGLE, 0x584836, 0xd6c0a4,
                    new Item.Properties()));

    // EAGLE
    public static final DeferredItem<Item> BACTRIAN_CAMEL_SPAWN_EGG = ITEMS.register("bactrian_camel_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BACTRIAN_CAMEL, 0x964e1b, 0x7f7f7f,
                    new Item.Properties()));

    // BELUGA STURGEON

    public static final DeferredItem<Item> BELUGA_STURGEON_CAVIAR = ITEMS.register("beluga_sturgeon_caviar",
            () -> new Item((new Item.Properties()).food(ModFoodProperties.BELUGA_STURGEON_CAVIAR)));

    public static final DeferredItem<Item> BELUGA_STURGEON_SPAWN_EGG = ITEMS.register("beluga_sturgeon_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BELUGA_STURGEON, 0x645e57, 0xa0988e,
                    new Item.Properties()));

    public static final DeferredItem<Item> BELUGA_STURGEON_BUCKET = ITEMS.register("beluga_sturgeon_bucket",
            () -> new MobBucketItem(ModEntities.BELUGA_STURGEON.get(), Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, (new Item.Properties())
                    .stacksTo(1)
                    .component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));

    // STOAT
    public static final DeferredItem<Item> STOAT_SPAWN_EGG = ITEMS.register("stoat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.STOAT, 0x645e57, 0xa0988e,
                    new Item.Properties()));

    // PHEASANT
    public static final DeferredItem<Item> PHEASANT_SPAWN_EGG = ITEMS.register("pheasant_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PHEASANT, 0x645e57, 0xa0988e,
                    new Item.Properties()));

    // SAOLA
    public static final DeferredItem<Item> SAOLA_SPAWN_EGG = ITEMS.register("saola_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SAOLA, 0x5d4125, 0xc7c7c7,
                    new Item.Properties()));

    // CHIMP
    public static final DeferredItem<Item> CHIMPANZEE_SPAWN_EGG = ITEMS.register("chimpanzee_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CHIMPANZEE, 0x362f28, 0xc4a88a,
                    new Item.Properties()));

    // ANACONDA

    public static final DeferredItem<Item> ANACONDA_SPAWN_EGG = ITEMS.register("anaconda_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ANACONDA, 0x38432a, 0x8f4732,
                    new Item.Properties()));

    // CICADA

    public static final DeferredItem<Item> CICADA_SPAWN_EGG = ITEMS.register("cicada_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CICADA, 0x4c2b1c, 0x232121,
                    new Item.Properties()));

    // HARE

    public static final DeferredItem<Item> HARE_SPAWN_EGG = ITEMS.register("hare_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HARE, 0x7e6a58, 0x3b3633,
                    new Item.Properties()));

    // FISHER

    public static final DeferredItem<Item> FISHER_SPAWN_EGG = ITEMS.register("fisher_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.FISHER, 0x564b41, 0x3a352f,
                    new Item.Properties()));

    // ROADRUNNER

    public static final DeferredItem<Item> ROADRUNNER_SPAWN_EGG = ITEMS.register("roadrunner_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ROADRUNNER, 0x574232, 0x4e80cc,
                    new Item.Properties()));

    // RUFF

    public static final DeferredItem<Item> RUFF_SPAWN_EGG = ITEMS.register("ruff_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RUFF, 0x907165, 0xbb8383,
                    new Item.Properties()));

    // DEER

    public static final DeferredItem<Item> DEER_SPAWN_EGG = ITEMS.register("deer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.DEER, 0xa56a51, 0xdab19f,
                    new Item.Properties()));

    // ROE DEER

    public static final DeferredItem<Item> ROE_DEER_SPAWN_EGG = ITEMS.register("roe_deer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ROE_DEER, 0x866851, 0xeeeeee,
                    new Item.Properties()));

    // HOATZIN

    public static final DeferredItem<Item> HOATZIN_SPAWN_EGG = ITEMS.register("hoatzin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HOATZIN, 0x844c2b, 0x3cc6d5,
                    new Item.Properties()));

    // CROCODILE

    public static final DeferredItem<Item> CROCODILE_SCALE = ITEMS.register("crocodile_scale",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CROCODILE_SPAWN_EGG = ITEMS.register("crocodile_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CROCODILE, 0x566c43, 0x425333,
                    new Item.Properties()));

    // HIPPO

    public static final DeferredItem<Item> HIPPOPOTAMUS_SPAWN_EGG = ITEMS.register("hippopotamus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HIPPOPOTAMUS, 0x44413f, 0x6b5855,
                    new Item.Properties()));

    // SHARK

    public static final DeferredItem<Item> WHITE_SHARK_SPAWN_EGG = ITEMS.register("white_shark_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.WHITE_SHARK, 0x868686, 0xf3f3f3,
                    new Item.Properties()));

    // LION

    public static final DeferredItem<Item> LION_SPAWN_EGG = ITEMS.register("lion_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.LION, 0xc0a97a, 0x514733,
                    new Item.Properties()));

    // PHILIPPINE EAGLE

    public static final DeferredItem<Item> PHILIPPINE_EAGLE_SPAWN_EGG = ITEMS.register("philippine_eagle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.PHILIPPINE_EAGLE, 0x4a443e, 0x43474e,
                    new Item.Properties()));

    // MONKEY

    public static final DeferredItem<Item> MONKEY_SPAWN_EGG = ITEMS.register("monkey_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MONKEY, 0x8b5c3e, 0x69737e,
                    new Item.Properties()));

    // CRYODRAKON and other extinct critters

    public static final DeferredItem<Item> CRYODRAKON_SPAWN_EGG = ITEMS.register("cryodrakon_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CRYODRAKON, 0xf7f4ef, 0x8d3824,
                    new Item.Properties()));

    public static final DeferredItem<Item> VELOCIRAPTOR_SPAWN_EGG = ITEMS.register("velociraptor_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.VELOCIRAPTOR, 0xac9780, 0x924c18,
                    new Item.Properties()));

    public static final DeferredItem<Item> HYPNOVENATOR_SPAWN_EGG = ITEMS.register("hypnovenator_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HYPNOVENATOR, 0x6c7b68, 0xa6b2bc,
                    new Item.Properties()));

    public static final DeferredItem<Item> GALLIMIMUS_SPAWN_EGG = ITEMS.register("gallimimus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GALLIMIMUS, 0x65523e, 0xc5bdb0,
                    new Item.Properties()));

    public static final DeferredItem<Item> DIPLODOCUS_SPAWN_EGG = ITEMS.register("diplodocus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.DIPLODOCUS, 0xd09963, 0xa38569,
                    new Item.Properties()));

    public static final DeferredItem<Item> EROMANGASAURUS_SPAWN_EGG = ITEMS.register("eromangasaurus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.EROMANGASAURUS, 0x3B5F4C, 0x1C3D2A,
                    new Item.Properties()));

    public static final DeferredItem<Item> BEIPIAOSAURUS_SPAWN_EGG = ITEMS.register("beipiaosaurus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.BEIPIAOSAURUS, 0x161616, 0x777777,
                    new Item.Properties()));

    public static final DeferredItem<Item> DEARC_SPAWN_EGG = ITEMS.register("dearc_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.DEARC, 0xe4e4e4, 0xd2a475,
                    new Item.Properties()));

    // FOODSTUFFS
    public static final DeferredItem<Item> RAW_CHEVON = ITEMS.register("raw_chevon",
            () -> new Item((new Item.Properties()).food(Foods.MUTTON)));

    public static final DeferredItem<Item> COOKED_CHEVON = ITEMS.register("cooked_chevon",
            () -> new Item((new Item.Properties()).food(Foods.COOKED_MUTTON)));

    public static final DeferredItem<Item> VODKA = ITEMS.register("vodka",
            () -> new Item((new Item.Properties()).food((ModFoodProperties.VODKA))));

    public static final DeferredItem<Item> RAW_PHEASANT = ITEMS.register("raw_pheasant",
            () -> new Item((new Item.Properties()).food(Foods.CHICKEN)));

    public static final DeferredItem<Item> COOKED_PHEASANT = ITEMS.register("cooked_pheasant",
            () -> new Item((new Item.Properties()).food(Foods.COOKED_CHICKEN)));

    // DART GUN AND DARTS
    public static final DeferredItem<Item> DART_GUN = ITEMS.register("dart_gun",
            () -> new DartGunItem(new Item.Properties().stacksTo(1).durability(1000)));

    public static final DeferredItem<Item> TRANQ_DART = ITEMS.register("dart",
            () -> new Item(new Item.Properties()));

    // BOATS
    public static final DeferredItem<ModBoatItem> WILLOW_BOAT = ITEMS.register("willow_boat",
            () -> new ModBoatItem(false, ModBoat.Type.WILLOW, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> WILLOW_CHEST_BOAT = ITEMS.register("willow_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.WILLOW, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> HORNBEAM_BOAT = ITEMS.register("hornbeam_boat",
            () -> new ModBoatItem(false, ModBoat.Type.HORNBEAM, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> HORNBEAM_CHEST_BOAT = ITEMS.register("hornbeam_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.HORNBEAM, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> LINDEN_BOAT = ITEMS.register("linden_boat",
            () -> new ModBoatItem(false, ModBoat.Type.LINDEN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> LINDEN_CHEST_BOAT = ITEMS.register("linden_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.LINDEN, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> ASPEN_BOAT = ITEMS.register("aspen_boat",
            () -> new ModBoatItem(false, ModBoat.Type.ASPEN, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> ASPEN_CHEST_BOAT = ITEMS.register("aspen_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.ASPEN, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> SYCAMORE_BOAT = ITEMS.register("sycamore_boat",
            () -> new ModBoatItem(false, ModBoat.Type.SYCAMORE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> SYCAMORE_CHEST_BOAT = ITEMS.register("sycamore_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.SYCAMORE, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> BAOBAB_BOAT = ITEMS.register("baobab_boat",
            () -> new ModBoatItem(false, ModBoat.Type.BAOBAB, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> BAOBAB_CHEST_BOAT = ITEMS.register("baobab_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.BAOBAB, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> GINKGO_BOAT = ITEMS.register("ginkgo_boat",
            () -> new ModBoatItem(false, ModBoat.Type.GINKGO, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> GINKGO_CHEST_BOAT = ITEMS.register("ginkgo_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.GINKGO, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> POPLAR_BOAT = ITEMS.register("poplar_boat",
            () -> new ModBoatItem(false, ModBoat.Type.POPLAR, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> POPLAR_CHEST_BOAT = ITEMS.register("poplar_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.POPLAR, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> MAPLE_BOAT = ITEMS.register("maple_boat",
            () -> new ModBoatItem(false, ModBoat.Type.MAPLE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> MAPLE_CHEST_BOAT = ITEMS.register("maple_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.MAPLE, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> PALM_BOAT = ITEMS.register("palm_boat",
            () -> new ModBoatItem(false, ModBoat.Type.PALM, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> PALM_CHEST_BOAT = ITEMS.register("palm_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.PALM, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> BEECH_BOAT = ITEMS.register("beech_boat",
            () -> new ModBoatItem(false, ModBoat.Type.BEECH, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> BEECH_CHEST_BOAT = ITEMS.register("beech_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.BEECH, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> ASH_BOAT = ITEMS.register("ash_boat",
            () -> new ModBoatItem(false, ModBoat.Type.ASH, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> ASH_CHEST_BOAT = ITEMS.register("ash_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.ASH, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> EUCALYPTUS_BOAT = ITEMS.register("eucalyptus_boat",
            () -> new ModBoatItem(false, ModBoat.Type.EUCALYPTUS, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> EUCALYPTUS_CHEST_BOAT = ITEMS.register("eucalyptus_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.EUCALYPTUS, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> REDWOOD_BOAT = ITEMS.register("redwood_boat",
            () -> new ModBoatItem(false, ModBoat.Type.REDWOOD, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> REDWOOD_CHEST_BOAT = ITEMS.register("redwood_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.REDWOOD, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> MONKEY_PUZZLE_BOAT = ITEMS.register("monkey_puzzle_boat",
            () -> new ModBoatItem(false, ModBoat.Type.MONKEY_PUZZLE, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> MONKEY_PUZZLE_CHEST_BOAT = ITEMS.register("monkey_puzzle_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.MONKEY_PUZZLE, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<ModBoatItem> YEW_BOAT = ITEMS.register("yew_boat",
            () -> new ModBoatItem(false, ModBoat.Type.YEW, new Item.Properties().stacksTo(1)));
    public static final DeferredItem<ModBoatItem> YEW_CHEST_BOAT = ITEMS.register("yew_chest_boat",
            () -> new ModBoatItem(true, ModBoat.Type.YEW, new Item.Properties().stacksTo(1)));

    // FOSSILS, PALAEONTOLOGY AND REVIVAL
    public static final DeferredItem<Item> FOSSIL = ITEMS.register("fossil",
            () -> new Item(new Item.Properties()));

    // MISC
    public static final DeferredItem<Item> SPIKED_HEAVY_CORE = ITEMS.register("spiked_heavy_core",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> OBSIDIAN_SHARD = ITEMS.register("obsidian_shard",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BANANA = ITEMS.register("banana",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BANANA)));

    public static final DeferredItem<Item> SAOLA_HORN = ITEMS.register("saola_horn",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_SHRIMP = ITEMS.register("raw_shrimp",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RAW_SHRIMP)));

    public static final DeferredItem<Item> IVORY = ITEMS.register("ivory",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BESTIARY = ITEMS.register("bestiary",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CHAKRAM = ITEMS.register("chakram",
            () -> new ChakramItem(new Item.Properties().stacksTo(16)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
