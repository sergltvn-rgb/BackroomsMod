package com.litvin.backrooms.worldgen;

import com.litvin.backrooms.init.BlockInit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
 * Level 2 — "Технические тоннели"
 * Тесные тоннели с трубами, лужами, машинными залами и вентиляционными лабиринтами.
 * Зоны: PIPE (трубы), FLOODED (затопленные), JUNCTION (перекрёстки), MACHINE (машинный зал), VENT (вентлабиринт)
 */
public class Level2ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level2ChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(Level2ChunkGenerator::new))
    );

    private final long fixedSeed = 543219876L;

    private static final int ZONE_PIPE = 0;       // Тоннели с трубами
    private static final int ZONE_FLOODED = 1;    // Затопленные тоннели
    private static final int ZONE_JUNCTION = 2;   // Перекрёстки
    private static final int ZONE_MACHINE = 3;    // Машинный зал
    private static final int ZONE_VENT = 4;       // Вентиляционный лабиринт

    public Level2ChunkGenerator(BiomeSource biomeSource) {
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
        if (megaX == 0 && megaZ == 0) return ZONE_JUNCTION;
        long h = hash(megaX, megaZ, fixedSeed + 7000000L);
        int v = (int) (h % 100);
        if (v < 30) return ZONE_PIPE;       // 30%
        if (v < 50) return ZONE_FLOODED;    // 20%
        if (v < 68) return ZONE_JUNCTION;   // 18%
        if (v < 86) return ZONE_MACHINE;    // 18%
        return ZONE_VENT;                    // 14%
    }

    private boolean isWallForZone(int worldX, int worldZ, int zoneType) {
        switch (zoneType) {
            case ZONE_PIPE: return pipeWall(worldX, worldZ);
            case ZONE_FLOODED: return floodedWall(worldX, worldZ);
            case ZONE_JUNCTION: return junctionWall(worldX, worldZ);
            case ZONE_MACHINE: return machineWall(worldX, worldZ);
            case ZONE_VENT: return ventWall(worldX, worldZ);
            default: return false;
        }
    }

    // PIPE: 6×6 ячейки, тесные тоннели
    private boolean pipeWall(int worldX, int worldZ) {
        int cellSize = 6;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 100L);
            int type = (int) (h % 4);
            if (type < 2) wallX = true;
            else wallX = (lz < 2 || lz > 3);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 200L);
            int type = (int) (h % 4);
            if (type < 2) wallZ = true;
            else wallZ = (lx < 2 || lx > 3);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // FLOODED: 8×8 ячейки, более открытые
    private boolean floodedWall(int worldX, int worldZ) {
        int cellSize = 8;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 300L);
            int type = (int) (h % 5);
            if (type < 1) wallX = true;
            else if (type < 3) wallX = (lz < 3 || lz > 4);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 400L);
            int type = (int) (h % 5);
            if (type < 1) wallZ = true;
            else if (type < 3) wallZ = (lx < 3 || lx > 4);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // JUNCTION: 10×10 ячейки, широкие перекрёстки
    private boolean junctionWall(int worldX, int worldZ) {
        int cellSize = 10;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 500L);
            int type = (int) (h % 5);
            if (type < 1) wallX = true;
            else if (type < 3) wallX = (lz < 3 || lz > 6);
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 600L);
            int type = (int) (h % 5);
            if (type < 1) wallZ = true;
            else if (type < 3) wallZ = (lx < 3 || lx > 6);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    // MACHINE: 14×14 ячейки, большие открытые залы с колоннами
    private boolean machineWall(int worldX, int worldZ) {
        int cellSize = 14;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 1500L);
            int type = (int) (h % 5);
            if (type < 1) wallX = true;                       // 20% сплошная
            else if (type < 2) wallX = (lz < 5 || lz > 8);    // 20% широкий проём
            // 60% открыто — огромные залы
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 1600L);
            int type = (int) (h % 5);
            if (type < 1) wallZ = true;
            else if (type < 2) wallZ = (lx < 5 || lx > 8);
        }

        // Опорные колонны 2×2 в углах ячеек
        if (lx <= 1 && lz <= 1) return true;
        return wallX || wallZ;
    }

    // VENT: 4×4 ячейки, тесный клаустрофобный лабиринт
    private boolean ventWall(int worldX, int worldZ) {
        int cellSize = 4;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);

        boolean wallX = false, wallZ = false;

        if (lx == 0) {
            long h = hash(cx - 1, cz, fixedSeed + 1700L);
            int type = (int) (h % 5);
            if (type < 3) wallX = true;            // 60% сплошная — густой лабиринт
            else wallX = (lz != 1 && lz != 2);     // узкий проём
        }

        if (lz == 0) {
            long h = hash(cx, cz - 1, fixedSeed + 1800L);
            int type = (int) (h % 5);
            if (type < 3) wallZ = true;
            else wallZ = (lx != 1 && lx != 2);
        }

        if (lx == 0 && lz == 0) { wallX = true; wallZ = true; }
        return wallX || wallZ;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkPos chunkPos = chunkAccess.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        BlockState brickWall = Blocks.BRICKS.defaultBlockState();
        BlockState stoneBrickFloor = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState crackedWall = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState mossyWall = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        BlockState ironWall = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState copperWall = Blocks.COPPER_BLOCK.defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();
        BlockState chain = Blocks.CHAIN.defaultBlockState();
        BlockState cauldron = Blocks.CAULDRON.defaultBlockState();
        BlockState ceilingBlock = Blocks.BRICKS.defaultBlockState();
        BlockState pipe = BlockInit.STEEL_PIPE.get().defaultBlockState();
        BlockState light = BlockInit.FLUORESCENT_LIGHT.get().defaultBlockState();
        BlockState flickeringLight = BlockInit.FLICKERING_LIGHT.get().defaultBlockState();
        BlockState brokenLight = BlockInit.BROKEN_LIGHT.get().defaultBlockState();
        BlockState bedrock = Blocks.BEDROCK.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState water = Blocks.WATER.defaultBlockState();
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

                // Машинный зал выше (потолок y=70), остальные y=68. Вентлабиринт ниже (y=67).
                int ceilingY;
                if (isSafeRoom) ceilingY = 68;
                else if (zoneType == ZONE_MACHINE) ceilingY = 70;
                else if (zoneType == ZONE_VENT) ceilingY = 67;
                else ceilingY = 68;

                // Пограничная стена: заделка перепадов высот между мега-ячейками
                int borderTopY = isSafeRoom ? -1 : getBorderWallTopY(megaX, megaZ, mx, mz, ceilingY);
                boolean isBorderWall = borderTopY > 0;
                if (isBorderWall) ceilingY = borderTopY;

                for (int y = chunkAccess.getMinBuildHeight(); y < chunkAccess.getMaxBuildHeight(); y++) {
                    pos.set(worldX, y, worldZ);

                    if (y < 63) {
                        chunkAccess.setBlockState(pos, y == chunkAccess.getMinBuildHeight() ? bedrock : stone, false);
                    } else if (y == 63) {
                        chunkAccess.setBlockState(pos, bedrock, false);
                    } else if (y == 64) {
                        // Пол
                        if (zoneType == ZONE_MACHINE && !isSafeRoom) {
                            long fh = hash(worldX, worldZ, fixedSeed + 11000L);
                            chunkAccess.setBlockState(pos, (fh % 7 == 0) ? Blocks.IRON_BLOCK.defaultBlockState() : stoneBrickFloor, false);
                        } else {
                            chunkAccess.setBlockState(pos, stoneBrickFloor, false);
                        }
                    } else if (y >= 65 && y < ceilingY) {
                        if (isBorderWall) {
                            chunkAccess.setBlockState(pos, brickWall, false);
                        } else if (isWall) {
                            long wallHash = hash(worldX, worldZ, y + fixedSeed + 3000L);
                            BlockState wallBlock;
                            switch (zoneType) {
                                case ZONE_FLOODED:
                                    wallBlock = (y == 65 || wallHash % 4 == 0) ? mossyWall : brickWall;
                                    break;
                                case ZONE_PIPE:
                                    wallBlock = (wallHash % 6 == 0) ? crackedWall : brickWall;
                                    break;
                                case ZONE_MACHINE:
                                    // Металлические панели и медь
                                    if (wallHash % 5 == 0) wallBlock = copperWall;
                                    else if (wallHash % 3 == 0) wallBlock = ironWall;
                                    else wallBlock = crackedWall;
                                    break;
                                case ZONE_VENT:
                                    wallBlock = ironWall;
                                    break;
                                default:
                                    wallBlock = brickWall;
                            }
                            chunkAccess.setBlockState(pos, wallBlock, false);
                        } else {
                            BlockState fill = getInteriorBlock(worldX, worldZ, y, ceilingY, zoneType, isSafeRoom,
                                    air, water, cobweb, pipe, ironBars, chain, ironWall, cauldron);
                            chunkAccess.setBlockState(pos, fill, false);
                        }
                    } else if (y == ceilingY) {
                        if (isBorderWall) {
                            chunkAccess.setBlockState(pos, ceilingBlock, false);
                        } else {
                            BlockState c = getCeilingBlock(worldX, worldZ, zoneType, isSafeRoom,
                                    ceilingBlock, light, flickeringLight, brokenLight, pipe);
                            chunkAccess.setBlockState(pos, c, false);
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

    // Высота потолка соседней мега-ячейки
    private int getMegaCeilingY(long megaX, long megaZ) {
        int zone = getZoneType(megaX, megaZ);
        if (zone == ZONE_MACHINE) return 70;
        if (zone == ZONE_VENT) return 67;
        return 68;
    }

    // Если столб на краю мега-ячейки и сосед выше — вернуть макс. высоту; иначе -1.
    private int getBorderWallTopY(long megaX, long megaZ, int mx, int mz, int thisCeilingY) {
        int maxNeighbor = thisCeilingY;
        if (mx == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX - 1, megaZ));
        if (mx == 47) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX + 1, megaZ));
        if (mz == 0)  maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ - 1));
        if (mz == 47) maxNeighbor = Math.max(maxNeighbor, getMegaCeilingY(megaX, megaZ + 1));
        return (maxNeighbor > thisCeilingY) ? maxNeighbor : -1;
    }

    // Внутреннее наполнение (воздух / атмосфера)
    private BlockState getInteriorBlock(int worldX, int worldZ, int y, int ceilingY, int zoneType, boolean isSafeRoom,
                                        BlockState air, BlockState water, BlockState cobweb, BlockState pipe,
                                        BlockState ironBars, BlockState chain, BlockState ironWall, BlockState cauldron) {
        if (isSafeRoom) return air;

        switch (zoneType) {
            case ZONE_FLOODED:
                if (y == 65) {
                    long waterHash = hash(worldX, worldZ, fixedSeed + 60000L);
                    return (waterHash % 2 == 0) ? water : air;
                }
                break;

            case ZONE_PIPE:
                if (y == ceilingY - 1) {
                    long pipeHash = hash(worldX, worldZ, fixedSeed + 70000L);
                    if (Math.floorMod(worldX, 3) == 0 && pipeHash % 3 == 0) return pipe;
                }
                if (y == 66) {
                    int lx = Math.floorMod(worldX, 6);
                    int lz = Math.floorMod(worldZ, 6);
                    if (lx == 1 && lz == 1) {
                        long cwHash = hash(worldX, worldZ, fixedSeed + 80000L);
                        if (cwHash % 6 == 0) return cobweb;
                    }
                }
                break;

            case ZONE_MACHINE:
                // Машинные кластеры на полу и цепи с потолка
                if (y == 65) {
                    BlockState m = machineProp(worldX, worldZ, ironWall, cauldron, ironBars);
                    if (m != null) return m;
                }
                if (y == 66) {
                    BlockState m = machineProp(worldX, worldZ, ironWall, cauldron, ironBars);
                    // второй блок машины (только железо, не котёл)
                    if (m == ironWall) return ironWall;
                }
                if (y == ceilingY - 1) {
                    long chHash = hash(worldX, worldZ, fixedSeed + 95000L);
                    if (Math.floorMod(worldX, 5) == 0 && Math.floorMod(worldZ, 5) == 0 && chHash % 2 == 0) return chain;
                }
                break;

            case ZONE_VENT:
                // Решётки из железных прутьев поперёк проходов + паутина
                if (y == 65) {
                    long grateHash = hash(worldX, worldZ, fixedSeed + 96000L);
                    if (Math.floorMod(worldX + worldZ, 7) == 0 && grateHash % 4 == 0) return ironBars;
                }
                if (y == 66) {
                    int lx = Math.floorMod(worldX, 4);
                    int lz = Math.floorMod(worldZ, 4);
                    if (lx == 1 && lz == 1) {
                        long cwHash = hash(worldX, worldZ, fixedSeed + 97000L);
                        if (cwHash % 3 == 0) return cobweb;
                    }
                }
                break;
        }
        return air;
    }

    // Машинный проп в ячейке 14×14
    private BlockState machineProp(int worldX, int worldZ, BlockState ironWall, BlockState cauldron, BlockState ironBars) {
        int cellSize = 14;
        long cx = Math.floorDiv(worldX, cellSize);
        int lx = Math.floorMod(worldX, cellSize);
        long cz = Math.floorDiv(worldZ, cellSize);
        int lz = Math.floorMod(worldZ, cellSize);
        long cellHash = hash(cx, cz, fixedSeed + 98000L);
        if (cellHash % 3 == 0) return null; // часть ячеек пустые

        // Компактный агрегат 3×3 в центре ячейки
        if (lx >= 6 && lx <= 8 && lz >= 6 && lz <= 8) {
            if (lx == 7 && lz == 7) return cauldron;
            return ironWall;
        }
        return null;
    }

    // Потолок и освещение
    private BlockState getCeilingBlock(int worldX, int worldZ, int zoneType, boolean isSafeRoom,
                                       BlockState ceilingBlock, BlockState light, BlockState flickeringLight,
                                       BlockState brokenLight, BlockState pipe) {
        if (isSafeRoom) {
            int smx = Math.floorMod(worldX, 5);
            int smz = Math.floorMod(worldZ, 5);
            return (smx == 2 && smz == 2) ? light : ceilingBlock;
        }

        switch (zoneType) {
            case ZONE_JUNCTION: {
                int lx = Math.floorMod(worldX, 10);
                int lz = Math.floorMod(worldZ, 10);
                if (lx == 5 && lz == 5) {
                    long lh = hash(Math.floorDiv(worldX, 10), Math.floorDiv(worldZ, 10), fixedSeed + 90000L);
                    return (lh % 4 == 0) ? flickeringLight : light;
                }
                return ceilingBlock;
            }
            case ZONE_PIPE: {
                long lh = hash(Math.floorDiv(worldX, 6), Math.floorDiv(worldZ, 6), fixedSeed + 90500L);
                int lx = Math.floorMod(worldX, 6);
                int lz = Math.floorMod(worldZ, 6);
                if ((lh % 4 == 0) && lx == 3 && lz == 3) return flickeringLight;
                return ceilingBlock;
            }
            case ZONE_FLOODED: {
                long lh = hash(Math.floorDiv(worldX, 8), Math.floorDiv(worldZ, 8), fixedSeed + 91000L);
                int lx = Math.floorMod(worldX, 8);
                int lz = Math.floorMod(worldZ, 8);
                if (lx == 4 && lz == 4) {
                    if (lh % 3 == 0) return flickeringLight;
                    if (lh % 5 == 0) return brokenLight;
                }
                return ceilingBlock;
            }
            case ZONE_MACHINE: {
                // Хорошо освещённые залы + трубы по потолку
                int lx = Math.floorMod(worldX, 7);
                int lz = Math.floorMod(worldZ, 7);
                if (lx == 3 && lz == 3) return light;
                long ph = hash(worldX, worldZ, fixedSeed + 92000L);
                if (Math.floorMod(worldX, 4) == 0 && ph % 3 == 0) return pipe;
                return ceilingBlock;
            }
            case ZONE_VENT: {
                // Почти полная тьма — редкие сломанные лампы
                long lh = hash(worldX, worldZ, fixedSeed + 93000L);
                if (lh % 30 == 0) return brokenLight;
                return ceilingBlock;
            }
            default:
                return ceilingBlock;
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
        BlockState[] states = new BlockState[levelHeightAccessor.getHeight()];
        for (int i = 0; i < states.length; i++) {
            int y = levelHeightAccessor.getMinBuildHeight() + i;
            if (y < 63) states[i] = Blocks.STONE.defaultBlockState();
            else if (y == 63) states[i] = Blocks.BEDROCK.defaultBlockState();
            else if (y == 64) states[i] = Blocks.STONE_BRICKS.defaultBlockState();
            else if (y >= 65 && y <= 67) states[i] = Blocks.AIR.defaultBlockState();
            else if (y == 68) states[i] = Blocks.BRICKS.defaultBlockState();
            else if (y == 69) states[i] = Blocks.BEDROCK.defaultBlockState();
            else states[i] = Blocks.STONE.defaultBlockState();
        }
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {}
}
