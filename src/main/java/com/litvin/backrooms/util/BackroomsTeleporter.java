package com.litvin.backrooms.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class BackroomsTeleporter {

    /**
     * Безопасные комнаты всех уровней расположены у начала координат уровня.
     * Область 0..24 по X и Z покрывает зону спавна (safe room) каждого уровня.
     */
    public static boolean isInSafeRoom(net.minecraft.world.entity.Entity entity) {
        double x = entity.getX();
        double z = entity.getZ();
        return x >= 0.0D && x < 24.0D && z >= 0.0D && z < 24.0D;
    }

    public static void teleportToSafePosition(ServerPlayer player, ServerLevel targetLevel, double x, double z) {
        BlockPos safePos = findSafeSpawnPosition(targetLevel, x, z);
        player.fallDistance = 0.0F;
        player.setDeltaMovement(0.0D, 0.0D, 0.0D);
        player.hurtMarked = true;
        
        // Teleport player to the center of the safe block
        player.teleportTo(targetLevel, safePos.getX() + 0.5D, safePos.getY() + 0.1D, safePos.getZ() + 0.5D, player.getYRot(), player.getXRot());
    }

    public static BlockPos findSafeSpawnPosition(ServerLevel level, double x, double z) {
        int startX = (int) Math.floor(x);
        int startZ = (int) Math.floor(z);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Search in a spiral/expanding square up to 15 blocks
        for (int r = 0; r <= 15; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) == r || Math.abs(dz) == r) {
                        int checkX = startX + dx;
                        int checkZ = startZ + dz;

                        // Search Y range from Y=64 (ground) to Y=72 (ceiling)
                        for (int checkY = 64; checkY <= 72; checkY++) {
                            pos.set(checkX, checkY, checkZ);
                            BlockState legs = level.getBlockState(pos);
                            BlockState head = level.getBlockState(pos.above());
                            BlockState floor = level.getBlockState(pos.below());

                            // Safe if floor is solid (non-air), and legs and head are passable (air)
                            if (!floor.isAir() && legs.isAir() && head.isAir()) {
                                return pos.immutable();
                            }
                        }
                    }
                }
            }
        }

        // Fallback
        return new BlockPos(startX, 65, startZ);
    }
}
