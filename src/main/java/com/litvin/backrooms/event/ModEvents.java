package com.litvin.backrooms.event;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.entity.BacteriaEntity;
import com.litvin.backrooms.entity.BackroomsSpawnRules;
import com.litvin.backrooms.init.EntityInit;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = BackroomsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(EntityInit.BACTERIA.get(), BacteriaEntity.createAttributes().build());
        event.put(EntityInit.SMILER.get(), com.litvin.backrooms.entity.SmilerEntity.createAttributes().build());
        event.put(EntityInit.SKIN_STEALER.get(), com.litvin.backrooms.entity.SkinStealerEntity.createAttributes().build());
        event.put(EntityInit.DEATH_RAT.get(), com.litvin.backrooms.entity.custom.DeathRatEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent event) {
        event.register(com.litvin.backrooms.capability.ISanity.class);
    }

    // Регистрация правил естественного спавна (SpawnPlacements не потокобезопасны — только в enqueueWork)
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Bacteria — на земле, при любом свете
            SpawnPlacements.register(
                    EntityInit.BACTERIA.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    BackroomsSpawnRules::checkBacteriaSpawn
            );
            // Smiler — на земле, только в темноте
            SpawnPlacements.register(
                    EntityInit.SMILER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    BackroomsSpawnRules::checkSmilerSpawn
            );
            // Skin Stealer
            SpawnPlacements.register(
                    EntityInit.SKIN_STEALER.get(),
                    SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    BackroomsSpawnRules::checkSkinStealerSpawn
            );
        });
    }
}

