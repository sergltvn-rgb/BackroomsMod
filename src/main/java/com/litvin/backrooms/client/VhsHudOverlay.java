package com.litvin.backrooms.client;

import com.litvin.backrooms.BackroomsMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class VhsHudOverlay implements IGuiOverlay {
    public static final VhsHudOverlay INSTANCE = new VhsHudOverlay();

    private static final ResourceLocation VHS_NOISE = new ResourceLocation(BackroomsMod.MODID, "textures/gui/vhs_noise.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Render VHS HUD only if player is in a Backrooms dimension
        ResourceLocation dim = mc.player.level().dimension().location();
        if (!dim.getNamespace().equals(BackroomsMod.MODID)) return;

        long time = mc.level.getGameTime();

        // 1. Draw static noise overlay
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Very faint noise overlay
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.15f);
        
        // Randomize UV coordinates slightly to animate the static
        float uOffset = (time % 20) * 0.05f;
        float vOffset = (time % 15) * 0.05f;
        
        guiGraphics.blit(VHS_NOISE, 0, 0, 0, uOffset, vOffset, screenWidth, screenHeight, 256, 256);

        // 2. Draw REC and Time (using normal color)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Blink REC indicator every second
        if (time % 20 < 10) {
            guiGraphics.drawString(mc.font, "REC \u2022", 10, 10, 0xFFFF0000, false);
        } else {
            guiGraphics.drawString(mc.font, "REC  ", 10, 10, 0xFFFFFFFF, false);
        }

        // Draw Play indicator
        guiGraphics.drawString(mc.font, "\u25B6 PLAY", 10, 25, 0xFFFFFFFF, false);

        // Draw Timer
        int seconds = (int) (time / 20) % 60;
        int minutes = (int) (time / 1200) % 60;
        int hours = (int) (time / 72000);
        String timeString = String.format("AM %02d:%02d:%02d", hours, minutes, seconds);
        
        // Draw in bottom right corner
        int timeWidth = mc.font.width(timeString);
        guiGraphics.drawString(mc.font, timeString, screenWidth - timeWidth - 10, screenHeight - 20, 0xFFFFFFFF, false);

        // Optional: Draw Battery indicator
        guiGraphics.drawString(mc.font, "[### ]", screenWidth - mc.font.width("[### ]") - 10, 10, 0xFFFFFFFF, false);

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
