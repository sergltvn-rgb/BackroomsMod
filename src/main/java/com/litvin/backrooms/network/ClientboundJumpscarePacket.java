package com.litvin.backrooms.network;

import com.litvin.backrooms.client.JumpscareOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundJumpscarePacket {
    public ClientboundJumpscarePacket() {}

    public ClientboundJumpscarePacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                // Apply temporary nausea and blindness
                mc.player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, false, false));
                mc.player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));

                // Jitter player camera (pitch/yaw shake)
                mc.player.setXRot(mc.player.getXRot() + (mc.player.getRandom().nextFloat() - 0.5f) * 8.0f);
                mc.player.setYRot(mc.player.getYRot() + (mc.player.getRandom().nextFloat() - 0.5f) * 8.0f);

                // Show screen static glitch overlay
                JumpscareOverlayRenderer.showJumpscare();
            }
        });
        context.setPacketHandled(true);
    }
}
