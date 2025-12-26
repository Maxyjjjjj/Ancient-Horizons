package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.registry.ModBlocks;
import com.fungoussoup.ancienthorizons.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;


public class ModItemModelProvider extends ItemModelProvider {
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }



    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AncientHorizons.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // TIMESTONE
        basicItem(ModItems.RAW_TIME_STONE.get());
        basicItem(ModItems.TIME_STONE.get());

        // ALUMINIUM
        basicItem(ModItems.RAW_ALUMINIUM.get());
        basicItem(ModItems.ALUMINIUM_INGOT.get());
        basicItem(ModItems.ALUMINIUM_NUGGET.get());

        handheldItem(ModItems.ALUMINIUM_SWORD);
        handheldItem(ModItems.ALUMINIUM_PICKAXE);
        handheldItem(ModItems.ALUMINIUM_AXE);
        handheldItem(ModItems.ALUMINIUM_SHOVEL);
        handheldItem(ModItems.ALUMINIUM_HOE);
        handheldItem(ModItems.ALUMINIUM_HAMMER);

        trimmedArmorItem(ModItems.ALUMINIUM_HELMET);
        trimmedArmorItem(ModItems.ALUMINIUM_CHESTPLATE);
        trimmedArmorItem(ModItems.ALUMINIUM_LEGGINGS);
        trimmedArmorItem(ModItems.ALUMINIUM_BOOTS);

        // TIN
        basicItem(ModItems.TIN_INGOT.get());
        basicItem(ModItems.TIN_NUGGET.get());
        basicItem(ModItems.RAW_TIN.get());

        basicItem(ModBlocks.TIN_DOOR.asItem());

        // BRONZE
        basicItem(ModItems.BRONZE_ALLOY.get());
        basicItem(ModItems.BRONZE_NUGGET.get());

        handheldItem(ModItems.BRONZE_SWORD);
        handheldItem(ModItems.BRONZE_PICKAXE);
        handheldItem(ModItems.BRONZE_AXE);
        handheldItem(ModItems.BRONZE_SHOVEL);
        handheldItem(ModItems.BRONZE_HOE);

        trimmedArmorItem(ModItems.BRONZE_HELMET);
        trimmedArmorItem(ModItems.BRONZE_CHESTPLATE);
        trimmedArmorItem(ModItems.BRONZE_LEGGINGS);
        trimmedArmorItem(ModItems.BRONZE_BOOTS);

        // SILVER
        basicItem(ModItems.SILVER_INGOT.get());
        basicItem(ModItems.SILVER_NUGGET.get());
        basicItem(ModItems.RAW_SILVER.get());

        handheldItem(ModItems.SILVER_SWORD);
        handheldItem(ModItems.SILVER_PICKAXE);
        handheldItem(ModItems.SILVER_AXE);
        handheldItem(ModItems.SILVER_SHOVEL);
        handheldItem(ModItems.SILVER_HOE);
        handheldItem(ModItems.SILVER_KATANA);

        trimmedArmorItem(ModItems.SILVER_HELMET);
        trimmedArmorItem(ModItems.SILVER_CHESTPLATE);
        trimmedArmorItem(ModItems.SILVER_LEGGINGS);
        trimmedArmorItem(ModItems.SILVER_BOOTS);

        // PLATINUM
        basicItem(ModItems.PLATINUM_INGOT.get());
        basicItem(ModItems.PLATINUM_NUGGET.get());
        basicItem(ModItems.RAW_PLATINUM.get());

        handheldItem(ModItems.PLATINUM_SWORD);
        handheldItem(ModItems.PLATINUM_PICKAXE);
        handheldItem(ModItems.PLATINUM_AXE);
        handheldItem(ModItems.PLATINUM_SHOVEL);
        handheldItem(ModItems.PLATINUM_HOE);

        trimmedArmorItem(ModItems.PLATINUM_HELMET);
        trimmedArmorItem(ModItems.PLATINUM_CHESTPLATE);
        trimmedArmorItem(ModItems.PLATINUM_LEGGINGS);
        trimmedArmorItem(ModItems.PLATINUM_BOOTS);

        // COBALT
        basicItem(ModItems.COBALT_INGOT.get());
        basicItem(ModItems.COBALT_NUGGET.get());
        basicItem(ModItems.RAW_COBALT.get());

        handheldItem(ModItems.COBALT_SWORD);
        handheldItem(ModItems.COBALT_PICKAXE);
        handheldItem(ModItems.COBALT_AXE);
        handheldItem(ModItems.COBALT_SHOVEL);
        handheldItem(ModItems.COBALT_HOE);

        trimmedArmorItem(ModItems.COBALT_HELMET);
        trimmedArmorItem(ModItems.COBALT_CHESTPLATE);
        trimmedArmorItem(ModItems.COBALT_LEGGINGS);
        trimmedArmorItem(ModItems.COBALT_BOOTS);

        // TUNGSTEN
        basicItem(ModItems.TUNGSTEN_INGOT.get());
        basicItem(ModItems.TUNGSTEN_NUGGET.get());
        basicItem(ModItems.RAW_TUNGSTEN.get());

        handheldItem(ModItems.TUNGSTEN_SWORD);
        handheldItem(ModItems.TUNGSTEN_PICKAXE);
        handheldItem(ModItems.TUNGSTEN_AXE);
        handheldItem(ModItems.TUNGSTEN_SHOVEL);
        handheldItem(ModItems.TUNGSTEN_HOE);
        handheldItem(ModItems.TUNGSTEN_FLAIL);

        trimmedArmorItem(ModItems.TUNGSTEN_HELMET);
        trimmedArmorItem(ModItems.TUNGSTEN_CHESTPLATE);
        trimmedArmorItem(ModItems.TUNGSTEN_LEGGINGS);
        trimmedArmorItem(ModItems.TUNGSTEN_BOOTS);

        basicItem(ModBlocks.TUNGSTEN_DOOR.asItem());

        // LEAD
        basicItem(ModItems.RAW_LEAD.get());
        basicItem(ModItems.LEAD_INGOT.get());
        handheldItem(ModItems.LEAD_SWORD);
        handheldItem(ModItems.LEAD_PICKAXE);
        handheldItem(ModItems.LEAD_AXE);
        handheldItem(ModItems.LEAD_SHOVEL);
        handheldItem(ModItems.LEAD_HOE);

        // NICKEL & OSMIUM
        basicItem(ModItems.RAW_NICKEL.get());
        basicItem(ModItems.NICKEL_INGOT.get());
        trimmedArmorItem(ModItems.NICKEL_HELMET);
        trimmedArmorItem(ModItems.NICKEL_CHESTPLATE);
        trimmedArmorItem(ModItems.NICKEL_LEGGINGS);
        trimmedArmorItem(ModItems.NICKEL_BOOTS);
        handheldItem(ModItems.NICKEL_SWORD);
        handheldItem(ModItems.NICKEL_PICKAXE);
        handheldItem(ModItems.NICKEL_AXE);
        handheldItem(ModItems.NICKEL_SHOVEL);
        handheldItem(ModItems.NICKEL_HOE);
        basicItem(ModItems.RAW_OSMIUM.get());
        basicItem(ModItems.OSMIUM_INGOT.get());
        trimmedArmorItem(ModItems.OSMIUM_HELMET);
        trimmedArmorItem(ModItems.OSMIUM_CHESTPLATE);
        trimmedArmorItem(ModItems.OSMIUM_LEGGINGS);
        trimmedArmorItem(ModItems.OSMIUM_BOOTS);
        handheldItem(ModItems.OSMIUM_SWORD);
        handheldItem(ModItems.OSMIUM_PICKAXE);
        handheldItem(ModItems.OSMIUM_AXE);
        handheldItem(ModItems.OSMIUM_SHOVEL);
        handheldItem(ModItems.OSMIUM_HOE);

        // ZIRCON
        basicItem(ModItems.ZIRCON.get());

        // WILLOW
        saplingItem(ModBlocks.WILLOW_SAPLING);
        buttonItem(ModBlocks.WILLOW_BUTTON, ModBlocks.WILLOW_PLANKS);
        fenceItem(ModBlocks.WILLOW_FENCE, ModBlocks.WILLOW_PLANKS);
        basicItem(ModBlocks.WILLOW_DOOR.asItem());

        // HORNBEAM
        saplingItem(ModBlocks.HORNBEAM_SAPLING);
        buttonItem(ModBlocks.HORNBEAM_BUTTON, ModBlocks.HORNBEAM_PLANKS);
        fenceItem(ModBlocks.HORNBEAM_FENCE, ModBlocks.HORNBEAM_PLANKS);
        basicItem(ModBlocks.HORNBEAM_DOOR.asItem());

        // LINDEN
        saplingItem(ModBlocks.LINDEN_SAPLING);
        buttonItem(ModBlocks.LINDEN_BUTTON, ModBlocks.LINDEN_PLANKS);
        fenceItem(ModBlocks.LINDEN_FENCE, ModBlocks.LINDEN_PLANKS);
        basicItem(ModBlocks.LINDEN_DOOR.asItem());

        // GINKGO
        saplingItem(ModBlocks.GINKGO_SAPLING);
        buttonItem(ModBlocks.GINKGO_BUTTON, ModBlocks.GINKGO_PLANKS);
        fenceItem(ModBlocks.GINKGO_FENCE, ModBlocks.GINKGO_PLANKS);
        basicItem(ModBlocks.GINKGO_DOOR.asItem());

        // POPLAR
        saplingItem(ModBlocks.POPLAR_SAPLING);
        buttonItem(ModBlocks.POPLAR_BUTTON, ModBlocks.POPLAR_PLANKS);
        fenceItem(ModBlocks.POPLAR_FENCE, ModBlocks.POPLAR_PLANKS);
        basicItem(ModBlocks.POPLAR_DOOR.asItem());

        // MAPLE
        saplingItem(ModBlocks.MAPLE_SAPLING);
        buttonItem(ModBlocks.MAPLE_BUTTON, ModBlocks.MAPLE_PLANKS);
        fenceItem(ModBlocks.MAPLE_FENCE, ModBlocks.MAPLE_PLANKS);
        basicItem(ModBlocks.MAPLE_DOOR.asItem());

        // BAOBAB
        saplingItem(ModBlocks.BAOBAB_SAPLING);
        buttonItem(ModBlocks.BAOBAB_BUTTON, ModBlocks.BAOBAB_PLANKS);
        fenceItem(ModBlocks.BAOBAB_FENCE, ModBlocks.BAOBAB_PLANKS);
        basicItem(ModBlocks.BAOBAB_DOOR.asItem());

        // PALM
        saplingItem(ModBlocks.PALM_SAPLING);
        buttonItem(ModBlocks.PALM_BUTTON, ModBlocks.PALM_PLANKS);
        fenceItem(ModBlocks.PALM_FENCE, ModBlocks.PALM_PLANKS);
        basicItem(ModBlocks.PALM_DOOR.asItem());

        // ASPEN
        saplingItem(ModBlocks.ASPEN_SAPLING);
        buttonItem(ModBlocks.ASPEN_BUTTON, ModBlocks.ASPEN_PLANKS);
        fenceItem(ModBlocks.ASPEN_FENCE, ModBlocks.ASPEN_PLANKS);
        basicItem(ModBlocks.ASPEN_DOOR.asItem());

        // ASH
        saplingItem(ModBlocks.ASH_SAPLING);
        buttonItem(ModBlocks.ASH_BUTTON, ModBlocks.ASH_PLANKS);
        fenceItem(ModBlocks.ASH_FENCE, ModBlocks.ASH_PLANKS);
        basicItem(ModBlocks.ASH_DOOR.asItem());

        // BEECH
        saplingItem(ModBlocks.BEECH_SAPLING);
        buttonItem(ModBlocks.BEECH_BUTTON, ModBlocks.BEECH_PLANKS);
        fenceItem(ModBlocks.BEECH_FENCE, ModBlocks.BEECH_PLANKS);
        basicItem(ModBlocks.BEECH_DOOR.asItem());

        // EUCALYPTUS
        saplingItem(ModBlocks.EUCALYPTUS_SAPLING);
        buttonItem(ModBlocks.EUCALYPTUS_BUTTON, ModBlocks.EUCALYPTUS_PLANKS);
        fenceItem(ModBlocks.EUCALYPTUS_FENCE, ModBlocks.EUCALYPTUS_PLANKS);
        basicItem(ModBlocks.EUCALYPTUS_DOOR.asItem());

        // SYCAMORE
        saplingItem(ModBlocks.SYCAMORE_SAPLING);
        buttonItem(ModBlocks.SYCAMORE_BUTTON, ModBlocks.SYCAMORE_PLANKS);
        fenceItem(ModBlocks.SYCAMORE_FENCE, ModBlocks.SYCAMORE_PLANKS);
        basicItem(ModBlocks.SYCAMORE_DOOR.asItem());

        // REDWOOD
        saplingItem(ModBlocks.REDWOOD_SAPLING);
        buttonItem(ModBlocks.REDWOOD_BUTTON, ModBlocks.REDWOOD_PLANKS);
        fenceItem(ModBlocks.REDWOOD_FENCE, ModBlocks.REDWOOD_PLANKS);
        basicItem(ModBlocks.REDWOOD_DOOR.asItem());

        // MONKEY_PUZZLE
        saplingItem(ModBlocks.MONKEY_PUZZLE_SAPLING);
        buttonItem(ModBlocks.MONKEY_PUZZLE_BUTTON, ModBlocks.MONKEY_PUZZLE_PLANKS);
        fenceItem(ModBlocks.MONKEY_PUZZLE_FENCE, ModBlocks.MONKEY_PUZZLE_PLANKS);
        basicItem(ModBlocks.MONKEY_PUZZLE_DOOR.asItem());

        // YEW
        saplingItem(ModBlocks.YEW_SAPLING);
        buttonItem(ModBlocks.YEW_BUTTON, ModBlocks.YEW_PLANKS);
        fenceItem(ModBlocks.YEW_FENCE, ModBlocks.YEW_PLANKS);
        basicItem(ModBlocks.YEW_DOOR.asItem());

        // SPAWN EGGS - ALL ENTITIES
        withExistingParent(ModItems.TIGER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SNOW_LEOPARD_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.RACCOON_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.EARTHWORM_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PANGOLIN_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.DOMESTIC_GOAT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SEAGULL_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));

        // PASSERINE SPAWN EGGS
        withExistingParent(ModItems.BLACKCAP_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BLUETHROAT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BULLFINCH_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CANARY_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CARDINAL_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHAFFINCH_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GOLDCREST_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GOLDFINCH_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.NIGHTINGALE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.REDSTART_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.REEDLING_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ROBIN_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SISKIN_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SKYLARK_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SPARROW_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.TIT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WAGTAIL_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WAXWING_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));

        // MORE SPAWN EGGS
        withExistingParent(ModItems.ZEBRA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ZORSE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ZONKEY_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ELEPHANT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GIRAFFE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BROWN_BEAR_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MANTIS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PENGUIN_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.EAGLE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BACTRIAN_CAMEL_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BELUGA_STURGEON_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.STOAT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PHEASANT_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SAOLA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHIMPANZEE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CICADA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HARE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.FISHER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ROADRUNNER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.RUFF_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.DEER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.ROE_DEER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HOATZIN_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CROCODILE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HIPPOPOTAMUS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WHITE_SHARK_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.LION_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.PHILIPPINE_EAGLE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MONKEY_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WOLVERINE_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MERGANSER_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.WILDEBEEST_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.RED_PANDA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.KOALA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));

        // EXTINCT CRITTERS SPAWN EGGS
        withExistingParent(ModItems.CRYODRAKON_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VELOCIRAPTOR_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.HYPNOVENATOR_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.GALLIMIMUS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.DIPLODOCUS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.EROMANGASAURUS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.BEIPIAOSAURUS_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.DEARC_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.LATENIVENATRIX_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.MAIP_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.SAICHANIA_SPAWN_EGG.getId().getPath(),mcLoc("item/template_spawn_egg"));

        // ANIMAL ARMOR
        basicItem(ModItems.TIGER_ARMOR.get());
        basicItem(ModItems.SNOW_LEOPARD_ARMOR.get());

        // FOODSTUFFS
        basicItem(ModItems.RAW_CHEVON.get());
        basicItem(ModItems.COOKED_CHEVON.get());
        basicItem(ModItems.VODKA.get());
        basicItem(ModItems.RAW_PHEASANT.get());
        basicItem(ModItems.COOKED_PHEASANT.get());
        basicItem(ModItems.BELUGA_STURGEON_CAVIAR.get());
        basicItem(ModItems.RAW_SHRIMP.get());
        basicItem(ModItems.BANANA.get());

        // MISC ITEMS
        basicItem(ModItems.SPIKED_HEAVY_CORE.get());
        basicItem(ModItems.OBSIDIAN_SHARD.get());
        basicItem(ModItems.PANGOLIN_SCALE.get());
        basicItem(ModItems.CROCODILE_SCALE.get());
        basicItem(ModItems.IVORY.get());
        basicItem(ModItems.SAOLA_HORN.get());

        // DART GUN AND DARTS
        handheldItem(ModItems.DART_GUN);
        basicItem(ModItems.TRANQ_DART.get());

        // UNTIERED WEAPONS
        basicItem(ModItems.COBALT_CHAKRAM.get());
        handheldItem(ModItems.GOLDEN_KHOPESH);
        handheldItem(ModItems.IRON_WARFANS);
        handheldItem(ModItems.WOODEN_CLUB);
        handheldItem(ModItems.DIAMOND_WARSCYTHE);
        handheldItem(ModItems.BRONZE_HALBERD);
        handheldItem(ModItems.STONE_MACUAHUITL);

        // SPECIAL ITEMS
        basicItem(ModItems.FOSSIL.get());
        basicItem(ModItems.KEY.get());
        basicItem(ModItems.BESTIARY.get());
        basicItem(ModItems.BELUGA_STURGEON_BUCKET.get());
    }
    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"block/" + item.getId().getPath()));
    }

    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        final String MOD_ID = AncientHorizons.MOD_ID; // Change this to your mod id

        if(itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/handheld")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID,"item/" + item.getId().getPath()));
    }
}

