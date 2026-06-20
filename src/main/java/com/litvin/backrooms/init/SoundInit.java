package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BackroomsMod.MODID);

    // Кастомный звук гудения ламп
    public static final RegistryObject<SoundEvent> FLUORESCENT_BUZZ = SOUNDS.register("fluorescent_buzz",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "fluorescent_buzz")));

    public static final RegistryObject<SoundEvent> AMBIENT_VHS = SOUNDS.register("ambient_vhs",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "ambient_vhs")));

    public static final RegistryObject<SoundEvent> BACTERIA_SCREAM = SOUNDS.register("bacteria_scream",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "bacteria_scream")));

    public static final RegistryObject<SoundEvent> LAMP_FLICKER = SOUNDS.register("lamp_flicker",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "lamp_flicker")));

    public static final RegistryObject<SoundEvent> AMBIENT_CREAK = SOUNDS.register("ambient_creak",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "ambient_creak")));

    public static final RegistryObject<SoundEvent> AMBIENT_STEPS = SOUNDS.register("ambient_steps",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "ambient_steps")));

    public static final RegistryObject<SoundEvent> MUSIC_DISC_POOLS = SOUNDS.register("music_disc_pools",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_pools")));

    public static final RegistryObject<SoundEvent> MUSIC_DISC_ESCAPEE = SOUNDS.register("music_disc_escapee",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_escapee")));

    public static final RegistryObject<SoundEvent> MUSIC_DISC_RUN_FOR_IT = SOUNDS.register("music_disc_run_for_it",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_run_for_it")));

    public static final RegistryObject<SoundEvent> MUSIC_DISC_KINGS_CURFEW = SOUNDS.register("music_disc_kings_curfew", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_kings_curfew")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_MENU = SOUNDS.register("music_disc_menu", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_menu")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ROW_YOUR_BOAT = SOUNDS.register("music_disc_row_your_boat", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_row_your_boat")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_SNACKROOSIC = SOUNDS.register("music_disc_snackroosic", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_snackroosic")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ELEVATOR_WONT_KILL_YOU = SOUNDS.register("music_disc_elevator_wont_kill_you", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_elevator_wont_kill_you")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_YOU_DAY = SOUNDS.register("music_disc_you_day", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_you_day")));
    public static final RegistryObject<SoundEvent> MUSIC_DISC_YOU_DAY___INSTRUMENTAL_MIX = SOUNDS.register("music_disc_you_day___instrumental_mix", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BackroomsMod.MODID, "music_disc_you_day___instrumental_mix")));
}

