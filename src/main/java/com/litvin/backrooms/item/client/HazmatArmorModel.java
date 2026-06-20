package com.litvin.backrooms.item.client;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.item.HazmatArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HazmatArmorModel extends GeoModel<HazmatArmorItem> {
    @Override
    public ResourceLocation getModelResource(HazmatArmorItem object) {
        return new ResourceLocation(BackroomsMod.MODID, "geo/hazmat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HazmatArmorItem object) {
        return new ResourceLocation(BackroomsMod.MODID, "textures/armor/hazmat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HazmatArmorItem animatable) {
        return new ResourceLocation(BackroomsMod.MODID, "animations/hazmat.animation.json");
    }
}
