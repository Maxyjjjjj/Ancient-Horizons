package com.fungoussoup.ancienthorizons.datagen;

import com.fungoussoup.ancienthorizons.registry.ModItems;
import com.fungoussoup.ancienthorizons.registry.ModLootTables;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class ChimpBarteringLoot implements LootTableSubProvider {

    public ChimpBarteringLoot(HolderLookup.Provider provider) {

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {

        /* ===============================
           MAIN BARTERING TABLE
           =============================== */
        consumer.accept(ModLootTables.CHIMP_BARTERING,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(NestedLootTable.lootTableReference(ModLootTables.CHIMP_BARTERING_FOOD).setWeight(70))
                                .add(NestedLootTable.lootTableReference(ModLootTables.CHIMP_BARTERING_TREASURES).setWeight(5))
                                .add(NestedLootTable.lootTableReference(ModLootTables.CHIMP_BARTERING_TRASH).setWeight(10))
                                .add(NestedLootTable.lootTableReference(ModLootTables.CHIMP_BARTERING_BAMBOO).setWeight(15))
                        )
        );

        /* ===============================
           TREASURES
           =============================== */
        consumer.accept(ModLootTables.CHIMP_BARTERING_TREASURES,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(1))
                                .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(10))
                                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(8))
                                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(12)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                                .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                                .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                        .when(LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location().setDimension(Level.END))))
                                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(18)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK).setWeight(12))
                                .add(LootItem.lootTableItem(Items.NAME_TAG).setWeight(14))
                                .add(LootItem.lootTableItem(Items.SADDLE).setWeight(10))
                                .add(LootItem.lootTableItem(Items.MUSIC_DISC_PIGSTEP).setWeight(2)
                                        .when(LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location().setDimension(Level.NETHER))))));

        /* ===============================
           TRASH
           =============================== */
        consumer.accept(ModLootTables.CHIMP_BARTERING_TRASH,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.STICK).setWeight(35)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                                .add(LootItem.lootTableItem(Items.SPIDER_EYE).setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                                .add(LootItem.lootTableItem(Items.BONE).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                                .add(LootItem.lootTableItem(Items.FEATHER).setWeight(12)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                                .add(LootItem.lootTableItem(Items.LEATHER).setWeight(10)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                .add(LootItem.lootTableItem(Items.STRING).setWeight(12)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                                .add(LootItem.lootTableItem(Items.GRAVEL).setWeight(8)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 10))))
                                .add(LootItem.lootTableItem(Items.FLINT).setWeight(5)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        )
        );

        /* ===============================
           FOOD
           =============================== */
        consumer.accept(ModLootTables.CHIMP_BARTERING_FOOD,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.APPLE).setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                .add(LootItem.lootTableItem(Items.SWEET_BERRIES).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))))
                                .add(LootItem.lootTableItem(Items.MELON_SLICE).setWeight(15)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                                .add(LootItem.lootTableItem(Items.GLOW_BERRIES).setWeight(12)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                                .add(LootItem.lootTableItem(Items.CHICKEN).setWeight(10)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                .add(LootItem.lootTableItem(Items.RABBIT).setWeight(8)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                .add(LootItem.lootTableItem(ModItems.RAW_PHEASANT).setWeight(8)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                .add(LootItem.lootTableItem(Items.HONEY_BOTTLE).setWeight(7))
                                .add(LootItem.lootTableItem(Items.CHORUS_FRUIT).setWeight(5)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                        .when(LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location().setDimension(Level.END))))
                        )
        );

        /* ===============================
           BAMBOO
           =============================== */
        consumer.accept(ModLootTables.CHIMP_BARTERING_BAMBOO,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.BAMBOO).setWeight(1)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 16))))
                        )
        );
    }
}
