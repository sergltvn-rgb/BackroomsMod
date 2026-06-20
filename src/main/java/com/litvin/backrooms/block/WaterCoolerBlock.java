package com.litvin.backrooms.block;

import com.litvin.backrooms.capability.SanityProvider;
import com.litvin.backrooms.network.PacketHandler;
import com.litvin.backrooms.network.ClientboundSanitySyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterCoolerBlock extends FurnitureBlock {
    public WaterCoolerBlock(Properties properties, VoxelShape shape) {
        super(properties, shape);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            player.heal(2.0F); // 1 heart
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                sanity.addSanity(15.0F); // 15% sanity
                PacketHandler.sendToClient(new ClientboundSanitySyncPacket(sanity.getSanity(), sanity.isHidden()), serverPlayer);
            });
            level.playSound(null, pos, SoundEvents.GENERIC_DRINK, SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.5F, 1.2F);
            
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aВы попили холодной чистой воды. Рассудок восстановлен."));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
