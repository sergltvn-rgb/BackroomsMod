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

public class BackroomsChunkGenerator extends ChunkGenerator {
    public static final Codec<BackroomsChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(BackroomsChunkGenerator::new))
    );

    private final long fixedSeed = 123456789L;

    // Типы зон (64×64 мега-ячейки)
    private static final int ZONE_CORRIDORS = 0;    // Классические узкие коридоры
    private static final int ZONE_OPEN_HALL = 1;     // Огромный зал с колоннами, высокий потолок
    private static final int ZONE_OFFICE = 2;        // Офисные кабинки с мебелью
    private static final int ZONE_MAINTENANCE = 3;   // Техническая зона: трубы, бетон, без ковра
    private static final int ZONE_DARK = 4;          // Тёмный лабиринт, без рабочих ламп
    private static final int ZONE_FLOODED = 5;       // Затопленная зона с водой

    public BackroomsChunkGenerator(BiomeSource biomeSource) {
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

    // ==================== ЗОНАЛЬНАЯ СИСТЕМА ====================

    private int getZoneType(long megaX, long megaZ) {
        // Безопасная комната на старте — всегда CORRIDORS
        if (megaX == 0 && megaZ == 0) return ZONE_CORRIDORS;

        long h = hash(megaX, megaZ, fixedSeed + 7000000L);
        int v = (int) (h % 100);
        if (v < 30) return ZONE_CORRIDORS;      // 30%
        if (v < 45) return ZONE_OPEN_HALL;       // 15%
        if (v < 65) return ZONE_OFFICE;          // 20%
        if (v < 80) return ZONE_MAINTENANCE;     // 15%
        if (v < 90) return ZONE_DARK;            // 10%
        return ZONE_FLOODED;                      // 10%
    }

    // ==================== ГРАНИЦЫ ВЫСОТ (заделка стыков) ====================

    private int getMegaCeilingY(long megaX, long megaZ) {
        return (getZoneType(megaX, megaZ) == ZONE_OPEN_HALL) ? 70 : 68;
    }

    // Если столб на краю мега-ячейки и сосед выше — вернуть макс. высоту потолка; иначе -1.
    private int getBorderWallTopY(long megaX, long megaZ, int mx, int mz, int thisCeilingY) {
        int maxNeighbor = thisCeilingY;
        if (mx == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX - 1, megaZ));
        if (mx == 63) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX + 1, megaZ));
        if (mz == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ - 1));
        if (mz == 63) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ + 1));
        return (maxNeighbor > thisCeilingY) ? maxNeighbor : -1;
    }

    // Проёмы в пограничной стене: 2 блока шириной каждые 16 блоков.
    private boolean isBorderDoorway(int worldX, int worldZ, int mx, int mz) {
        boolean door = false;
        if ((mx == 0 || mx == 63) && Math.floorMod(worldZ, 16) < 2) door = true;
        if ((mz == 0 || mz == 63) && Math.floorMod(worldX, 16) < 2) door = true;
        return door;
    }

    // ==================== СТЕНЫ ПО ЗОНАМ ====================

    private boolean isWallForZone(int worldX, int worldZ, int zoneType) {
        switch (zoneType) {
            case ZONE_CORRIDORS: return corridorWall(worldX, worldZ);
            case ZONE_OPEN_HALL: return hallWall(worldX, worldZ);
            case ZONE_OFFICE: return officeWall(worldX, worldZ);
            case ZONE_MAINTENANCE: return maintenanceWall(worldX, worldZ);
            case ZONE_DARK: return darkWall(worldX, worldZ);
            case ZONE_FLOODED: return floodedWall(worldX, worldZ);
            default: return false;
        }
    }

    // CORRIDORS: 6×6 ячейки, узкие проходы
    private boolean corridorWall(int worldX, int worldZ) {
        int cellSize = 6;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 100L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallX = true; // 40% сплошная стена
            } else if (type < 4) {
                wallX = (lz < 2 || lz > 3); // 40% стена с дверным проёмом
            }
            // 20% нет стены (открыто)
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 200L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallZ = true;
            } else if (type < 4) {
                wallZ = (lx < 2 || lx > 3);
            }
        }

        // Угловые столбы всегда стоят
        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    // OPEN_HALL: огромный зал, стиль колонн варьируется по регионам 48×48
    private boolean hallWall(int worldX, int worldZ) {
        long regionX = Math.floorDiv(worldX, 48);
        long regionZ = Math.floorDiv(worldZ, 48);
        long styleHash = hash(regionX, regionZ, fixedSeed + 1100L);
        int style = (int) (styleHash % 3);

        int spacing;   // расстояние между колоннами
        int thickness; // толщина колонны (1×1 или 2×2)
        switch (style) {
            case 0:  spacing = 8;  thickness = 1; break; // частые тонкие колонны
            case 1:  spacing = 12; thickness = 2; break; // классические массивные
            default: spacing = 16; thickness = 2; break; // редкие, широкий простор
        }

        int cx = Math.floorMod(worldX, spacing);
        int cz = Math.floorMod(worldZ, spacing);
        return cx < thickness && cz < thickness;
    }

    // OFFICE: 8×8 ячейки, кубиклы
    private boolean officeWall(int worldX, int worldZ) {
        int cellSize = 8;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 300L);
            int type = (int) (h % 5);
            if (type < 1) {
                wallX = true; // 20% сплошная
            } else if (type < 4) {
                wallX = (lz < 2 || lz > 4); // 60% с широким проёмом (3 блока)
            }
            // 20% открыто
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 400L);
            int type = (int) (h % 5);
            if (type < 1) {
                wallZ = true;
            } else if (type < 4) {
                wallZ = (lx < 2 || lx > 4);
            }
        }

        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    // MAINTENANCE: 10×10 ячейки, индустриальный стиль
    private boolean maintenanceWall(int worldX, int worldZ) {
        int cellSize = 10;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 500L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallX = true; // 40% сплошная
            } else if (type < 4) {
                wallX = (lz < 4 || lz > 5); // 40% с проёмом 2 блока
            }
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 600L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallZ = true;
            } else if (type < 4) {
                wallZ = (lx < 4 || lx > 5);
            }
        }

        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    // DARK: 5×5 ячейки, густой лабиринт
    private boolean darkWall(int worldX, int worldZ) {
        int cellSize = 5;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 700L);
            int type = (int) (h % 5);
            if (type < 3) {
                wallX = true; // 60% сплошная стена — густой лабиринт
            } else {
                wallX = (lz != 2); // узкий проём в 1 блок
            }
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 800L);
            int type = (int) (h % 5);
            if (type < 3) {
                wallZ = true;
            } else {
                wallZ = (lx != 2);
            }
        }

        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    // FLOODED: 8×8 ячейки, как коридоры но с водой
    private boolean floodedWall(int worldX, int worldZ) {
        int cellSize = 8;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 900L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallX = true;
            } else if (type < 4) {
                wallX = (lz < 3 || lz > 4);
            }
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 1000L);
            int type = (int) (h % 5);
            if (type < 2) {
                wallZ = true;
            } else if (type < 4) {
                wallZ = (lx < 3 || lx > 4);
            }
        }

        if (lx == 0 && lz == 0) {
            wallX = true;
            wallZ = true;
        }

        return wallX || wallZ;
    }

    // ==================== МЕБЕЛЬНЫЕ КЛАСТЕРЫ ====================

    /**
     * Возвращает блок мебели для данной позиции внутри ячейки, или null если мебели нет.
     * clusterType: 0=DESK_SETUP, 1=STORAGE_CORNER, 2=BREAK_AREA, 3=ABANDONED_PILE, 4=LONE_CHAIR
     */
    private BlockState getFurnitureAt(int lx, int lz, int clusterType, long cellHash) {
        Direction dir = Direction.from2DDataValue((int) (cellHash % 4));
        int glitchVal = 0;
        if (cellHash % 7 == 0) glitchVal = 1 + (int) (cellHash / 7 % 3);

        switch (clusterType) {
            case 0: // DESK_SETUP: стол + стул
                if (lx == 3 && lz == 4) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitchVal);
                if (lx == 3 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 1: // STORAGE_CORNER: коробки + шкаф
                if (lx == 1 && lz == 1) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitchVal);
                if (lx == 2 && lz == 1) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getClockWise()).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 1 && lz == 2) return BlockInit.FILE_CABINET.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitchVal);
                break;

            case 2: // BREAK_AREA: кулер + стулья
                if (lx == 3 && lz == 4) return BlockInit.WATER_COOLER.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 5 && lz == 4) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.WEST).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 5 && lz == 5) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.EAST).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 3: // ABANDONED_PILE: перевёрнутая мебель
                if (lx == 2 && lz == 2) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 2); // crooked
                if (lx == 3 && lz == 2) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getClockWise()).setValue(FurnitureBlock.GLITCH, 1); // sunk
                if (lx == 2 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getOpposite()).setValue(FurnitureBlock.GLITCH, 3); // combined
                break;

            case 4: // LONE_CHAIR: одинокий стул посреди зала
                if (lx == 4 && lz == 4) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 5: // CUBICLE: офисные перегородки + стол + стул + шкаф
                if (lx == 1 && (lz >= 1 && lz <= 4)) return BlockInit.OFFICE_PARTITION.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.EAST).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 3 && lz == 2) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitchVal);
                if (lx == 3 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 4 && lz == 5) return BlockInit.FILE_CABINET.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getOpposite()).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 6: // FILING_ROOM: ряд картотечных шкафов + коробки + кулер
                if (lz == 1 && lx >= 1 && lx <= 5) return BlockInit.FILE_CABINET.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.SOUTH).setValue(FurnitureBlock.GLITCH, (lx % 2 == 0) ? glitchVal : 0);
                if (lz == 4 && lx == 2) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lz == 4 && lx == 3) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getClockWise()).setValue(FurnitureBlock.GLITCH, glitchVal);
                if (lz == 5 && lx == 5) return BlockInit.WATER_COOLER.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                break;
        }
        return null;
    }

    // ==================== ОСНОВНАЯ ГЕНЕРАЦИЯ ====================

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        // Блоки Level 0
        BlockState wallpaper = BlockInit.WALLPAPER.get().defaultBlockState();
        BlockState carpet = BlockInit.CARPET.get().defaultBlockState();
        BlockState ceiling = BlockInit.CEILING_TILE.get().defaultBlockState();
        BlockState light = BlockInit.FLUORESCENT_LIGHT.get().defaultBlockState();
        BlockState flickeringLight = BlockInit.FLICKERING_LIGHT.get().defaultBlockState();
        BlockState brokenLight = BlockInit.BROKEN_LIGHT.get().defaultBlockState();
        BlockState drawnWallpaper = BlockInit.DRAWN_WALLPAPER.get().defaultBlockState();
        BlockState steelPipe = BlockInit.STEEL_PIPE.get().defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = Blocks.WATER.defaultBlockState();
        BlockState cobweb = Blocks.COBWEB.defaultBlockState();
        BlockState grayFloor = Blocks.GRAY_CONCRETE.defaultBlockState();
        BlockState crackedBricks = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState mossyCobble = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        BlockState glitched = BlockInit.GLITCHED_BLOCK.get().defaultBlockState();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            int worldX = minX + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = minZ + z;

                // === Определение зоны ===
                long megaX = Math.floorDiv(worldX, 64);
                long megaZ = Math.floorDiv(worldZ, 64);
                int zoneType = getZoneType(megaX, megaZ);

                // === Безопасная стартовая комната (24×24 в центре первой мега-ячейки) ===
                int mx = Math.floorMod(worldX, 64);
                int mz = Math.floorMod(worldZ, 64);
                boolean isSafeRoom = false;
                boolean isWall;

                if (megaX == 0 && megaZ == 0 && mx < 24 && mz < 24) {
                    isSafeRoom = true;
                    // Стены по периметру с 4-блочными дверными проёмами
                    if (mx == 0 || mz == 0 || mx == 23 || mz == 23) {
                        boolean isDoor = false;
                        if ((mx == 0 || mx == 23) && (mz >= 10 && mz <= 13)) isDoor = true;
                        if ((mz == 0 || mz == 23) && (mx >= 10 && mx <= 13)) isDoor = true;
                        isWall = !isDoor;
                    } else {
                        isWall = false;
                    }
                } else {
                    isWall = isWallForZone(worldX, worldZ, zoneType);
                }

                // === Параметры высоты по зоне ===
                int ceilingY = (zoneType == ZONE_OPEN_HALL && !isSafeRoom) ? 70 : 68;
                int wallMaxY = ceilingY - 1;

                // === Пограничная стена: заделка перепадов высот между мега-ячейками ===
                int borderTopY = isSafeRoom ? -1 : getBorderWallTopY(megaX, megaZ, mx, mz, ceilingY);
                boolean isBorderWall = borderTopY > 0;
                boolean borderDoor = isBorderWall && isBorderDoorway(worldX, worldZ, mx, mz);
                if (isBorderWall) {
                    ceilingY = borderTopY;
                    wallMaxY = ceilingY - 1;
                }

                // === Заполнение по Y ===
                for (int y = chunkAccess.getMinBuildHeight(); y < chunkAccess.getMaxBuildHeight(); y++) {
                    pos.set(worldX, y, worldZ);

                    if (y < 63) {
                        // Основание: бедрок внизу, камень выше
                        chunkAccess.setBlockState(pos, y == chunkAccess.getMinBuildHeight() ? bedrock : stone, false);
                    } else if (y == 63) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    } else if (y == 64) {
                        // === ПОЛ ===
                        if (!isSafeRoom && !isWall && !isBorderWall
                                && hash(worldX, worldZ, fixedSeed + 91000L) % 9000 == 0) {
                            // Редкий багнутый блок — портал ноклипа на следующий уровень
                            chunkAccess.setBlockState(pos, glitched, false);
                        } else if (zoneType == ZONE_MAINTENANCE && !isSafeRoom) {
                            chunkAccess.setBlockState(pos, grayFloor, false);
                        } else {
                            chunkAccess.setBlockState(pos, carpet, false);
                        }
                    } else if (y >= 65 && y <= wallMaxY) {
                        // === СТЕНЫ / ВОЗДУХ / МЕБЕЛЬ / АТМОСФЕРА ===
                        if (isBorderWall) {
                            // Пограничная стена: сплошные обои, редкие проёмы
                            if (borderDoor && y <= 67) {
                                chunkAccess.setBlockState(pos, air, false);
                            } else {
                                chunkAccess.setBlockState(pos, wallpaper, false);
                            }
                        } else if (isWall) {
                            // Определяем тип блока стены по зоне
                            BlockState wallBlock = getWallBlock(worldX, worldZ, y, zoneType, wallpaper, drawnWallpaper, crackedBricks, mossyCobble);
                            chunkAccess.setBlockState(pos, wallBlock, false);
                        } else {
                            // Не стена — воздух, мебель или атмосферные детали
                            BlockState fillBlock = getAirFillBlock(worldX, worldZ, y, zoneType,
                                    air, water, cobweb, steelPipe, isSafeRoom);
                            chunkAccess.setBlockState(pos, fillBlock, false);
                        }
                    } else if (y == ceilingY) {
                        // === ПОТОЛОК / ОСВЕЩЕНИЕ ===
                        if (isBorderWall) {
                            chunkAccess.setBlockState(pos, ceiling, false);
                        } else {
                            BlockState ceilingBlock = getCeilingBlock(worldX, worldZ, zoneType,
                                    ceiling, light, flickeringLight, brokenLight, steelPipe, isSafeRoom);
                            chunkAccess.setBlockState(pos, ceilingBlock, false);
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

    // ==================== БЛОКИ СТЕН ПО ЗОНАМ ====================

    private BlockState getWallBlock(int worldX, int worldZ, int y, int zoneType,
                                     BlockState wallpaper, BlockState drawnWallpaper,
                                     BlockState crackedBricks, BlockState mossyCobble) {
        long wallHash = hash(worldX, worldZ, y + fixedSeed + 3000L);

        switch (zoneType) {
            case ZONE_MAINTENANCE:
                // Потрескавшийся кирпич, иногда обычные стены
                return (wallHash % 3 == 0) ? crackedBricks : Blocks.STONE_BRICKS.defaultBlockState();

            case ZONE_FLOODED:
                // Мшистый булыжник на нижних блоках, обои выше
                if (y == 65 && wallHash % 3 == 0) return mossyCobble;
                if (y == 66 && wallHash % 5 == 0) return mossyCobble;
                return wallpaper;

            case ZONE_DARK:
                // Рисунки на стенах чаще — жуткие граффити
                if (y == 66 && wallHash % 40 == 0) return drawnWallpaper;
                return wallpaper;

            default:
                // Обычные обои, редкие рисунки
                if (y == 66 && wallHash % 120 == 0) return drawnWallpaper;
                return wallpaper;
        }
    }

    // ==================== ЗАПОЛНЕНИЕ НЕ-СТЕН ====================

    private BlockState getAirFillBlock(int worldX, int worldZ, int y, int zoneType,
                                        BlockState air, BlockState water, BlockState cobweb,
                                        BlockState steelPipe, boolean isSafeRoom) {
        if (isSafeRoom) return air;

        // === ЗАТОПЛЕННАЯ ЗОНА: вода на уровне y=65 ===
        if (zoneType == ZONE_FLOODED && y == 65) {
            long waterHash = hash(worldX, worldZ, fixedSeed + 60000L);
            if (waterHash % 3 != 2) { // ~66% затоплено
                return water;
            }
            return air;
        }

        // === ТЁМНАЯ ЗОНА: паутина в углах ===
        if (zoneType == ZONE_DARK && y == 66) {
            int cellSize = 5;
            int lx = Math.floorMod(worldX, cellSize);
            int lz = Math.floorMod(worldZ, cellSize);
            if (lx == 1 && lz == 1) {
                long cobwebHash = hash(worldX, worldZ, fixedSeed + 70000L);
                if (cobwebHash % 4 == 0) return cobweb; // 25% шанс паутины в углу
            }
        }

        // === ТЕХНИЧЕСКАЯ ЗОНА: трубы на потолке ===
        if (zoneType == ZONE_MAINTENANCE && y == 67) {
            long pipeHash = hash(worldX, worldZ, fixedSeed + 80000L);
            if (Math.floorMod(worldX, 3) == 0 && pipeHash % 4 == 0) {
                return steelPipe;
            }
        }

        // === МЕБЕЛЬНЫЕ КЛАСТЕРЫ (только y=65) ===
        if (y == 65) {
            BlockState furniture = placeFurnitureCluster(worldX, worldZ, zoneType);
            if (furniture != null) return furniture;
        }

        return air;
    }

    // ==================== РАЗМЕЩЕНИЕ МЕБЕЛИ ====================

    private BlockState placeFurnitureCluster(int worldX, int worldZ, int zoneType) {
        // Определяем размер ячейки для мебели по зоне
        int cellSize;
        switch (zoneType) {
            case ZONE_CORRIDORS: cellSize = 6; break;
            case ZONE_OFFICE: cellSize = 8; break;
            case ZONE_OPEN_HALL: cellSize = 12; break;
            case ZONE_DARK: cellSize = 5; break;
            case ZONE_FLOODED: cellSize = 8; break;
            default: return null; // MAINTENANCE — без мебели
        }

        long fcx = Math.floorDiv(worldX, cellSize);
        int flx = Math.floorMod(worldX, cellSize);
        long fcz = Math.floorDiv(worldZ, cellSize);
        int flz = Math.floorMod(worldZ, cellSize);

        long furnCellHash = hash(fcx, fcz, fixedSeed + 50000L);
        boolean hasFurniture;
        int clusterType;

        switch (zoneType) {
            case ZONE_OFFICE:
                hasFurniture = (furnCellHash % 2 == 0); // 50% ячеек — насыщенный офис
                switch ((int) ((furnCellHash / 3) % 5)) {
                    case 0:  clusterType = 0; break; // DESK_SETUP
                    case 1:  clusterType = 1; break; // STORAGE_CORNER
                    case 2:  clusterType = 2; break; // BREAK_AREA
                    case 3:  clusterType = 5; break; // CUBICLE
                    default: clusterType = 6; break; // FILING_ROOM
                }
                break;
            case ZONE_CORRIDORS:
                hasFurniture = (furnCellHash % 10 == 0); // 10%
                clusterType = (furnCellHash / 10 % 2 == 0) ? 1 : 4; // STORAGE или LONE_CHAIR
                break;
            case ZONE_OPEN_HALL:
                hasFurniture = (furnCellHash % 15 == 0); // Редко
                clusterType = 4; // LONE_CHAIR
                break;
            case ZONE_DARK:
                hasFurniture = (furnCellHash % 6 == 0); // 16%
                clusterType = 3; // ABANDONED_PILE
                break;
            case ZONE_FLOODED:
                hasFurniture = (furnCellHash % 12 == 0); // 8%
                clusterType = 3; // ABANDONED_PILE
                break;
            default:
                return null;
        }

        if (!hasFurniture) return null;
        return getFurnitureAt(flx, flz, clusterType, furnCellHash);
    }

    // ==================== ПОТОЛОК И ОСВЕЩЕНИЕ ====================

    private BlockState getCeilingBlock(int worldX, int worldZ, int zoneType,
                                        BlockState ceiling, BlockState light,
                                        BlockState flickeringLight, BlockState brokenLight,
                                        BlockState steelPipe, boolean isSafeRoom) {
        // Безопасная комната — хорошо освещена
        if (isSafeRoom) {
            int smx = Math.floorMod(worldX, 6);
            int smz = Math.floorMod(worldZ, 6);
            if (smx >= 2 && smx <= 3 && smz >= 2 && smz <= 3) {
                return light;
            }
            return ceiling;
        }

        // Техническая зона: трубы вместо потолка
        if (zoneType == ZONE_MAINTENANCE) {
            long pipeHash = hash(worldX, worldZ, fixedSeed + 81000L);
            if (pipeHash % 3 == 0) return steelPipe;
            return ceiling;
        }

        // Тёмная зона: только сломанные лампы
        if (zoneType == ZONE_DARK) {
            long darkHash = hash(worldX, worldZ, fixedSeed + 82000L);
            if (darkHash % 20 == 0) return brokenLight; // Редкие сломанные лампы
            return ceiling;
        }

        // Определяем наличие лампы по ячейке зоны
        int cellSize;
        switch (zoneType) {
            case ZONE_CORRIDORS: cellSize = 6; break;
            case ZONE_OPEN_HALL: cellSize = 12; break;
            case ZONE_OFFICE: cellSize = 8; break;
            case ZONE_FLOODED: cellSize = 8; break;
            default: cellSize = 8; break;
        }

        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        // Лампа в центре ячейки (2×2 блока)
        int center = cellSize / 2;
        boolean isCenterPos = (lx == center || lx == center - 1) && (lz == center || lz == center - 1);

        if (!isCenterPos) return ceiling;

        // Шанс лампы зависит от зоны
        long roomHash = hash(cx, cz, fixedSeed + 2000000L);
        boolean hasLight;
        switch (zoneType) {
            case ZONE_CORRIDORS:
                hasLight = (roomHash % 5 == 0); // 20%
                break;
            case ZONE_OPEN_HALL:
                hasLight = (roomHash % 2 == 0); // 50%
                break;
            case ZONE_OFFICE:
                hasLight = (roomHash % 3 == 0); // 33%
                break;
            case ZONE_FLOODED:
                hasLight = (roomHash % 7 == 0); // 14%
                break;
            default:
                hasLight = (roomHash % 4 == 0);
                break;
        }

        if (!hasLight) return ceiling;

        // Тип лампы
        long lightHash = hash(worldX, worldZ, fixedSeed + 333L);
        switch (zoneType) {
            case ZONE_CORRIDORS:
                // 40% мигающая, 20% сломанная, 40% нормальная
                if (lightHash % 5 < 2) return flickeringLight;
                if (lightHash % 5 == 2) return brokenLight;
                return light;

            case ZONE_FLOODED:
                // Почти все мигающие
                if (lightHash % 5 < 3) return flickeringLight;
                if (lightHash % 5 == 3) return brokenLight;
                return light;

            case ZONE_OPEN_HALL:
                // В основном нормальные
                if (lightHash % 10 == 0) return flickeringLight;
                return light;

            case ZONE_OFFICE:
                // В основном нормальные, иногда мигающие
                if (lightHash % 5 == 0) return flickeringLight;
                if (lightHash % 8 == 0) return brokenLight;
                return light;

            default:
                return light;
        }
    }

    // ==================== СТАНДАРТНЫЕ ПЕРЕОПРЕДЕЛЕНИЯ ====================

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
                states[i] = BlockInit.CARPET.get().defaultBlockState();
            } else if (y >= 65 && y <= 67) {
                states[i] = Blocks.AIR.defaultBlockState();
            } else if (y == 68) {
                states[i] = BlockInit.CEILING_TILE.get().defaultBlockState();
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
