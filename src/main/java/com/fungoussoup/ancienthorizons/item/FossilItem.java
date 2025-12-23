package com.fungoussoup.ancienthorizons.item;

import com.fungoussoup.ancienthorizons.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class FossilItem extends Item {

    private static final String ENTITY_KEY = "FossilEntity";

    public FossilItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        ensureEntityAssigned(stack, level.random);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        ensureEntityAssigned(stack, level.random);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        ResourceLocation id = getFossilEntity(stack);

        if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            tooltip.add(
                    Component.translatable("tooltip.ancienthorizons.fossil_of")
                            .withStyle(ChatFormatting.GRAY)
                            .append(type.getDescription())
                            .withStyle(ChatFormatting.AQUA)
            );
        } else {
            tooltip.add(
                    Component.translatable("tooltip.ancienthorizons.fossil_unidentified")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }

    private void ensureEntityAssigned(ItemStack stack, RandomSource random) {
        if (getFossilEntity(stack) != null) return;

        List<EntityType<?>> entities = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> type.is(ModTags.EntityTypes.FOSSIL_ANIMALS))
                .toList();

        if (entities.isEmpty()) return;

        EntityType<?> chosen = entities.get(random.nextInt(entities.size()));
        setFossilEntity(stack, BuiltInRegistries.ENTITY_TYPE.getKey(chosen));
    }

    private static void setFossilEntity(ItemStack stack, ResourceLocation id) {
        CompoundTag tag = new CompoundTag();
        tag.putString(ENTITY_KEY, id.toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Nullable
    private static ResourceLocation getFossilEntity(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;

        CompoundTag tag = data.copyTag(); // safe read
        if (!tag.contains(ENTITY_KEY)) return null;

        return ResourceLocation.tryParse(tag.getString(ENTITY_KEY));
    }
}