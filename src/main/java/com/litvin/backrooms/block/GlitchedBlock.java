package com.litvin.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Багнутый блок — портал "ноклипа".
 * При наступании переносит игрока на следующий уровень Закулисья
 * (или внутрь с улицы, или наружу с последнего уровня).
 */
public class GlitchedBlock extends Block {
    public GlitchedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && entity instanceof ServerPlayer player) {
            if (!player.isSpectator()) {
                com.litvin.backrooms.util.LevelTransitionManager.noclipToNextLevel(player);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
