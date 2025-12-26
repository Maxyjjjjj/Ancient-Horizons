package com.fungoussoup.ancienthorizons.network;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AncientHorizons.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Register Mount Attack Packet
        registrar.playToServer(
                MountAttackPacket.TYPE,
                MountAttackPacket.STREAM_CODEC,
                MountAttackPacket::handle
        );

        // Register Bestiary Sync
        registrar.playToClient(
                BestiaryNetworking.SyncBestiaryPacket.TYPE,
                BestiaryNetworking.SyncBestiaryPacket.STREAM_CODEC,
                BestiaryNetworking.SyncBestiaryPacket::handle
        );

        registrar.playToClient(
                BestiaryNetworking.DiscoveryNotificationPacket.TYPE,
                BestiaryNetworking.DiscoveryNotificationPacket.STREAM_CODEC,
                BestiaryNetworking.DiscoveryNotificationPacket::handle
        );
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <MSG extends CustomPacketPayload> void sendToAllPlayers(MSG message) {
        PacketDistributor.sendToAllPlayers(message);
    }
}
