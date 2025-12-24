package com.fungoussoup.ancienthorizons.network;

import com.fungoussoup.ancienthorizons.AncientHorizons;
import com.fungoussoup.ancienthorizons.bestiary.BestiaryManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Network packet for syncing bestiary discoveries to clients
 */
public class BestiaryNetworking {

    /**
     * Packet to sync discovered entries from server to client
     */
    public record SyncBestiaryPacket(List<ResourceLocation> discoveredEntries) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<SyncBestiaryPacket> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "sync_bestiary"));

        public static final StreamCodec<ByteBuf, SyncBestiaryPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, ResourceLocation.STREAM_CODEC),
                SyncBestiaryPacket::discoveredEntries,
                SyncBestiaryPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        /**
         * Handles the packet on the client side
         */
        public static void handle(SyncBestiaryPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                // Update client-side bestiary manager with discovered entries
                for (ResourceLocation entityType : packet.discoveredEntries()) {
                    BestiaryManager.discoverEntry(entityType);
                }
            });
        }
    }

    /**
     * Packet to notify client of a new discovery
     */
    public record DiscoveryNotificationPacket(ResourceLocation entityType, String entityName) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<DiscoveryNotificationPacket> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AncientHorizons.MOD_ID, "discovery_notification"));

        public static final StreamCodec<ByteBuf, DiscoveryNotificationPacket> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                DiscoveryNotificationPacket::entityType,
                ByteBufCodecs.STRING_UTF8,
                DiscoveryNotificationPacket::entityName,
                DiscoveryNotificationPacket::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        /**
         * Handles the packet on the client side
         */
        public static void handle(DiscoveryNotificationPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                // Mark as discovered on client
                BestiaryManager.discoverEntry(packet.entityType());

                // TODO: Show notification toast/message
                // Example: Minecraft.getInstance().getToasts().addToast(...)
            });
        }
    }

    /**
     * Sends sync packet to a player with all their discoveries
     */
    public static void syncToPlayer(ServerPlayer player, List<ResourceLocation> discoveredEntries) {
        PacketDistributor.sendToPlayer(player, new SyncBestiaryPacket(discoveredEntries));
    }

    /**
     * Sends discovery notification to a player
     */
    public static void notifyDiscovery(ServerPlayer player, ResourceLocation entityType, String entityName) {
        PacketDistributor.sendToPlayer(player, new DiscoveryNotificationPacket(entityType, entityName));
    }
}