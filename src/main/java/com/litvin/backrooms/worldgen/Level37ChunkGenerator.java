package com.litvin.backrooms.worldgen;

import com.litvin.backrooms.init.BlockInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level37ChunkGenerator extends ChunkGenerator {

    public static final Codec<Level37ChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(Level37ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(Level37ChunkGenerator::new))
    );

    private final BlockState tileBlock;
    private final BlockState waterBlock;
    private final BlockState lightBlock;

    public Level37ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.tileBlock = BlockInit.POOL_TILES.get().defaultBlockState();
        this.waterBlock = Blocks.WATER.defaultBlockState();
        this.lightBlock = BlockInit.FLUORESCENT_LIGHT.get().defaultBlockState();
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState randomState, ChunkAccess chunk) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getGenDepth() {
        return 256;
    }

    @Override
    public int getSeaLevel() {
        return 4;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structures, ChunkAccess chunk, GenerationStep.Carving step) {
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structures, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            int startX = chunk.getPos().getMinBlockX();
            int startZ = chunk.getPos().getMinBlockZ();
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = startX + x;
                    int worldZ = startZ + z;
                    
                    double noise1 = Math.sin(worldX * 0.1) * Math.cos(worldZ * 0.1);
                    double noise2 = Math.sin(worldX * 0.05 + 10) * Math.cos(worldZ * 0.05 + 10);
                    
                    int floorY = (noise1 > 0.5) ? 1 : 0;
                    if (noise2 > 0.8) floorY = 2; // Shallow water
                    if (noise2 < -0.5) floorY = -2; // Deep water
                    
                    // Ceiling
                    chunk.setBlockState(new BlockPos(x, 8, z), tileBlock, false);
                    
                    // Floor
                    for (int y = -3; y <= floorY; y++) {
                        chunk.setBlockState(new BlockPos(x, y, z), tileBlock, false);
                    }
                    
                    // Water
                    for (int y = floorY + 1; y <= 3; y++) {
                        chunk.setBlockState(new BlockPos(x, y, z), waterBlock, false);
                    }
                    
                    // Pillars
                    if (worldX % 8 == 0 && worldZ % 8 == 0) {
                        for (int y = floorY + 1; y < 8; y++) {
                            chunk.setBlockState(new BlockPos(x, y, z), tileBlock, false);
                        }
                    }
                    
                    // Walls
                    if (noise1 > 0.8 || noise2 > 0.95) {
                        for (int y = floorY + 1; y < 8; y++) {
                            chunk.setBlockState(new BlockPos(x, y, z), tileBlock, false);
                        }
                    }
                    
                    // Lights
                    if (worldX % 4 == 0 && worldZ % 4 == 0 && Math.abs(noise1) < 0.5) {
                        chunk.setBlockState(new BlockPos(x, 7, z), lightBlock, false);
                    }
                }
            }
            return chunk;
        }, executor);
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState randomState) {
        return 8;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        BlockState[] states = new BlockState[9];
        for (int i = 0; i < 9; i++) {
            states[i] = i == 8 || i == 0 ? tileBlock : Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(0, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
    }
}
