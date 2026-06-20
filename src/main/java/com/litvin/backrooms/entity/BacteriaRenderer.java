package com.litvin.backrooms.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BacteriaRenderer extends GeoEntityRenderer<BacteriaEntity> {
    public BacteriaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BacteriaModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(BacteriaEntity entity, float entityYaw, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        // Новая вытянутая модель ~3.1 блока; масштаб под хитбокс 2.5 блока
        poseStack.scale(0.82F, 0.82F, 0.82F);
        if (entity.isPhantom()) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.35F);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (entity.isPhantom()) {
            com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();
    }
}

