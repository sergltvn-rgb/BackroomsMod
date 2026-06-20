package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.effect.CameraShakeMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectInit {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BackroomsMod.MODID);

    public static final RegistryObject<MobEffect> CAMERA_SHAKE = EFFECTS.register("camera_shake", CameraShakeMobEffect::new);
}
