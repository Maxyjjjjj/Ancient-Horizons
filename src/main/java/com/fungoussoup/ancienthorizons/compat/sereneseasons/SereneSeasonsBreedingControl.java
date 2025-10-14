package com.fungoussoup.ancienthorizons.compat.sereneseasons;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class SereneSeasonsBreedingControl {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!SereneSeasonsBreedingSeason.isSereneSeasonsLoaded()) return;

        Player player = event.getEntity();
        Level level = player.level();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();

        if (!(target instanceof Animal animal)) return;
        if (!animal.isFood(stack)) return;
        if (!animal.isAlive() || animal.isBaby()) return;

        // Check if breeding is allowed in current season
        if (!SereneSeasonsBreedingSeason.canBreedInCurrentSeason(animal.getType(), level)) {
            event.setCanceled(true); // Cancel the interaction

            if (!level.isClientSide()) return;

            player.displayClientMessage(
                    Component.translatable("message.ancienthorizons.sereneseasons.out_of_season")
                            .withStyle(ChatFormatting.RED),
                    true
            );
        }
    }
}

