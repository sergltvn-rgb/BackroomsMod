package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.worldgen.BackroomsChunkGenerator;
import com.litvin.backrooms.worldgen.Level1ChunkGenerator;
import com.litvin.backrooms.worldgen.Level2ChunkGenerator;
import com.litvin.backrooms.worldgen.Level3ChunkGenerator;
import com.litvin.backrooms.worldgen.Level4ChunkGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GeneratorInit {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, BackroomsMod.MODID);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> BACKROOMS_GENERATOR = CHUNK_GENERATORS.register("backrooms",
            () -> BackroomsChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_1_GENERATOR = CHUNK_GENERATORS.register("level_1",
            () -> Level1ChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_2_GENERATOR = CHUNK_GENERATORS.register("level_2",
            () -> Level2ChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_3_GENERATOR = CHUNK_GENERATORS.register("level_3",
            () -> Level3ChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_4_GENERATOR = CHUNK_GENERATORS.register("level_4",
            () -> Level4ChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends ChunkGenerator>> LEVEL_37_GENERATOR = CHUNK_GENERATORS.register("level_37",
            () -> com.litvin.backrooms.worldgen.Level37ChunkGenerator.CODEC);

    public static final RegistryObject<Codec<? extends net.minecraft.world.level.chunk.ChunkGenerator>> LEVEL_1_CHUNK_GENERATOR = CHUNK_GENERATORS.register("level_1_chunk_generator", () -> com.litvin.backrooms.worldgen.dimension.Level1ChunkGenerator.CODEC);
    public static final RegistryObject<Codec<? extends net.minecraft.world.level.chunk.ChunkGenerator>> LEVEL_2_CHUNK_GENERATOR = CHUNK_GENERATORS.register("level_2_chunk_generator", () -> com.litvin.backrooms.worldgen.dimension.Level2ChunkGenerator.CODEC);
}

