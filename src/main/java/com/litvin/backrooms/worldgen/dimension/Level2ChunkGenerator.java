package com.litvin.backrooms.worldgen.dimension;

import com.litvin.backrooms.init.BlockInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.StructureManager;
import net.minecraft.server.level.WorldGenRegion;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.Random;

public class Level2ChunkGenerator extends ChunkGenerator {

    public static final Codec<Level2ChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                net.minecraft.world.level.biome.BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
        ).apply(instance, instance.stable(Level2ChunkGenerator::new));
    });

    public Level2ChunkGenerator(net.minecraft.world.level.biome.BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {}

    @Override
    public void applyCarvers(WorldGenRegion pRegion, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion pRegion) {}

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        
        BlockState rust = BlockInit.RUST2.get().defaultBlockState();
        BlockState rustDark = BlockInit.RUST3.get().defaultBlockState();
        BlockState pipe = BlockInit.STEEL_PIPE.get().defaultBlockState();

        Random random = new Random(chunkPos.toLong());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;

                // Floor and ceiling
                pos.set(x, 0, z);
                chunkAccess.setBlockState(pos, random.nextBoolean() ? rust : rustDark, false);
                pos.set(x, 5, z);
                chunkAccess.setBlockState(pos, rustDark, false);

                // Create winding narrow paths using sin/cos
                int pathZ = (int) (Math.sin(worldX * 0.1) * 5) + 8;
                boolean isPath = Math.abs((worldZ % 16) - pathZ) <= 1;
                
                // Also create perpendicular paths
                int pathX = (int) (Math.cos(worldZ * 0.1) * 5) + 8;
                isPath |= Math.abs((worldX % 16) - pathX) <= 1;

                for (int y = 1; y < 5; y++) {
                    pos.set(x, y, z);
                    if (!isPath) {
                        chunkAccess.setBlockState(pos, rust, false);
                    } else if (random.nextFloat() < 0.05f) { // random pipes on walls of path
                         chunkAccess.setBlockState(pos, pipe, false);
                    }
                }
                
                // portal
                if (x == 8 && z == 8 && random.nextFloat() < 0.05f) {
                     chunkAccess.setBlockState(new BlockPos(x, 1, z), BlockInit.PORTAL_BLOCK.get().defaultBlockState(), false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
        return 5;
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel, RandomState pRandom) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(java.util.List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
}

