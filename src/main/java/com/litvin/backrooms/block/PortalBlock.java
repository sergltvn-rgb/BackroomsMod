package com.litvin.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;

public class PortalBlock extends Block {

    public PortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pLevel.isClientSide && pEntity instanceof ServerPlayer player) {
            MinecraftServer server = pLevel.getServer();
            if (server != null) {
                ResourceKey<Level> currentDim = pLevel.dimension();
                ResourceKey<Level> nextDim = getNextDimension(currentDim);
                
                if (nextDim != null) {
                    ServerLevel nextLevel = server.getLevel(nextDim);
                    if (nextLevel != null) {
                        player.teleportTo(nextLevel, player.getX(), 120, player.getZ(), player.getYRot(), player.getXRot());
                    }
                }
            }
        }
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    private ResourceKey<Level> getNextDimension(ResourceKey<Level> current) {
        if (current.location().getPath().equals("level_0")) {
            return ResourceKey.create(Registries.DIMENSION, new ResourceLocation("backroomsmod", "level_1"));
        } else if (current.location().getPath().equals("level_1")) {
            return ResourceKey.create(Registries.DIMENSION, new ResourceLocation("backroomsmod", "level_2"));
        } else if (current.location().getPath().equals("level_2")) {
            return ResourceKey.create(Registries.DIMENSION, new ResourceLocation("backroomsmod", "level_37"));
        }
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation("backroomsmod", "level_0"));
    }
}
