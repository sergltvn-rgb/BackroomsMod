package com.litvin.backrooms.network;

import com.litvin.backrooms.capability.SanityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSanitySyncPacket {
    private final float sanity;
    private final boolean hidden;

    public ClientboundSanitySyncPacket(float sanity, boolean hidden) {
        this.sanity = sanity;
        this.hidden = hidden;
    }

    public ClientboundSanitySyncPacket(FriendlyByteBuf buf) {
        this.sanity = buf.readFloat();
        this.hidden = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.sanity);
        buf.writeBoolean(this.hidden);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.getCapability(SanityProvider.SANITY).ifPresent(s -> {
                    s.setSanity(this.sanity);
                    s.setHidden(this.hidden);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
