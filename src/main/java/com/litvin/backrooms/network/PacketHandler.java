package com.litvin.backrooms.network;

import com.litvin.backrooms.BackroomsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BackroomsMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.messageBuilder(ClientboundSanitySyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundSanitySyncPacket::encode)
                .decoder(ClientboundSanitySyncPacket::new)
                .consumerMainThread(ClientboundSanitySyncPacket::handle)
                .add();

        INSTANCE.messageBuilder(ClientboundJumpscarePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundJumpscarePacket::encode)
                .decoder(ClientboundJumpscarePacket::new)
                .consumerMainThread(ClientboundJumpscarePacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
