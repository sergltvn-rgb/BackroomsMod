package com.litvin.backrooms.worldgen;

import com.litvin.backrooms.block.FurnitureBlock;
import com.litvin.backrooms.init.BlockInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * Level 4 — "Заброшенный офис"
 * Офисные помещения с кабинками, конференц-залами и серверными.
 * Зоны: OFFICE (кабинки), CONFERENCE (большая комната), SERVER_ROOM (тёмная), LOBBY (светлое фойе)
 */
public class Level4ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level4ChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(Level4ChunkGenerator::new))
    );

    private final long fixedSeed = 444555666L;

    private static final int ZONE_OFFICE = 0;       // Офисные кабинки
    private static final int ZONE_CONFERENCE = 1;    // Конференц-зал
    private static final int ZONE_SERVER = 2;        // Серверная комната (тёмная)
    private static final int ZONE_LOBBY = 3;         // Лобби (открытое, светлое)

    public Level4ChunkGenerator(BiomeSource biomeSource) {
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
        if (megaX == 0 && megaZ == 0) return ZONE_LOBBY;
        long h = hash(megaX, megaZ, fixedSeed + 7000000L);
        int v = (int) (h % 100);
        if (v < 40) return ZONE_OFFICE;       // 40%
        if (v < 60) return ZONE_CONFERENCE;   // 20%
        if (v < 75) return ZONE_SERVER;       // 15%
        return ZONE_LOBBY;                     // 25%
    }

    private boolean isWallForZone(int worldX, int worldZ, int zoneType) {
        switch (zoneType) {
            case ZONE_OFFICE: return officeWall(worldX, worldZ);
            case ZONE_CONFERENCE: return conferenceWall(worldX, worldZ);
            case ZONE_SERVER: return serverWall(worldX, worldZ);
            case ZONE_LOBBY: return lobbyWall(worldX, worldZ);
            default: return false;
        }
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
            long h = hash(cx - 1, cz, fixedSeed + 100L);
            int type = (int) (h % 5);
            if (type < 1) wallX = true;
            else if (type < 4) wallX = (lz < 2 || lz > 4); // 3-блочный проём
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 200L);
            int type = (int) (h % 5);
            if (type < 1) wallZ = true;
            else if (type < 4) wallZ = (lx < 2 || lx > 4);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // CONFERENCE: 16×16 ячейки, большие комнаты
    private boolean conferenceWall(int worldX, int worldZ) {
        int cellSize = 16;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 300L);
            int type = (int) (h % 3);
            if (type == 0) wallX = true;
            else wallX = (lz < 6 || lz > 9); // 4-блочный проём
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 400L);
            int type = (int) (h % 3);
            if (type == 0) wallZ = true;
            else wallZ = (lx < 6 || lx > 9);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // SERVER: 6×6 ячейки, тёмные тесные серверные
    private boolean serverWall(int worldX, int worldZ) {
        int cellSize = 6;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 500L);
            int type = (int) (h % 4);
            if (type < 2) wallX = true;
            else wallX = (lz < 2 || lz > 3);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 600L);
            int type = (int) (h % 4);
            if (type < 2) wallZ = true;
            else wallZ = (lx < 2 || lx > 3);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // LOBBY: 12×12 ячейки, широкие открытые пространства
    private boolean lobbyWall(int worldX, int worldZ) {
        int cellSize = 12;
        int lx = Math.floorMod(worldX, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);
        long cx = Math.floorDiv(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 700L);
            int type = (int) (h % 5);
            if (type == 0) wallX = true;
            else if (type < 3) wallX = (lz < 4 || lz > 7); // 4-блочный проём
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 800L);
            int type = (int) (h % 5);
            if (type == 0) wallZ = true;
            else if (type < 3) wallZ = (lx < 4 || lx > 7);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }

        // Декоративные колонны 2×2 в лобби
        if (!wallX && !wallZ && (lx == 5 || lx == 6) && (lz == 5 || lz == 6)) {
            long colHash = hash(cx, cz, fixedSeed + 900L);
            if (colHash % 3 == 0) return true; // 33% шанс колонны
        }

        return wallX || wallZ;
    }

    // Мебельные кластеры для офисов
    private BlockState getOfficeFurniture(int lx, int lz, int clusterType, long cellHash) {
        Direction dir = Direction.from2DDataValue((int) (cellHash % 4));
        int glitch = (cellHash % 7 == 0) ? 1 + (int) (cellHash / 7 % 3) : 0;

        switch (clusterType) {
            case 0: // DESK_SETUP: стол + стул
                if (lx == 3 && lz == 4) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitch);
                if (lx == 3 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 5 && lz == 4) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getOpposite()).setValue(FurnitureBlock.GLITCH, glitch);
                if (lx == 5 && lz == 5) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getOpposite()).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 1: // FILING_AREA: шкафы + коробки
                if (lx == 1 && lz == 1) return BlockInit.FILE_CABINET.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, glitch);
                if (lx == 1 && lz == 2) return BlockInit.FILE_CABINET.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 2 && lz == 1) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getClockWise()).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 2: // BREAK_ROOM: кулер + стулья
                if (lx == 4 && lz == 4) return BlockInit.WATER_COOLER.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 3 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.EAST).setValue(FurnitureBlock.GLITCH, 0);
                if (lx == 5 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.WEST).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 3: // CONFERENCE_TABLE: длинный стол по центру
                if (lz == 8 && lx >= 5 && lx <= 10) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.NORTH).setValue(FurnitureBlock.GLITCH, glitch);
                if (lz == 7 && (lx == 5 || lx == 7 || lx == 9)) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.NORTH).setValue(FurnitureBlock.GLITCH, 0);
                if (lz == 9 && (lx == 6 || lx == 8 || lx == 10)) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, Direction.SOUTH).setValue(FurnitureBlock.GLITCH, 0);
                break;

            case 4: // ABANDONED
                if (lx == 3 && lz == 3) return BlockInit.OFFICE_TABLE.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 2);
                if (lx == 4 && lz == 3) return BlockInit.OFFICE_CHAIR.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getOpposite()).setValue(FurnitureBlock.GLITCH, 3);
                if (lx == 3 && lz == 4) return BlockInit.CARDBOARD_BOX.get().defaultBlockState()
                        .setValue(FurnitureBlock.FACING, dir.getClockWise()).setValue(FurnitureBlock.GLITCH, 1);
                break;
        }
        return null;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        BlockState floor = Blocks.WHITE_CONCRETE.defaultBlockState();
        BlockState wall = Blocks.QUARTZ_BLOCK.defaultBlockState();
        BlockState window = Blocks.GLASS_PANE.defaultBlockState();
        BlockState ceiling = BlockInit.CEILING_TILE.get().defaultBlockState();
        BlockState light = BlockInit.FLUORESCENT_LIGHT.get().defaultBlockState();
        BlockState flickeringLight = BlockInit.FLICKERING_LIGHT.get().defaultBlockState();
        BlockState brokenLight = BlockInit.BROKEN_LIGHT.get().defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState vent = BlockInit.CEILING_VENT.get().defaultBlockState();
        BlockState partition = BlockInit.OFFICE_PARTITION.get().defaultBlockState();
        BlockState drawnWallpaper = BlockInit.DRAWN_WALLPAPER.get().defaultBlockState();
        BlockState darkFloor = Blocks.GRAY_CONCRETE.defaultBlockState();
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

                boolean isSafeRoom = (megaX == 0 && megaZ == 0 && mx < 20 && mz < 20);
                boolean isWall;

                if (isSafeRoom) {
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

                for (int y = 63; y <= 69; y++) {
                    pos.set(worldX, y, worldZ);

                    if (y == 63) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    } else if (y == 64) {
                        // Пол: тёмный в серверной, белый в остальных
                        chunkAccess.setBlockState(pos, (zoneType == ZONE_SERVER && !isSafeRoom) ? darkFloor : floor, false);
                    } else if (y >= 65 && y <= 67) {
                        if (isWall) {
                            // Окна в офисных стенах
                            if (y == 66 && zoneType != ZONE_SERVER) {
                                long wHash = hash(worldX, worldZ, fixedSeed + 3000L);
                                if (wHash % 4 == 0) {
                                    chunkAccess.setBlockState(pos, window, false);
                                } else {
                                    chunkAccess.setBlockState(pos, wall, false);
                                }
                            } else if (y == 65 && (zoneType == ZONE_OFFICE || zoneType == ZONE_LOBBY)) {
                                // Нижний ряд стены — иногда исписанные обои (граффити)
                                long gHash = hash(worldX, worldZ, fixedSeed + 3500L);
                                if (gHash % 7 == 0) {
                                    Direction gdir = Direction.from2DDataValue((int) (gHash % 4));
                                    chunkAccess.setBlockState(pos, drawnWallpaper.setValue(FurnitureBlock.FACING, gdir), false);
                                } else {
                                    chunkAccess.setBlockState(pos, wall, false);
                                }
                            } else {
                                chunkAccess.setBlockState(pos, wall, false);
                            }
                        } else {
                            // Мебель и декорации
                            if (y == 65 && !isSafeRoom) {
                                BlockState furniture = placeLevelFourFurniture(worldX, worldZ, zoneType);
                                if (furniture != null) {
                                    chunkAccess.setBlockState(pos, furniture, false);
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            } else if (y == 66 && !isSafeRoom) {
                                // Перегородки (второй блок) — только в OFFICE
                                if (zoneType == ZONE_OFFICE) {
                                    long partHash = hash(worldX, worldZ, fixedSeed + 40000L);
                                    int olx = Math.floorMod(worldX, 8);
                                    int olz = Math.floorMod(worldZ, 8);
                                    // Внутренние перегородки в кубиклах
                                    if ((olx == 4 && olz >= 2 && olz <= 5) && partHash % 3 == 0) {
                                        Direction dir = Direction.from2DDataValue((int) (partHash % 4));
                                        chunkAccess.setBlockState(pos, partition.setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0), false);
                                    } else {
                                        chunkAccess.setBlockState(pos, air, false);
                                    }
                                }
                                // Паутина в серверной
                                else if (zoneType == ZONE_SERVER) {
                                    int slx = Math.floorMod(worldX, 6);
                                    int slz = Math.floorMod(worldZ, 6);
                                    if (slx == 1 && slz == 1) {
                                        long cwHash = hash(worldX, worldZ, fixedSeed + 50000L);
                                        chunkAccess.setBlockState(pos, (cwHash % 4 == 0) ? cobweb : air, false);
                                    } else {
                                        chunkAccess.setBlockState(pos, air, false);
                                    }
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            } else if (y == 67 && !isSafeRoom) {
                                // Вентиляция на потолке
                                long ventHash = hash(worldX, worldZ, fixedSeed + 60000L);
                                if (ventHash % 80 == 0) {
                                    Direction dir = Direction.from2DDataValue((int) (ventHash % 4));
                                    chunkAccess.setBlockState(pos, vent.setValue(FurnitureBlock.FACING, dir).setValue(FurnitureBlock.GLITCH, 0), false);
                                } else {
                                    chunkAccess.setBlockState(pos, air, false);
                                }
                            } else {
                                chunkAccess.setBlockState(pos, air, false);
                            }
                        }
                    } else if (y == 68) {
                        // Потолок и освещение
                        BlockState ceilBlock = getCeilingForZone(worldX, worldZ, zoneType, isSafeRoom,
                                ceiling, light, flickeringLight, brokenLight);
                        chunkAccess.setBlockState(pos, ceilBlock, false);
                    } else if (y == 69) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    }
                }

                // Fill below and above
                for (int y = chunkAccess.getMinBuildHeight(); y < 63; y++) {
                    pos.set(worldX, y, worldZ);
                    chunkAccess.setBlockState(pos, y == chunkAccess.getMinBuildHeight() ? bedrock : stone, false);
                }
                for (int y = 70; y < chunkAccess.getMaxBuildHeight(); y++) {
                    pos.set(worldX, y, worldZ);
                    chunkAccess.setBlockState(pos, stone, false);
                }
            }
        }
        return CompletableFuture.completedFuture(chunkAccess);
    }

    private BlockState placeLevelFourFurniture(int worldX, int worldZ, int zoneType) {
        int cellSize;
        switch (zoneType) {
            case ZONE_OFFICE: cellSize = 8; break;
            case ZONE_CONFERENCE: cellSize = 16; break;
            case ZONE_SERVER: cellSize = 6; break;
            case ZONE_LOBBY: cellSize = 12; break;
            default: return null;
        }

        long fcx = Math.floorDiv(worldX, cellSize);
        int flx = Math.floorMod(worldX, cellSize);
        long fcz = Math.floorDiv(worldZ, cellSize);
        int flz = Math.floorMod(worldZ, cellSize);

        long furnHash = hash(fcx, fcz, fixedSeed + 50000L);
        boolean hasFurniture;
        int clusterType;

        switch (zoneType) {
            case ZONE_OFFICE:
                hasFurniture = (furnHash % 2 == 0); // 50% — офис полон мебели
                clusterType = (int) ((furnHash / 2) % 3); // DESK, FILING, BREAK
                break;
            case ZONE_CONFERENCE:
                hasFurniture = true; // Всегда мебель
                clusterType = 3; // CONFERENCE_TABLE
                break;
            case ZONE_SERVER:
                hasFurniture = (furnHash % 4 == 0); // 25%
                clusterType = 4; // ABANDONED
                break;
            case ZONE_LOBBY:
                hasFurniture = (furnHash % 5 == 0); // 20%
                clusterType = 2; // BREAK_ROOM
                break;
            default:
                return null;
        }

        if (!hasFurniture) return null;
        return getOfficeFurniture(flx, flz, clusterType, furnHash);
    }

    private BlockState getCeilingForZone(int worldX, int worldZ, int zoneType, boolean isSafeRoom,
                                          BlockState ceiling, BlockState light,
                                          BlockState flickeringLight, BlockState brokenLight) {
        if (isSafeRoom) {
            int smx = Math.floorMod(worldX, 5);
            int smz = Math.floorMod(worldZ, 5);
            return (smx == 2 && smz == 2) ? light : ceiling;
        }

        int cellSize;
        switch (zoneType) {
            case ZONE_OFFICE: cellSize = 8; break;
            case ZONE_CONFERENCE: cellSize = 16; break;
            case ZONE_SERVER: cellSize = 6; break;
            case ZONE_LOBBY: cellSize = 12; break;
            default: cellSize = 8; break;
        }

        int lx = Math.floorMod(worldX, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);
        int center = cellSize / 2;

        boolean isCenterPos = (lx == center || lx == center - 1) && (lz == center || lz == center - 1);
        if (!isCenterPos) return ceiling;

        long roomHash = hash(Math.floorDiv(worldX, cellSize), Math.floorDiv(worldZ, cellSize), fixedSeed + 2000000L);

        switch (zoneType) {
            case ZONE_SERVER:
                // Тёмная — только broken или flickering
                if (roomHash % 3 == 0) {
                    long lh = hash(worldX, worldZ, fixedSeed + 333L);
                    return (lh % 3 == 0) ? flickeringLight : brokenLight;
                }
                return ceiling;

            case ZONE_OFFICE:
                if (roomHash % 2 == 0) { // 50% ячеек с лампой
                    long lh = hash(worldX, worldZ, fixedSeed + 333L);
                    if (lh % 6 == 0) return flickeringLight;
                    return light;
                }
                return ceiling;

            case ZONE_CONFERENCE:
                // Хорошо освещённые
                return light;

            case ZONE_LOBBY:
                if (roomHash % 3 != 2) { // 66%
                    long lh = hash(worldX, worldZ, fixedSeed + 333L);
                    if (lh % 8 == 0) return flickeringLight;
                    return light;
                }
                return ceiling;

            default:
                return ceiling;
        }
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
