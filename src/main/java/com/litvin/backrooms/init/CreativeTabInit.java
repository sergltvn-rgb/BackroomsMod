package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabInit {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BackroomsMod.MODID);

    public static final RegistryObject<CreativeModeTab> BACKROOMS_TAB = TABS.register("backrooms_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.backroomsmod.backrooms_tab"))
                    .icon(() -> new ItemStack(ItemInit.ALMOND_WATER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ItemInit.WALLPAPER.get());
                        output.accept(ItemInit.CARPET.get());
                        output.accept(ItemInit.CEILING_TILE.get());
                        output.accept(ItemInit.FLUORESCENT_LIGHT.get());
                        output.accept(ItemInit.STEEL_PIPE.get());
                        output.accept(ItemInit.GLITCHED_BLOCK.get());
                        output.accept(ItemInit.OFFICE_CHAIR.get());
                        output.accept(ItemInit.OFFICE_TABLE.get());
                        output.accept(ItemInit.FLICKERING_LIGHT.get());
                        output.accept(ItemInit.BROKEN_LIGHT.get());
                        output.accept(ItemInit.DRAWN_WALLPAPER.get());
                        output.accept(ItemInit.CRATE.get());
                        output.accept(ItemInit.CARDBOARD_BOX.get());
                        output.accept(ItemInit.FILE_CABINET.get());
                        output.accept(ItemInit.CEILING_VENT.get());
                        output.accept(ItemInit.OFFICE_PARTITION.get());
                        output.accept(ItemInit.WATER_COOLER.get());
                        output.accept(ItemInit.ALMOND_WATER.get());
                        output.accept(ItemInit.FLASHLIGHT.get());
                        output.accept(ItemInit.BATTERY.get());
                        output.accept(ItemInit.BACTERIA_SPAWN_EGG.get());
                        output.accept(ItemInit.SMILER_SPAWN_EGG.get());
                        output.accept(ItemInit.HAZMAT_HELMET.get());
                        output.accept(ItemInit.HAZMAT_CHESTPLATE.get());
                        output.accept(ItemInit.HAZMAT_LEGGINGS.get());
                        output.accept(ItemInit.HAZMAT_BOOTS.get());
                    })
                    .build());
}
