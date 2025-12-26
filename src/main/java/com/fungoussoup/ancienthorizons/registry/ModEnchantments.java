package com.fungoussoup.ancienthorizons.registry;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> DISCIPLINE = key("discipline");
    public static final ResourceKey<Enchantment> FRUIT_NINJA = key("fruit_ninja");
    public static final ResourceKey<Enchantment> PHANTOM_SLICE = key("phantom_slice");
    public static final ResourceKey<Enchantment> SANDSTORM = key("sandstorm");
    public static final ResourceKey<Enchantment> DISARMAMENT = key("disarmament");
    public static final ResourceKey<Enchantment> CRUDE_FORCE = key("crude_force");
    public static final ResourceKey<Enchantment> SACRIFICIAL_EDGE = key("sacrificial_edge");
    public static final ResourceKey<Enchantment> TYPHOON = key("typhoon");
    public static final ResourceKey<Enchantment> GRACE = key("grace");
    public static final ResourceKey<Enchantment> HARVEST = key("harvest");
    public static final ResourceKey<Enchantment> REAPING = key("reaping");
    public static final ResourceKey<Enchantment> BOOMERANG = key("boomerang");
    public static final ResourceKey<Enchantment> RICOCHET = key("ricochet");
    public static final ResourceKey<Enchantment> SEVERANCE = key("severance");
    public static final ResourceKey<Enchantment> PIERCE = key("pierce");
    public static final ResourceKey<Enchantment> COMMAND = key("command");
    public static final ResourceKey<Enchantment> TREMOR = key("tremor");
    public static final ResourceKey<Enchantment> AFTERSHOCK = key("aftershock");

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        // Katana enchantments
        register(context, DISCIPLINE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.KATANA_ENCHANTABLE),
                        5, 3,
                        Enchantment.dynamicCost(15, 9),
                        Enchantment.dynamicCost(50, 8),
                        2,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, FRUIT_NINJA, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.KATANA_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 5),
                        1,
                        EquipmentSlotGroup.OFFHAND)));

        register(context, PHANTOM_SLICE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.KATANA_ENCHANTABLE),
                        3, 2,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, SANDSTORM, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.KHOPESH_ENCHANTABLE),
                        4, 2,
                        Enchantment.dynamicCost(12, 6),
                        Enchantment.dynamicCost(35, 7),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, DISARMAMENT, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.KHOPESH_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 6),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        // Warfans enchantments
        register(context, TYPHOON, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.WARFANS_ENCHANTABLE),
                        4, 2,
                        Enchantment.dynamicCost(12, 6),
                        Enchantment.dynamicCost(40, 7),
                        2,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, GRACE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.WARFANS_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 6),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        // Club enchantments
        register(context, CRUDE_FORCE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.CLUB_ENCHANTABLE),
                        2, 1,
                        Enchantment.dynamicCost(8, 4),
                        Enchantment.dynamicCost(20, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        // Macuahuitl enchantments
        register(context, SACRIFICIAL_EDGE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.MACUAHUITL_ENCHANTABLE),
                        4, 2,
                        Enchantment.dynamicCost(12, 5),
                        Enchantment.dynamicCost(35, 7),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, HARVEST, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.WARSCYTHE_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(25, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, REAPING, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.WARSCYTHE_ENCHANTABLE),
                        4, 2,
                        Enchantment.dynamicCost(12, 6),
                        Enchantment.dynamicCost(35, 6),
                        1,
                        EquipmentSlotGroup.MAINHAND)));


        // Chakram enchantments
        register(context, BOOMERANG, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.CHAKRAM_ENCHANTABLE),
                        3, 2,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(25, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, RICOCHET, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.CHAKRAM_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(8, 4),
                        Enchantment.dynamicCost(20, 4),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, SEVERANCE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.CHAKRAM_ENCHANTABLE),
                        4, 2,
                        Enchantment.dynamicCost(12, 6),
                        Enchantment.dynamicCost(30, 6),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        // Halberd enchantments
        register(context, PIERCE, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.HALBERD_ENCHANTABLE),
                        5, 2,
                        Enchantment.dynamicCost(15, 7),
                        Enchantment.dynamicCost(45, 8),
                        2,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, COMMAND, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.HALBERD_ENCHANTABLE),
                        3, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        // Hammer enchantments
        register(context, TREMOR, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.HAMMER_ENCHANTABLE),
                        3, 2,
                        Enchantment.dynamicCost(12, 6),
                        Enchantment.dynamicCost(35, 6),
                        1,
                        EquipmentSlotGroup.MAINHAND)));

        register(context, AFTERSHOCK, Enchantment.enchantment(
                Enchantment.definition(items.getOrThrow(ModTags.Items.HAMMER_ENCHANTABLE),
                        4, 1,
                        Enchantment.dynamicCost(10, 5),
                        Enchantment.dynamicCost(30, 5),
                        1,
                        EquipmentSlotGroup.MAINHAND)));


    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, name));
    }
}
