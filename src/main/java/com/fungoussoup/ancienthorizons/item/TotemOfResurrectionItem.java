package com.fungoussoup.ancienthorizons.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class TotemOfResurrectionItem extends Item {

    private static final String ENTITY_KEY = "FossilEntity";

    public TotemOfResurrectionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack offhand = player.getOffhandItem();
        ItemStack mainhand = player.getMainHandItem();

        // Totem must be in one hand, fossil in the other
        ItemStack fossil = mainhand.getItem() instanceof FossilItem ? mainhand :
                offhand.getItem() instanceof FossilItem ? offhand : ItemStack.EMPTY;

        if (fossil.isEmpty()) return InteractionResult.PASS;

        ResourceLocation entityId = getFossilEntity(fossil);
        if (entityId == null) return InteractionResult.FAIL;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);

        ServerLevel serverLevel = (ServerLevel) level;

        type.spawn(
                serverLevel,
                context.getClickedPos().above(),
                net.minecraft.world.entity.MobSpawnType.TRIGGERED
        );

        // Consume items
        fossil.shrink(1);
        context.getItemInHand().shrink(1);

        return InteractionResult.CONSUME;
    }

    /* -------- fossil reader -------- */

    private static ResourceLocation getFossilEntity(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;

        CompoundTag tag = data.copyTag();
        if (!tag.contains(ENTITY_KEY)) return null;

        return ResourceLocation.tryParse(tag.getString(ENTITY_KEY));
    }
}

