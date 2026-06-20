package com.litvin.backrooms.client;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.capability.SanityProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ClientSanityOverlay implements IGuiOverlay {
    public static final ClientSanityOverlay INSTANCE = new ClientSanityOverlay();

    private static final ResourceLocation VIGNETTE = new ResourceLocation("textures/misc/vignette.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Render sanity HUD only if player is in a Backrooms dimension
        ResourceLocation dim = mc.player.level().dimension().location();
        if (!dim.getNamespace().equals(BackroomsMod.MODID)) return;

        mc.player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
            float currentSanity = sanity.getSanity();

            // 1. Render Locker Hiding Overlay
            if (sanity.isHidden()) {
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                
                // Very dark black/gray vignette to simulate looking through locker slits
                RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 0.95f);
                guiGraphics.blit(VIGNETTE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            } else if (currentSanity < 50.0f) {
                // 1.5 Render Red Screen Vignette at low sanity (< 50) when not hidden
                float opacity = (50.0f - currentSanity) / 50.0f; // 0.0 to 1.0
                
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                
                // Pulsate slightly
                float pulse = 1.0f + 0.1f * (float) Math.sin(mc.level.getGameTime() * 0.1f);
                float alpha = opacity * 0.45f * pulse;
                
                RenderSystem.setShaderColor(opacity * 0.7f, 0.0f, 0.0f, alpha);
                guiGraphics.blit(VIGNETTE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                
                RenderSystem.disableBlend();
                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
            }

            // 2. Render Sleek Glassmorphic Sanity Panel
            int barWidth = 80;
            int barHeight = 6;
            int posX = 10;
            int posY = screenHeight - 45; // Bottom left, fits well above chat

            // Draw glassmorphic panel background (semi-transparent black)
            guiGraphics.fill(posX - 4, posY - 12, posX + barWidth + 4, posY + barHeight + 4, 0xAA000000);

            // Draw Text
            String text = String.format("Рассудок: %.0f%%", currentSanity);
            int textColor = 0xFFFFFF; // White default
            
            if (currentSanity > 75.0f) {
                textColor = 0xFFE0E080; // Pastel yellow
            } else if (currentSanity > 40.0f) {
                textColor = 0xFFE0A040; // Pastel orange
            } else {
                textColor = 0xFFFF4040; // Pastel red
            }
            guiGraphics.drawString(mc.font, text, posX, posY - 10, textColor, true);

            // Draw Bar Background
            guiGraphics.fill(posX, posY, posX + barWidth, posY + barHeight, 0xFF444444);

            // Draw Filled Bar
            int filledWidth = (int) (barWidth * (currentSanity / 100.0f));
            int barColor = 0xFFD8D820; // Default Backrooms theme yellow
            
            if (currentSanity < 40.0f) {
                barColor = 0xFFFF4444; // Warning Red
            }
            guiGraphics.fill(posX, posY, posX + filledWidth, posY + barHeight, barColor);
        });
    }
}
