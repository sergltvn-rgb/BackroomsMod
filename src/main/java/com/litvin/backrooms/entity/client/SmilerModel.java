package com.litvin.backrooms.entity.client;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.entity.SmilerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SmilerModel extends GeoModel<SmilerEntity> {
    @Override
    public ResourceLocation getModelResource(SmilerEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "geo/smiler.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SmilerEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "textures/entity/smiler.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmilerEntity animatable) {
        return new ResourceLocation(BackroomsMod.MODID, "animations/smiler.animation.json");
    }
}
