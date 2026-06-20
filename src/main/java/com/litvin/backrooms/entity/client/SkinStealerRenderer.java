package com.litvin.backrooms.entity.client;

import com.litvin.backrooms.entity.SkinStealerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkinStealerRenderer extends GeoEntityRenderer<SkinStealerEntity> {
    public SkinStealerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkinStealerModel());
        this.shadowRadius = 0.5f;
    }
}
