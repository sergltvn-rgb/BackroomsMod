package com.litvin.backrooms.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Random;

public class JumpscareOverlayRenderer implements IGuiOverlay {
    public static final JumpscareOverlayRenderer INSTANCE = new JumpscareOverlayRenderer();
    private static int ticksActive = 0;
    private static float proximityStatic = 0.0F;
    private static final Random RANDOM = new Random();

    public static void showJumpscare() {
        ticksActive = 15; // Displays for 0.75 seconds (15 ticks)
    }

    public static void setProximityStatic(float val) {
        proximityStatic = val;
    }

    public static void clientTick() {
        if (ticksActive > 0) {
            ticksActive--;
        }
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (ticksActive <= 0 && proximityStatic <= 0.0F) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float intensity = ticksActive > 0 ? 1.0F : proximityStatic;

        // 1. Render horizontal static glitch bars
        int bars = (int) ((12 + RANDOM.nextInt(8)) * intensity);
        for (int i = 0; i < bars; i++) {
            int yStart = RANDOM.nextInt(screenHeight);
            int barHeight = 2 + RANDOM.nextInt(12);
            int noise = RANDOM.nextInt(120);
            int alpha = (int) (170 * intensity);
            int color = (alpha << 24) | (noise << 16) | (noise << 8) | noise;
            guiGraphics.fill(0, yStart, screenWidth, yStart + barHeight, color);
        }

        // 2. Render static grain dots across the screen
        int dots = (int) ((50 + RANDOM.nextInt(30)) * intensity);
        for (int i = 0; i < dots; i++) {
            int gx = RANDOM.nextInt(screenWidth);
            int gy = RANDOM.nextInt(screenHeight);
            int gw = 4 + RANDOM.nextInt(12);
            int gh = 1 + RANDOM.nextInt(2);
            int alpha = (int) (220 * intensity);
            int baseColor = RANDOM.nextBoolean() ? 0xFFFFFF : 0x000000;
            int color = (alpha << 24) | baseColor;
            guiGraphics.fill(gx, gy, gx + gw, gy + gh, color);
        }

        // 3. Render red flashing signal lost message
        if (ticksActive > 0 && ticksActive % 4 < 2) {
            String text = "WARNING: CARRIER SIGNAL LOST";
            int x = screenWidth / 2;
            int y = screenHeight / 2 - 5;
            guiGraphics.drawCenteredString(mc.font, text, x, y, 0xFFFF0000);
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}

