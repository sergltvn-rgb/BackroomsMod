package com.litvin.backrooms.entity;

import com.litvin.backrooms.BackroomsMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BacteriaModel extends GeoModel<BacteriaEntity> {

    @Override
    public ResourceLocation getModelResource(BacteriaEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "geo/bacteria.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BacteriaEntity object) {
        return new ResourceLocation(BackroomsMod.MODID, "textures/entity/bacteria.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BacteriaEntity animatable) {
        return new ResourceLocation(BackroomsMod.MODID, "animations/bacteria.animation.json");
    }
}
