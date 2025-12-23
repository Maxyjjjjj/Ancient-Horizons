package com.fungoussoup.ancienthorizons.network;

import com.fungoussoup.ancienthorizons.entity.custom.mob.MaipEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "ancienthorizons", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class MountAttackHandler {

    private static boolean wasAttackKeyPressed = false;

    @SubscribeEvent
    public static void onMouseInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        Entity vehicle = player.getVehicle();

        // Check if player is riding a Maip and pressing attack button
        if (vehicle instanceof MaipEntity) {
            if (event.isAttack()) {
                // Send packet to server to perform mount attack
                ModMessages.sendToServer(new MountAttackPacket());

                // Cancel the normal attack event to prevent player from attacking
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;

        Entity vehicle = player.getVehicle();

        // Alternative key-based input detection
        if (vehicle instanceof MaipEntity maip) {
            // Check for left mouse button (attack)
            boolean isAttackKeyPressed = mc.options.keyAttack.isDown();

            if (isAttackKeyPressed && !wasAttackKeyPressed) {
                // Attack key just pressed
                ModMessages.sendToServer(new MountAttackPacket());
            }

            wasAttackKeyPressed = isAttackKeyPressed;
        } else {
            wasAttackKeyPressed = false;
        }
    }
}