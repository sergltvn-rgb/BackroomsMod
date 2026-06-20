package com.litvin.backrooms.entity.client;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.entity.SkinStealerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SkinStealerModel extends GeoModel<SkinStealerEntity> {
    @Override
    public ResourceLocation getModelResource(SkinStealerEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "geo/skin_stealer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SkinStealerEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "textures/entity/skin_stealer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SkinStealerEntity animatable) {
        return new ResourceLocation(BackroomsMod.MODID, "animations/skin_stealer.animation.json");
    }
}
