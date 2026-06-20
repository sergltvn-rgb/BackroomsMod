package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BackroomsMod.MODID);

    // Обои (стены)
    public static final RegistryObject<Block> WALLPAPER = BLOCKS.register("wallpaper",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)));

    // Мокрый ковер (пол)
    public static final RegistryObject<Block> CARPET = BLOCKS.register("carpet",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(1.5F, 2.0F)
                    .sound(SoundType.WOOL)));

    // Потолочная плитка (потолок)
    public static final RegistryObject<Block> CEILING_TILE = BLOCKS.register("ceiling_tile",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.STONE)));

    // Светящаяся лампа (потолочное освещение с гудением) - яркость снижена до 4 (гнетущая атмосфера)
    public static final RegistryObject<Block> FLUORESCENT_LIGHT = BLOCKS.register("fluorescent_light",
            () -> new FluorescentLightBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(1.0F, 1.0F)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 4)));

    // Стальная труба (декорации)
    public static final RegistryObject<Block> STEEL_PIPE = BLOCKS.register("steel_pipe",
            () -> new com.litvin.backrooms.block.PipeBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL)));

    // Багнутый блок (телепортер)
    public static final RegistryObject<Block> GLITCHED_BLOCK = BLOCKS.register("glitched_block",
            () -> new GlitchedBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));

    public static final RegistryObject<Block> LOCKER = BLOCKS.register("locker",
            () -> new LockerBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    // Мебель
    public static final RegistryObject<Block> OFFICE_CHAIR = BLOCKS.register("office_chair",
            () -> new FurnitureBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), Block.box(2, 0, 2, 14, 16, 14)));

    public static final RegistryObject<Block> OFFICE_TABLE = BLOCKS.register("office_table",
            () -> new FurnitureBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), Block.box(0, 0, 0, 16, 16, 16)));

    // Новые блоки мебели и предметов интерьера (v1.4)
    public static final RegistryObject<Block> CARDBOARD_BOX = BLOCKS.register("cardboard_box",
            () -> new CardboardBoxBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion().sound(SoundType.WOOD), Block.box(1, 0, 1, 15, 14, 15)));

    public static final RegistryObject<Block> FILE_CABINET = BLOCKS.register("file_cabinet",
            () -> new FileCabinetBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().sound(SoundType.METAL), Block.box(1, 0, 1, 15, 16, 15)));

    public static final RegistryObject<Block> CEILING_VENT = BLOCKS.register("ceiling_vent",
            () -> new CeilingVentBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().sound(SoundType.METAL), Block.box(0, 14, 0, 16, 16, 16)));

    public static final RegistryObject<Block> OFFICE_PARTITION = BLOCKS.register("office_partition",
            () -> new OfficePartitionBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion().sound(SoundType.WOOD), Block.box(0, 0, 7, 16, 16, 9)));

    public static final RegistryObject<Block> WATER_COOLER = BLOCKS.register("water_cooler",
            () -> new WaterCoolerBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion().sound(SoundType.GLASS), Block.box(3, 0, 3, 13, 16, 13)));

    // Новые блоки Уровня 0
    public static final RegistryObject<Block> FLICKERING_LIGHT = BLOCKS.register("flickering_light",
            () -> new FlickeringLightBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .lightLevel(state -> state.getValue(FlickeringLightBlock.LIT) && !state.getValue(FlickeringLightBlock.BROKEN) ? 4 : 0)
                    .sound(SoundType.GLASS)
                    .randomTicks()));

    public static final RegistryObject<Block> BROKEN_LIGHT = BLOCKS.register("broken_light",
            () -> new BrokenLightBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));

    public static final RegistryObject<Block> DRAWN_WALLPAPER = BLOCKS.register("drawn_wallpaper",
            () -> new DrawnWallpaperBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    public static final RegistryObject<Block> CRATE = BLOCKS.register("crate",
            () -> new CrateBlock(BlockBehaviour.Properties.copy(Blocks.BARREL)));

    // Невидимый блок света для фонарика
    public static final RegistryObject<Block> FLASHLIGHT_LIGHT = BLOCKS.register("flashlight_light",
            () -> new FlashlightLightBlock(BlockBehaviour.Properties.copy(Blocks.AIR)
                    .lightLevel(state -> 15)
                    .noCollission()
                    .noLootTable()
                    .replaceable()
                    .instabreak()));

    // Блоки Уровня 37 (Poolrooms)
    public static final RegistryObject<Block> POOL_TILES = BLOCKS.register("pool_tiles",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK).sound(SoundType.STONE)));

    public static final RegistryObject<Block> CONCRETE1 = BLOCKS.register("concrete1", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CONCRETE2 = BLOCKS.register("concrete2", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> CONCRETE5 = BLOCKS.register("concrete5", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RUST2 = BLOCKS.register("rust2", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RUST3 = BLOCKS.register("rust3", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> METAL1 = BLOCKS.register("metal1", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> METAL2 = BLOCKS.register("metal2", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> WALL_BLOCK = BLOCKS.register("wall_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> WALL_TRIM_TEXTURE = BLOCKS.register("wall_trim_texture", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RUG1 = BLOCKS.register("rug1", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> RUG2 = BLOCKS.register("rug2", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ZERO_FLOOR = BLOCKS.register("zero_floor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ZERO_WALL = BLOCKS.register("zero_wall", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ZERO_CEILING = BLOCKS.register("zero_ceiling", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ONE_FLOOR = BLOCKS.register("one_floor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> ONE_WALL = BLOCKS.register("one_wall", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> POOLS_FLOOR = BLOCKS.register("pools_floor", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> POOLS_WALL = BLOCKS.register("pools_wall", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> PORTAL_BLOCK = BLOCKS.register("portal_block", () -> new com.litvin.backrooms.block.PortalBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_DOOR)));
    public static final RegistryObject<Block> POWER_POLE = BLOCKS.register("power_pole", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().sound(SoundType.METAL)));
    public static final RegistryObject<Block> POWER_POLE_TOP = BLOCKS.register("power_pole_top", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion().sound(SoundType.METAL)));
}





