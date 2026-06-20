package com.litvin.backrooms.event;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.client.JumpscareOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BackroomsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            JumpscareOverlayRenderer.clientTick();

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.level != null) {
                double closestDist = Double.MAX_VALUE;
                for (net.minecraft.world.entity.Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof com.litvin.backrooms.entity.BacteriaEntity bacteria) {
                        double dist = mc.player.distanceTo(bacteria);
                        if (dist < closestDist) {
                            closestDist = dist;
                        }
                    }
                }

                float intensity = 0.0F;
                if (closestDist < 16.0D) {
                    intensity = (float) (1.0D - (closestDist / 16.0D));
                    if (intensity < 0.0F) intensity = 0.0F;
                    if (intensity > 0.8F) intensity = 0.8F;
                }
                JumpscareOverlayRenderer.setProximityStatic(intensity);
            }
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hasEffect(com.litvin.backrooms.init.EffectInit.CAMERA_SHAKE.get())) {
            int amplifier = mc.player.getEffect(com.litvin.backrooms.init.EffectInit.CAMERA_SHAKE.get()).getAmplifier();
            float shakeIntensity = (amplifier + 1) * 0.5f;

            long time = mc.level.getGameTime();
            float delta = mc.getFrameTime();

            float pitchOffset = (float) (Math.sin((time + delta) * 1.5) * shakeIntensity);
            float yawOffset = (float) (Math.cos((time + delta) * 1.3) * shakeIntensity);
            float rollOffset = (float) (Math.sin((time + delta) * 1.7) * shakeIntensity * 0.5f);

            event.setPitch(event.getPitch() + pitchOffset);
            event.setYaw(event.getYaw() + yawOffset);
            event.setRoll(event.getRoll() + rollOffset);
        }
    }
}

