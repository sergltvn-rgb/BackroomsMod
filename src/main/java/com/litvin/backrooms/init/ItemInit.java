package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.item.AlmondWaterItem;
import com.litvin.backrooms.item.HazmatArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BackroomsMod.MODID);

    // Блоки как предметы
    public static final RegistryObject<Item> WALLPAPER = ITEMS.register("wallpaper",
            () -> new BlockItem(BlockInit.WALLPAPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CARPET = ITEMS.register("carpet",
            () -> new BlockItem(BlockInit.CARPET.get(), new Item.Properties()));

    public static final RegistryObject<Item> CEILING_TILE = ITEMS.register("ceiling_tile",
            () -> new BlockItem(BlockInit.CEILING_TILE.get(), new Item.Properties()));

    public static final RegistryObject<Item> FLUORESCENT_LIGHT = ITEMS.register("fluorescent_light",
            () -> new BlockItem(BlockInit.FLUORESCENT_LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Item> STEEL_PIPE = ITEMS.register("steel_pipe",
            () -> new BlockItem(BlockInit.STEEL_PIPE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLITCHED_BLOCK = ITEMS.register("glitched_block",
            () -> new BlockItem(BlockInit.GLITCHED_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> LOCKER = ITEMS.register("locker",
            () -> new BlockItem(BlockInit.LOCKER.get(), new Item.Properties()));

    public static final RegistryObject<Item> OFFICE_CHAIR = ITEMS.register("office_chair",
            () -> new BlockItem(BlockInit.OFFICE_CHAIR.get(), new Item.Properties()));

    public static final RegistryObject<Item> OFFICE_TABLE = ITEMS.register("office_table",
            () -> new BlockItem(BlockInit.OFFICE_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> FLICKERING_LIGHT = ITEMS.register("flickering_light",
            () -> new BlockItem(BlockInit.FLICKERING_LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Item> BROKEN_LIGHT = ITEMS.register("broken_light",
            () -> new BlockItem(BlockInit.BROKEN_LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Item> DRAWN_WALLPAPER = ITEMS.register("drawn_wallpaper",
            () -> new BlockItem(BlockInit.DRAWN_WALLPAPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRATE = ITEMS.register("crate",
            () -> new BlockItem(BlockInit.CRATE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CARDBOARD_BOX = ITEMS.register("cardboard_box",
            () -> new BlockItem(BlockInit.CARDBOARD_BOX.get(), new Item.Properties()));

    public static final RegistryObject<Item> FILE_CABINET = ITEMS.register("file_cabinet",
            () -> new BlockItem(BlockInit.FILE_CABINET.get(), new Item.Properties()));

    public static final RegistryObject<Item> CEILING_VENT = ITEMS.register("ceiling_vent",
            () -> new BlockItem(BlockInit.CEILING_VENT.get(), new Item.Properties()));

    public static final RegistryObject<Item> OFFICE_PARTITION = ITEMS.register("office_partition",
            () -> new BlockItem(BlockInit.OFFICE_PARTITION.get(), new Item.Properties()));

    public static final RegistryObject<Item> WATER_COOLER = ITEMS.register("water_cooler",
            () -> new BlockItem(BlockInit.WATER_COOLER.get(), new Item.Properties()));

    // Предметы
    public static final RegistryObject<Item> ALMOND_WATER = ITEMS.register("almond_water",
            () -> new AlmondWaterItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> FLASHLIGHT = ITEMS.register("flashlight",
            () -> new com.litvin.backrooms.item.FlashlightItem(new Item.Properties().durability(600)));

    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> BACTERIA_SPAWN_EGG = ITEMS.register("bacteria_spawn_egg",
            () -> new net.minecraftforge.common.ForgeSpawnEggItem(EntityInit.BACTERIA, 0x000000, 0x555555, new Item.Properties()));

    // Броня
    public static final RegistryObject<Item> HAZMAT_HELMET = ITEMS.register("hazmat_helmet",
            () -> new com.litvin.backrooms.item.HazmatArmorItem(HazmatArmorMaterial.HAZMAT, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> HAZMAT_CHESTPLATE = ITEMS.register("hazmat_chestplate",
            () -> new com.litvin.backrooms.item.HazmatArmorItem(HazmatArmorMaterial.HAZMAT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> HAZMAT_LEGGINGS = ITEMS.register("hazmat_leggings",
            () -> new com.litvin.backrooms.item.HazmatArmorItem(HazmatArmorMaterial.HAZMAT, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> HAZMAT_BOOTS = ITEMS.register("hazmat_boots",
            () -> new com.litvin.backrooms.item.HazmatArmorItem(HazmatArmorMaterial.HAZMAT, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> SMILER_SPAWN_EGG = ITEMS.register("smiler_spawn_egg",
            () -> new net.minecraftforge.common.ForgeSpawnEggItem(EntityInit.SMILER, 0x000000, 0xFFFFFF, new Item.Properties()));
    public static final RegistryObject<Item> POOL_TILES = ITEMS.register("pool_tiles",
            () -> new BlockItem(BlockInit.POOL_TILES.get(), new Item.Properties()));


    public static final RegistryObject<Item> PIPE_WRENCH = ITEMS.register("pipe_wrench",
            () -> new net.minecraft.world.item.SwordItem(net.minecraft.world.item.Tiers.IRON, 3, -2.4f, new Item.Properties()));

    public static final RegistryObject<Item> CANNED_FOOD = ITEMS.register("canned_food",
            () -> new Item(new Item.Properties().food(new net.minecraft.world.food.FoodProperties.Builder().nutrition(6).saturationMod(0.6f).build())));

    public static final RegistryObject<Item> JUICE = ITEMS.register("juice",
            () -> new Item(new Item.Properties().food(new net.minecraft.world.food.FoodProperties.Builder().nutrition(4).saturationMod(0.3f).alwaysEat().build())));

    public static final RegistryObject<Item> SANITY_PILLS = ITEMS.register("sanity_pills",
            () -> new com.litvin.backrooms.item.SanityPillsItem(new Item.Properties().food(new net.minecraft.world.food.FoodProperties.Builder().alwaysEat().build())));

    public static final RegistryObject<Item> SCUBA_HELMET = ITEMS.register("scuba_helmet",
            () -> new net.minecraft.world.item.ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> SCUBA_CHESTPLATE = ITEMS.register("scuba_chestplate",
            () -> new net.minecraft.world.item.ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> SCUBA_LEGGINGS = ITEMS.register("scuba_leggings",
            () -> new net.minecraft.world.item.ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> SCUBA_BOOTS = ITEMS.register("scuba_boots",
            () -> new net.minecraft.world.item.ArmorItem(net.minecraft.world.item.ArmorMaterials.IRON, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> MUSIC_DISC_POOLS = ITEMS.register("music_disc_pools",
            () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_POOLS, new Item.Properties().stacksTo(1), 300));

    public static final RegistryObject<Item> MUSIC_DISC_ESCAPEE = ITEMS.register("music_disc_escapee",
            () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_ESCAPEE, new Item.Properties().stacksTo(1), 2100));

    public static final RegistryObject<Item> MUSIC_DISC_RUN_FOR_IT = ITEMS.register("music_disc_run_for_it",
            () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_RUN_FOR_IT, new Item.Properties().stacksTo(1), 2400));

    public static final RegistryObject<Item> CONCRETE1 = ITEMS.register("concrete1", () -> new BlockItem(BlockInit.CONCRETE1.get(), new Item.Properties()));
    public static final RegistryObject<Item> CONCRETE2 = ITEMS.register("concrete2", () -> new BlockItem(BlockInit.CONCRETE2.get(), new Item.Properties()));
    public static final RegistryObject<Item> CONCRETE5 = ITEMS.register("concrete5", () -> new BlockItem(BlockInit.CONCRETE5.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUST2 = ITEMS.register("rust2", () -> new BlockItem(BlockInit.RUST2.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUST3 = ITEMS.register("rust3", () -> new BlockItem(BlockInit.RUST3.get(), new Item.Properties()));
    public static final RegistryObject<Item> METAL1 = ITEMS.register("metal1", () -> new BlockItem(BlockInit.METAL1.get(), new Item.Properties()));
    public static final RegistryObject<Item> METAL2 = ITEMS.register("metal2", () -> new BlockItem(BlockInit.METAL2.get(), new Item.Properties()));
    public static final RegistryObject<Item> WALL_BLOCK = ITEMS.register("wall_block", () -> new BlockItem(BlockInit.WALL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> WALL_TRIM_TEXTURE = ITEMS.register("wall_trim_texture", () -> new BlockItem(BlockInit.WALL_TRIM_TEXTURE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUG1 = ITEMS.register("rug1", () -> new BlockItem(BlockInit.RUG1.get(), new Item.Properties()));
    public static final RegistryObject<Item> RUG2 = ITEMS.register("rug2", () -> new BlockItem(BlockInit.RUG2.get(), new Item.Properties()));
    public static final RegistryObject<Item> ZERO_FLOOR = ITEMS.register("zero_floor", () -> new BlockItem(BlockInit.ZERO_FLOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ZERO_WALL = ITEMS.register("zero_wall", () -> new BlockItem(BlockInit.ZERO_WALL.get(), new Item.Properties()));
    public static final RegistryObject<Item> ZERO_CEILING = ITEMS.register("zero_ceiling", () -> new BlockItem(BlockInit.ZERO_CEILING.get(), new Item.Properties()));
    public static final RegistryObject<Item> ONE_FLOOR = ITEMS.register("one_floor", () -> new BlockItem(BlockInit.ONE_FLOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ONE_WALL = ITEMS.register("one_wall", () -> new BlockItem(BlockInit.ONE_WALL.get(), new Item.Properties()));
    public static final RegistryObject<Item> POOLS_FLOOR = ITEMS.register("pools_floor", () -> new BlockItem(BlockInit.POOLS_FLOOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> POOLS_WALL = ITEMS.register("pools_wall", () -> new BlockItem(BlockInit.POOLS_WALL.get(), new Item.Properties()));

    public static final RegistryObject<Item> MUSIC_DISC_KINGS_CURFEW = ITEMS.register("music_disc_kings_curfew", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_KINGS_CURFEW, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_MENU = ITEMS.register("music_disc_menu", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_MENU, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_ROW_YOUR_BOAT = ITEMS.register("music_disc_row_your_boat", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_ROW_YOUR_BOAT, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_SNACKROOSIC = ITEMS.register("music_disc_snackroosic", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_SNACKROOSIC, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_ELEVATOR_WONT_KILL_YOU = ITEMS.register("music_disc_elevator_wont_kill_you", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_ELEVATOR_WONT_KILL_YOU, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_YOU_DAY = ITEMS.register("music_disc_you_day", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_YOU_DAY, new Item.Properties().stacksTo(1), 2400));
    public static final RegistryObject<Item> MUSIC_DISC_YOU_DAY___INSTRUMENTAL_MIX = ITEMS.register("music_disc_you_day___instrumental_mix", () -> new net.minecraft.world.item.RecordItem(15, com.litvin.backrooms.init.SoundInit.MUSIC_DISC_YOU_DAY___INSTRUMENTAL_MIX, new Item.Properties().stacksTo(1), 2400));

    public static final RegistryObject<Item> MARKER = ITEMS.register("marker", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOTTLES = ITEMS.register("bottles", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PORTAL_BLOCK = ITEMS.register("portal_block", () -> new net.minecraft.world.item.BlockItem(BlockInit.PORTAL_BLOCK.get(), new net.minecraft.world.item.Item.Properties()));
}




