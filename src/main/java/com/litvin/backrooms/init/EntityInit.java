package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.entity.BacteriaEntity;
import com.litvin.backrooms.entity.SmilerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BackroomsMod.MODID);

    public static final RegistryObject<EntityType<BacteriaEntity>> BACTERIA = ENTITIES.register("bacteria",
            () -> EntityType.Builder.of(BacteriaEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.5f)
                    .build(new ResourceLocation(BackroomsMod.MODID, "bacteria").toString()));

    public static final RegistryObject<EntityType<SmilerEntity>> SMILER = ENTITIES.register("smiler",
            () -> EntityType.Builder.of(SmilerEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f)
                    .build(new ResourceLocation(BackroomsMod.MODID, "smiler").toString()));

    public static final RegistryObject<EntityType<com.litvin.backrooms.entity.SkinStealerEntity>> SKIN_STEALER = ENTITIES.register("skin_stealer",
            () -> EntityType.Builder.of(com.litvin.backrooms.entity.SkinStealerEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.9f)
                    .build(new ResourceLocation(BackroomsMod.MODID, "skin_stealer").toString()));
}
