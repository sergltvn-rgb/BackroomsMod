package com.litvin.backrooms.worldgen;

import com.litvin.backrooms.block.FurnitureBlock;
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

/**
 * Level 1 — "Склады и хранилища"
 * Просторные помещения с высокими потолками, стеллажами и ящиками.
 * Зоны: WAREHOUSE (высокие потолки), STORAGE (узкие ряды), NARROW_PASSAGES (сжатые коридоры)
 */
public class Level1ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level1ChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(Level1ChunkGenerator::new))
    );

    private final long fixedSeed = 987654321L;

    // Типы зон (48×48 мега-ячейки)
    private static final int ZONE_WAREHOUSE = 0;      // Огромные залы с колоннами, высокий потолок
    private static final int ZONE_STORAGE = 1;        // Узкие ряды со стеллажами (ящиками)
    private static final int ZONE_NARROW = 2;         // Сжатые коридоры, тёмные

    public Level1ChunkGenerator(BiomeSource biomeSource) {
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
        if (megaX == 0 && megaZ == 0) return ZONE_WAREHOUSE;
        long h = hash(megaX, megaZ, fixedSeed + 7000000L);
        int v = (int) (h % 100);
        if (v < 40) return ZONE_WAREHOUSE;   // 40%
        if (v < 75) return ZONE_STORAGE;     // 35%
        return ZONE_NARROW;                   // 25%
    }

    // ==================== ГРАНИЦЫ ВЫСОТ (заделка стыков) ====================

    private int getMegaCeilingY(long megaX, long megaZ) {
        return (getZoneType(megaX, megaZ) == ZONE_WAREHOUSE) ? 71 : 68;
    }

    // Если столб на краю мега-ячейки (48×48) и сосед выше — вернуть макс. высоту; иначе -1.
    private int getBorderWallTopY(long megaX, long megaZ, int mx, int mz, int thisCeilingY) {
        int maxNeighbor = thisCeilingY;
        if (mx == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX - 1, megaZ));
        if (mx == 47) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX + 1, megaZ));
        if (mz == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ - 1));
        if (mz == 47) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ + 1));
        return (maxNeighbor > thisCeilingY) ? maxNeighbor : -1;
    }

    // Проёмы в пограничной стене: 2 блока шириной каждые 16 блоков.
    private boolean isBorderDoorway(int worldX, int worldZ, int mx, int mz) {
        boolean door = false;
        if ((mx == 0 || mx == 47) && Math.floorMod(worldZ, 16) < 2) door = true;
        if ((mz == 0 || mz == 47) && Math.floorMod(worldX, 16) < 2) door = true;
        return door;
    }

    private boolean isWallForZone(int worldX, int worldZ, int zoneType) {
        switch (zoneType) {
            case ZONE_WAREHOUSE: return warehouseWall(worldX, worldZ);
            case ZONE_STORAGE: return storageWall(worldX, worldZ);
            case ZONE_NARROW: return narrowWall(worldX, worldZ);
            default: return false;
        }
    }

    // WAREHOUSE: стиль колонн варьируется по регионам 48×48
    private boolean warehouseWall(int worldX, int worldZ) {
        long regionX = Math.floorDiv(worldX, 48);
        long regionZ = Math.floorDiv(worldZ, 48);
        long styleHash = hash(regionX, regionZ, fixedSeed + 1100L);
        int style = (int) (styleHash % 3);

        int spacing;   // расстояние между колоннами
        int thickness; // толщина колонны
        switch (style) {
            case 0:  spacing = 8;  thickness = 1; break; // частые тонкие колонны
            case 1:  spacing = 12; thickness = 2; break; // классические массивные
            default: spacing = 16; thickness = 2; break; // редкие, широкий простор
        }
        int cx = Math.floorMod(worldX, spacing);
        int cz = Math.floorMod(worldZ, spacing);
        return cx < thickness && cz < thickness;
    }

    // STORAGE: 4×12 ячейки, длинные ряды стеллажей
    private boolean storageWall(int worldX, int worldZ) {
        int cellX = 4; // узкие ряды
        int cellZ = 12; // длинные ряды

        int lx = Math.floorMod(worldX, cellX);
        long cx = Math.floorDiv(worldX, cellX);
        int lz = Math.floorMod(worldZ, cellZ);
        long cz = Math.floorDiv(worldZ, cellZ);

        // Стены по X каждые 4 блока (создают ряды)
        if (lx == 0) {
            long h = hash(cx, cz, fixedSeed + 100L);
            if (h % 3 != 0) { // 66% стена
                return (lz < 4 || lz > 7); // проём в центре
            }
        }

        // Поперечные стены редкие — каждые 12 блоков
        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 200L);
            if (h % 4 == 0) { // 25% поперечная стена
                return (lx != 2); // узкий проход
            }
        }

        return false;
    }

    // NARROW: 4×4 ячейки, тесный лабиринт
    private boolean narrowWall(int worldX, int worldZ) {
        int cellSize = 4;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 300L);
            int type = (int) (h % 4);
            if (type < 2) wallX = true;
            else wallX = (lz != 2); // проём 1 блок
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 400L);
            int type = (int) (h % 4);
            if (type < 2) wallZ = true;
            else wallZ = (lx != 2);
        }

        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        // Блоки Level 1 — индустриально-складской стиль
        BlockState wall = Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
        BlockState floor = Blocks.GRAY_CONCRETE.defaultBlockState();
        BlockState ceiling = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState lightBlock = Blocks.LANTERN.defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState pipe = BlockInit.STEEL_PIPE.get().defaultBlockState();
        BlockState crackedBricks = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState cobweb = Blocks.COBWEB.defaultBlockState();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            int worldX = minX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = minZ + z;

                long megaX = Math.floorDiv(worldX, 48);
                long megaZ = Math.floorDiv(worldZ, 48);
                int zoneType = getZoneType(megaX, megaZ);

                int mx = Math.floorMod(worldX, 48);
                int mz = Math.floorMod(worldZ, 48);

                // Безопасная комната
                boolean isSafeRoom = false;
                boolean isWall;

                if (megaX == 0 && megaZ == 0 && mx < 20 && mz < 20) {
                    isSafeRoom = true;
                    if (mx == 0 || mz == 0 || mx == 19 || mz == 19) {
                        boolean isDoor = false;
                        if ((mx == 0 || mx == 19) && (mz >= 8 && mz <= 11)) isDoor = true;
                        if ((mz == 0 || mz == 19) && (mx >= 8 && mx <= 11)) isDoor = true;
                        isWall = !isDoor;
                    } else {
                        isWall = false;
                    }
                } else {
                    isWall = isWallForZone(worldX, worldZ, zoneType);
                }

                // Высота потолка
                int ceilingY = (zoneType == ZONE_WAREHOUSE && !isSafeRoom) ? 71 : 68;
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
                        chunkAccess.setBlockState(pos, floor, false);
                    } else if (y >= 65 && y <= wallMaxY) {
                        if (isBorderWall) {
                            // Пограничная стена: сплошной бетон, редкие проёмы
                            if (borderDoor && y <= 67) {
                                chunkAccess.setBlockState(pos, air, false);
                            } else {
                                chunkAccess.setBlockState(pos, wall, false);
                            }
                        } else if (isWall) {
                            long wallHash = hash(worldX, worldZ, y + fixedSeed + 3000L);
                            if (zoneType == ZONE_NARROW && wallHash % 8 == 0) {
                                chunkAccess.setBlockState(pos, crackedBricks, false);
                            } else {
                                chunkAccess.setBlockState(pos, wall, false);
                            }
                        } else {
                            // Стеллажи в STORAGE зоне (ящики на полу)
                            if (y == 65 && zoneType == ZONE_STORAGE && !isSafeRoom) {
                                long crateHash = hash(worldX, worldZ, fixedSeed + 40000L);
                                int bucket = (int) (crateHash % 12);
                                Direction dir = Direction.from2DDataValue((int) (crateHash % 4));
                                if (bucket == 0) {
                                    // Деревянный ящик — полный блок
                                    chunkAccess.setBlockState(pos, BlockInit.CRATE.get().defaultBlockState(), false);
                                } else if (bucket == 1) {
                                    int glitch = (crateHash % 8 == 0) ? 1 : 0;
                                    chunkAccess.setBlockState(pos, BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                                            .setValue(FurnitureBlock.FACING, dir)
                                            .setValue(FurnitureBlock.GLITCH, glitch), false);
                                } else if (bucket == 2) {
                                    chunkAccess.setBlockState(pos, BlockInit.FILE_CABINET.get().defaultBlockState()
                                            .setValue(FurnitureBlock.FACING, dir)
                                            .setValue(FurnitureBlock.GLITCH, 0), false);
                                } else if (bucket == 3 && crateHash % 48 < 4) {
                                    // Редкий кулер среди стеллажей
                                    chunkAccess.setBlockState(pos, BlockInit.WATER_COOLER.get().defaultBlockState()
                                            .setValue(FurnitureBlock.FACING, dir)
                                            .setValue(FurnitureBlock.GLITCH, 0), false);
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            }
                            // Трубы под потолком в WAREHOUSE
                            else if (y == wallMaxY && zoneType == ZONE_WAREHOUSE) {
                                long pipeHash = hash(worldX, worldZ, fixedSeed + 50000L);
                                if (Math.floorMod(worldX, 4) == 0 && pipeHash % 5 == 0) {
                                    chunkAccess.setBlockState(pos, pipe, false);
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            }
                            // Паутина в NARROW
                            else if (y == 66 && zoneType == ZONE_NARROW) {
                                int lx = Math.floorMod(worldX, 4);
                                int lz = Math.floorMod(worldZ, 4);
                                if (lx == 1 && lz == 1) {
                                    long cwHash = hash(worldX, worldZ, fixedSeed + 60000L);
                                    if (cwHash % 5 == 0) {
                                        chunkAccess.setBlockState(pos, cobweb, false);
                                    } else {
                                        chunkAccess.setBlockState(pos, air, false);
                                    }
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            } else {
                                chunkAccess.setBlockState(pos, air, false);
                            }
                        }
                    } else if (y == ceilingY) {
                        // Освещение
                        boolean hasLight = false;
                        if (isSafeRoom) {
                            int smx = Math.floorMod(worldX, 6);
                            int smz = Math.floorMod(worldZ, 6);
                            hasLight = (smx >= 2 && smx <= 3 && smz >= 2 && smz <= 3);
                        } else if (zoneType == ZONE_WAREHOUSE) {
                            int lx = Math.floorMod(worldX, 12);
                            int lz = Math.floorMod(worldZ, 12);
                            hasLight = (lx >= 5 && lx <= 6 && lz >= 5 && lz <= 6);
                        } else if (zoneType == ZONE_STORAGE) {
                            long lightHash = hash(Math.floorDiv(worldX, 4), Math.floorDiv(worldZ, 12), fixedSeed + 90000L);
                            int lx = Math.floorMod(worldX, 4);
                            int lz = Math.floorMod(worldZ, 12);
                            hasLight = (lightHash % 3 == 0) && (lx == 2) && (lz == 6);
                        } else { // NARROW
                            long lightHash = hash(Math.floorDiv(worldX, 4), Math.floorDiv(worldZ, 4), fixedSeed + 91000L);
                            hasLight = (lightHash % 6 == 0) && (Math.floorMod(worldX, 4) == 2) && (Math.floorMod(worldZ, 4) == 2);
                        }

                        if (hasLight && !isBorderWall) {
                            chunkAccess.setBlockState(pos, lightBlock, false);
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

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving carvingStep) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return 65;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        BlockState[] states = new BlockState[levelHeightAccessor.getHeight()];
        for (int i = 0; i < states.length; i++) {
            int y = levelHeightAccessor.getMinBuildHeight() + i;
            if (y < 63) {
                states[i] = Blocks.STONE.defaultBlockState();
            } else if (y == 63) {
                states[i] = Blocks.BEDROCK.defaultBlockState();
            } else if (y == 64) {
                states[i] = Blocks.GRAY_CONCRETE.defaultBlockState();
            } else if (y >= 65 && y <= 67) {
                states[i] = Blocks.AIR.defaultBlockState();
            } else if (y == 68) {
                states[i] = Blocks.STONE_BRICKS.defaultBlockState();
            } else if (y == 69) {
                states[i] = Blocks.BEDROCK.defaultBlockState();
            } else {
                states[i] = Blocks.STONE.defaultBlockState();
            }
        }
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
    }
}
