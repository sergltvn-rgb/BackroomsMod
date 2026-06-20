package com.litvin.backrooms.item.client;

import com.litvin.backrooms.item.HazmatArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HazmatArmorRenderer extends GeoArmorRenderer<HazmatArmorItem> {
    public HazmatArmorRenderer() {
        super(new HazmatArmorModel());
    }
}
