package com.fungoussoup.ancienthorizons.network;

import com.fungoussoup.ancienthorizons.entity.custom.mob.MaipEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MountAttackPacket() implements CustomPacketPayload {

    public static final Type<MountAttackPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("ancienthorizons", "mount_attack"));

    public static final StreamCodec<ByteBuf, MountAttackPacket> STREAM_CODEC =
            StreamCodec.unit(new MountAttackPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MountAttackPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isServerbound()) {
                ServerPlayer player = (ServerPlayer) context.player();
                Entity vehicle = player.getVehicle();

                if (vehicle instanceof MaipEntity maip) {
                    // Verify player is the owner
                    if (maip.isOwnedBy(player)) {
                        maip.performMountAttack();
                    }
                }
            }
        });
    }
}
