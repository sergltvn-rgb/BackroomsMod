package com.litvin.backrooms.event;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.client.ClientSanityOverlay;
import com.litvin.backrooms.client.JumpscareOverlayRenderer;
import com.litvin.backrooms.entity.BacteriaRenderer;
import com.litvin.backrooms.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BackroomsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.BACTERIA.get(), BacteriaRenderer::new);
        event.registerEntityRenderer(EntityInit.SMILER.get(), com.litvin.backrooms.entity.client.SmilerRenderer::new);
        event.registerEntityRenderer(EntityInit.SKIN_STEALER.get(), com.litvin.backrooms.entity.client.SkinStealerRenderer::new);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "sanity", ClientSanityOverlay.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "jumpscare_glitch", JumpscareOverlayRenderer.INSTANCE);
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "vhs_hud", com.litvin.backrooms.client.VhsHudOverlay.INSTANCE);
    }
}
