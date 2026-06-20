package com.litvin.backrooms.entity.client;

import com.litvin.backrooms.entity.SmilerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SmilerRenderer extends GeoEntityRenderer<SmilerEntity> {
    public SmilerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SmilerModel());
        this.shadowRadius = 0.0f; // У смайлера нет тени
    }

    @Override
    public ResourceLocation getTextureLocation(SmilerEntity animatable) {
        return new ResourceLocation("backroomsmod", "textures/entity/smiler.png");
    }

    @Override
    public RenderType getRenderType(SmilerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        // Смайлер светится в темноте: аддитивный emissive-рендер.
        // Чёрный фон лица становится прозрачным, в темноте видны только
        // светящиеся глаза и оскал — канонный эффект Backrooms.
        return RenderType.eyes(texture);
    }
}
