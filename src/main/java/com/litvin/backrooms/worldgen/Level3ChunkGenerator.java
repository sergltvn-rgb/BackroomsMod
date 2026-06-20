package com.litvin.backrooms.worldgen;

import com.litvin.backrooms.init.BlockInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
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

/**
 * Level 3 — "Бетонные каналы"
 * Индустриальные каналы без мебели, только бетон и трубы.
 * Зоны: CANAL (длинные тоннели), CISTERN (большие цистерны), DRAIN (решётки)
 */
public class Level3ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level3ChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(Level3ChunkGenerator::new))
    );

    private final long fixedSeed = 111222333L;

    private static final int ZONE_CANAL = 0;     // Длинные прямые тоннели
    private static final int ZONE_CISTERN = 1;   // Большие цистерны (круглые комнаты)
    private static final int ZONE_DRAIN = 2;     // Решётки, дренаж

    public Level3ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private long hash(long x, long z, long seed) {
        long h = seed + x * 312341L + z * 439123L;
        h ^= (h >>> 20) ^ (h >>> 12);
        return Math.abs(h ^ (h >>> 7) ^ (h >>> 4));
    }

    private int getZoneType(long megaX, long megaZ) {
        if (megaX == 0 && megaZ == 0) return ZONE_CISTERN;
        long h = hash(megaX, megaZ, fixedSeed + 7000000L);
        int v = (int) (h % 100);
        if (v < 45) return ZONE_CANAL;    // 45%
        if (v < 75) return ZONE_CISTERN;  // 30%
        return ZONE_DRAIN;                 // 25%
    }

    // ==================== ГРАНИЦЫ ВЫСОТ (заделка стыков) ====================

    private int getMegaCeilingY(long megaX, long megaZ) {
        return (getZoneType(megaX, megaZ) == ZONE_CISTERN) ? 70 : 68;
    }

    // Если столб на краю мега-ячейки (56×56) и сосед выше — вернуть макс. высоту; иначе -1.
    private int getBorderWallTopY(long megaX, long megaZ, int mx, int mz, int thisCeilingY) {
        int maxNeighbor = thisCeilingY;
        if (mx == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX - 1, megaZ));
        if (mx == 55) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX + 1, megaZ));
        if (mz == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ - 1));
        if (mz == 55) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ + 1));
        return (maxNeighbor > thisCeilingY) ? maxNeighbor : -1;
    }

    // Проёмы в пограничной стене: 2 блока шириной каждые 16 блоков.
    private boolean isBorderDoorway(int worldX, int worldZ, int mx, int mz) {
        boolean door = false;
        if ((mx == 0 || mx == 55) && Math.floorMod(worldZ, 16) < 2) door = true;
        if ((mz == 0 || mz == 55) && Math.floorMod(worldX, 16) < 2) door = true;
        return door;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        BlockState wall = Blocks.BRICKS.defaultBlockState();
        BlockState crackedWall = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState mossyWall = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        BlockState floor = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();
        BlockState pipe = BlockInit.STEEL_PIPE.get().defaultBlockState();
        BlockState ceiling = Blocks.STONE_SLAB.defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = Blocks.WATER.defaultBlockState();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            int worldX = minX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = minZ + z;

                long megaX = Math.floorDiv(worldX, 56);
                long megaZ = Math.floorDiv(worldZ, 56);
                int zoneType = getZoneType(megaX, megaZ);

                int mx = Math.floorMod(worldX, 56);
                int mz = Math.floorMod(worldZ, 56);

                boolean isSafeRoom = (megaX == 0 && megaZ == 0 && mx < 16 && mz < 16);
                boolean isWall;

                if (isSafeRoom) {
                    if (mx == 0 || mz == 0 || mx == 15 || mz == 15) {
                        boolean isDoor = false;
                        if ((mx == 0 || mx == 15) && (mz >= 6 && mz <= 9)) isDoor = true;
                        if ((mz == 0 || mz == 15) && (mx >= 6 && mx <= 9)) isDoor = true;
                        isWall = !isDoor;
                    } else {
                        isWall = false;
                    }
                } else {
                    isWall = isWallForZone(worldX, worldZ, zoneType);
                }

                // Для цистерн — высокий потолок
                int ceilingY = (zoneType == ZONE_CISTERN && !isSafeRoom) ? 70 : 68;
                int wallMaxY = ceilingY - 1;

                // Пограничная стена: заделка перепадов высот между мега-ячейками
                int borderTopY = isSafeRoom ? -1 : getBorderWallTopY(megaX, megaZ, mx, mz, ceilingY);
                boolean isBorderWall = borderTopY > 0;
                boolean borderDoor = isBorderWall && isBorderDoorway(worldX, worldZ, mx, mz);
                if (isBorderWall) {
                    ceilingY = borderTopY;
                    wallMaxY = ceilingY - 1;
                }

                for (int y = chunkAccess.getMinBuildHeight(); y < chunkAccess.getMaxBuildHeight(); y++) {
                    pos.set(worldX, y, worldZ);

                    if (y < 63) {
                        chunkAccess.setBlockState(pos, y == chunkAccess.getMinBuildHeight() ? bedrock : stone, false);
                    } else if (y == 63) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    } else if (y == 64) {
                        // Пол: решётки в DRAIN зоне, камень в остальных
                        if (zoneType == ZONE_DRAIN && !isSafeRoom) {
                            long floorHash = hash(worldX, worldZ, fixedSeed + 20000L);
                            chunkAccess.setBlockState(pos, (floorHash % 4 == 0) ? ironBars : floor, false);
                        } else {
                            chunkAccess.setBlockState(pos, floor, false);
                        }
                    } else if (y >= 65 && y <= wallMaxY) {
                        if (isBorderWall) {
                            // Пограничная стена: сплошной кирпич, редкие проёмы
                            if (borderDoor && y <= 67) {
                                chunkAccess.setBlockState(pos, air, false);
                            } else {
                                chunkAccess.setBlockState(pos, wall, false);
                            }
                        } else if (isWall) {
                            long wallHash = hash(worldX, worldZ, y + fixedSeed + 3000L);
                            BlockState wallBlock;
                            if (zoneType == ZONE_CISTERN) {
                                wallBlock = (y == 65 && wallHash % 3 == 0) ? mossyWall : wall;
                            } else if (zoneType == ZONE_DRAIN) {
                                wallBlock = (wallHash % 5 == 0) ? crackedWall : wall;
                            } else {
                                wallBlock = wall;
                            }
                            chunkAccess.setBlockState(pos, wallBlock, false);
                        } else {
                            // Трубы на верхнем уровне в CANAL
                            if (y == wallMaxY && zoneType == ZONE_CANAL && !isSafeRoom) {
                                long pipeHash = hash(worldX, worldZ, fixedSeed + 50000L);
                                if (Math.floorMod(worldX, 3) == 0 && pipeHash % 4 == 0) {
                                    chunkAccess.setBlockState(pos, pipe, false);
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            }
                            // Вода в цистернах на нижнем уровне
                            else if (y == 65 && zoneType == ZONE_CISTERN && !isSafeRoom) {
                                long waterHash = hash(worldX, worldZ, fixedSeed + 60000L);
                                chunkAccess.setBlockState(pos, (waterHash % 4 == 0) ? water : air, false);
                            } else {
                                chunkAccess.setBlockState(pos, air, false);
                            }
                        }
                    } else if (y == ceilingY) {
                        // Потолок с трубами
                        boolean hasLight = false;
                        if (isSafeRoom) {
                            hasLight = (Math.floorMod(worldX, 5) == 2 && Math.floorMod(worldZ, 5) == 2);
                        } else if (zoneType == ZONE_CISTERN) {
                            int lx = Math.floorMod(worldX, 14);
                            int lz = Math.floorMod(worldZ, 14);
                            hasLight = (lx == 7 && lz == 7);
                        } else if (zoneType == ZONE_CANAL) {
                            long lh = hash(Math.floorDiv(worldX, 10), Math.floorDiv(worldZ, 10), fixedSeed + 90000L);
                            hasLight = (lh % 4 == 0) && (Math.floorMod(worldX, 10) == 5) && (Math.floorMod(worldZ, 10) == 5);
                        } else {
                            long lh = hash(Math.floorDiv(worldX, 8), Math.floorDiv(worldZ, 8), fixedSeed + 91000L);
                            hasLight = (lh % 5 == 0) && (Math.floorMod(worldX, 8) == 4) && (Math.floorMod(worldZ, 8) == 4);
                        }

                        if (hasLight && !isBorderWall) {
                            chunkAccess.setBlockState(pos, pipe, false);
                        } else {
                            chunkAccess.setBlockState(pos, ceiling, false);
                        }
                    } else if (y == ceilingY + 1) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    } else {
                        chunkAccess.setBlockState(pos, stone, false);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    private boolean isWallForZone(int worldX, int worldZ, int zoneType) {
        switch (zoneType) {
            case ZONE_CANAL: return canalWall(worldX, worldZ);
            case ZONE_CISTERN: return cisternWall(worldX, worldZ);
            case ZONE_DRAIN: return drainWall(worldX, worldZ);
            default: return false;
        }
    }

    // CANAL: длинные узкие каналы, ширина варьируется по регионам
    private boolean canalWall(int worldX, int worldZ) {
        long regionX = Math.floorDiv(worldX, 56);
        long regionZ = Math.floorDiv(worldZ, 56);
        long styleHash = hash(regionX, regionZ, fixedSeed + 1300L);
        int cellX = 4 + (int) (styleHash % 3); // ширина канала 4–6 блоков
        int cellZ = 10;
        long cx = Math.floorDiv(worldX, cellX);
        int lx = Math.floorMod(worldX, cellX);
        long cz = Math.floorDiv(worldZ, cellZ);
        int lz = Math.floorMod(worldZ, cellZ);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 100L);
            int type = (int) (h % 4);
            if (type < 2) wallX = true;
            else wallX = (lz < 4 || lz > 5);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 200L);
            int type = (int) (h % 4);
            if (type < 2) wallZ = true;
            else wallZ = (lx != 2);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // CISTERN: 14×14 ячейки — большие цистерны
    private boolean cisternWall(int worldX, int worldZ) {
        int cellSize = 14;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 300L);
            int type = (int) (h % 5);
            if (type < 1) wallX = true;
            else if (type < 3) wallX = (lz < 5 || lz > 8); // 4-блочный проём
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 400L);
            int type = (int) (h % 5);
            if (type < 1) wallZ = true;
            else if (type < 3) wallZ = (lx < 5 || lx > 8);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }

        // Колонны в цистернах — узор варьируется по регионам 56×56
        if (!wallX && !wallZ) {
            long regionX = Math.floorDiv(worldX, 56);
            long regionZ = Math.floorDiv(worldZ, 56);
            long colStyle = hash(regionX, regionZ, fixedSeed + 1200L);
            switch ((int) (colStyle % 3)) {
                case 0: // одиночная массивная колонна 2×2
                    if ((lx == 6 || lx == 7) && (lz == 6 || lz == 7)) return true;
                    break;
                case 1: // четыре колонны по углам зала
                    if ((lx == 3 || lx == 10) && (lz == 3 || lz == 10)) return true;
                    break;
                default: // ряд колонн по центральной оси
                    if (lz == 7 && (lx == 3 || lx == 7 || lx == 11)) return true;
                    break;
            }
        }

        return wallX || wallZ;
    }

    // DRAIN: 8×8 ячейки — обычный дренаж
    private boolean drainWall(int worldX, int worldZ) {
        int cellSize = 8;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 500L);
            int type = (int) (h % 5);
            if (type < 2) wallX = true;
            else if (type < 4) wallX = (lz < 3 || lz > 4);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 600L);
            int type = (int) (h % 5);
            if (type < 2) wallZ = true;
            else if (type < 4) wallZ = (lx < 3 || lx > 4);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {}

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving carvingStep) {}

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {}

    @Override
    public int getGenDepth() { return 384; }

    @Override
    public int getMinY() { return -64; }

    @Override
    public int getSeaLevel() { return 63; }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) { return 65; }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
}
