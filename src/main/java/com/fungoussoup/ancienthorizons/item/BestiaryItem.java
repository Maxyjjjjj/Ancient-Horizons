package com.fungoussoup.ancienthorizons.item;

import com.fungoussoup.ancienthorizons.client.screen.BestiaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The bestiary item that opens the bestiary GUI when used
 */
public class BestiaryItem extends Item {

    public BestiaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            openBestiaryScreen();
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void openBestiaryScreen() {
        Minecraft.getInstance().setScreen(new BestiaryScreen());
    }
}