package com.fungoussoup.ancienthorizons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.List;
import java.util.UUID;

public class KeyItem extends Item {
    public KeyItem(Properties properties) {
        // Keys should not stack to ensure each unique ID stays on its own item
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        if (level.getBlockState(pos).getBlock() instanceof AbstractChestBlock<?>) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ChestBlockEntity chest) {
                if (player.isShiftKeyDown()) {
                    return lockChest(level, pos, chest, stack, player);
                } else {
                    return unlockChest(level, pos, chest, stack, player);
                }
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult lockChest(Level level, BlockPos pos, ChestBlockEntity chest, ItemStack key, Player player) {
        CompoundTag chestTag = chest.getPersistentData();

        if (chestTag.getBoolean("IsLocked")) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ancienthorizons.chest_already_locked").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        String keyId = getOrCreateKeyId(key);
        chestTag.putBoolean("IsLocked", true);
        chestTag.putString("KeyId", keyId);
        chestTag.putString("LockedBy", player.getName().getString());
        chest.setChanged();

        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.displayClientMessage(Component.translatable("message.ancienthorizons.success_chest_lock").withStyle(ChatFormatting.GREEN), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private InteractionResult unlockChest(Level level, BlockPos pos, ChestBlockEntity chest, ItemStack key, Player player) {
        CompoundTag chestTag = chest.getPersistentData();

        if (!chestTag.getBoolean("IsLocked")) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ancienthorizons.not_locked").withStyle(ChatFormatting.YELLOW), true);
            }
            return InteractionResult.FAIL;
        }

        String chestKeyId = chestTag.getString("KeyId");
        String keyId = getOrCreateKeyId(key);

        if (!chestKeyId.equals(keyId)) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("message.ancienthorizons.incorrect_key").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        chestTag.remove("IsLocked");
        chestTag.remove("KeyId");
        chestTag.remove("LockedBy");
        chest.setChanged();

        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.displayClientMessage(Component.translatable("message.ancienthorizons.success_chest_unlock").withStyle(ChatFormatting.GREEN), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Replaces getOrCreateTag() logic using DataComponents
     */
    private String getOrCreateKeyId(ItemStack stack) {
        // 1. Fetch existing CustomData or get empty if it doesn't exist
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if (!tag.contains("KeyId")) {
            String newId = UUID.randomUUID().toString();
            tag.putString("KeyId", newId);

            // 2. Apply the modified tag back to the stack using DataComponents
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return newId;
        }

        return tag.getString("KeyId");
    }

    public static boolean canUnlock(ItemStack key, ChestBlockEntity chest) {
        if (!(key.getItem() instanceof KeyItem)) return false;

        CompoundTag chestTag = chest.getPersistentData();
        if (!chestTag.getBoolean("IsLocked")) return true;

        CustomData customData = key.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return false;

        CompoundTag keyTag = customData.copyTag();
        if (!keyTag.contains("KeyId")) return false;

        return chestTag.getString("KeyId").equals(keyTag.getString("KeyId"));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (customData != null) ? customData.copyTag() : null;

        if (tag != null && tag.contains("KeyId")) {
            String keyId = tag.getString("KeyId");
            tooltipComponents.add(Component.translatable("tooltip.ancienthorizons.key_id")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(keyId.substring(0, 8) + "...")
                            .withStyle(ChatFormatting.DARK_GRAY)));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.ancienthorizons.unbound_key").withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component.translatable("tooltip.ancienthorizons.use_key_on_chest").withStyle(ChatFormatting.DARK_GRAY));
        }

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("tooltip.ancienthorizons.unlock_chest").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.ancienthorizons.lock_chest").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().contains("KeyId");
    }
}