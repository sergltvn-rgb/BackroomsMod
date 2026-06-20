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

public class Level1ChunkGenerator extends ChunkGenerator {

    public static final Codec<Level1ChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                net.minecraft.world.level.biome.BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
        ).apply(instance, instance.stable(Level1ChunkGenerator::new));
    });

    public Level1ChunkGenerator(net.minecraft.world.level.biome.BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {
        // Obsolete in newer forge usually, handled via other methods, but keeping for compat
    }
    
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
        
        BlockState floor = BlockInit.ONE_FLOOR.get().defaultBlockState();
        BlockState wall = BlockInit.CONCRETE1.get().defaultBlockState();
        BlockState ceiling = BlockInit.CONCRETE5.get().defaultBlockState();
        BlockState pillar = BlockInit.CONCRETE2.get().defaultBlockState();

        Random random = new Random(chunkPos.toLong());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkPos.getMinBlockX() + x;
                int worldZ = chunkPos.getMinBlockZ() + z;

                // floor
                pos.set(x, 0, z);
                chunkAccess.setBlockState(pos, floor, false);
                
                // ceiling
                pos.set(x, 10, z);
                chunkAccess.setBlockState(pos, ceiling, false);

                // walls based on coarse grid
                boolean isWall = (worldX % 30 < 2 || worldZ % 30 < 2) && random.nextFloat() > 0.4f;
                boolean isPillar = (worldX % 15 == 0 && worldZ % 15 == 0);

                for (int y = 1; y < 10; y++) {
                    pos.set(x, y, z);
                    if (isPillar) {
                        chunkAccess.setBlockState(pos, pillar, false);
                    } else if (isWall) {
                        chunkAccess.setBlockState(pos, wall, false);
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
        return 10;
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel, RandomState pRandom) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(java.util.List<String> pInfo, RandomState pRandom, BlockPos pPos) {}
}

