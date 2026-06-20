package com.litvin.backrooms.block;

import com.litvin.backrooms.block.entity.LockerBlockEntity;
import com.litvin.backrooms.capability.SanityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LockerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public LockerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE; // simple box for now, could be refined
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            return neighborState.is(this) && neighborState.getValue(HALF) != half ? state.setValue(FACING, neighborState.getValue(FACING)).setValue(OPEN, neighborState.getValue(OPEN)) : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        } else {
            return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(level, currentPos) ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Find the bottom half to get the block entity
            BlockPos bottomPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockEntity be = level.getBlockEntity(bottomPos);
            
            if (be instanceof LockerBlockEntity locker) {
                // If the player is already hidden in a locker, they want to exit
                player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                    if (sanity.isHidden() && locker.getOccupant() != null && locker.getOccupant().equals(player.getUUID())) {
                        // Exit locker
                        sanity.setHidden(false);
                        locker.setOccupant(null);
                        level.setBlock(bottomPos, level.getBlockState(bottomPos).setValue(OPEN, false), 3);
                        level.setBlock(bottomPos.above(), level.getBlockState(bottomPos.above()).setValue(OPEN, false), 3);
                        level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        // Teleport slightly outside the locker
                        Direction facing = level.getBlockState(bottomPos).getValue(FACING);
                        serverPlayer.teleportTo(bottomPos.getX() + 0.5 + facing.getStepX() * 0.8, bottomPos.getY(), bottomPos.getZ() + 0.5 + facing.getStepZ() * 0.8);
                    } else if (!sanity.isHidden() && locker.getOccupant() == null) {
                        // Enter locker
                        sanity.setHidden(true);
                        locker.setOccupant(player.getUUID());
                        level.setBlock(bottomPos, level.getBlockState(bottomPos).setValue(OPEN, true), 3);
                        level.setBlock(bottomPos.above(), level.getBlockState(bottomPos.above()).setValue(OPEN, true), 3);
                        level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        // Teleport inside the locker
                        serverPlayer.teleportTo(bottomPos.getX() + 0.5, bottomPos.getY() + 0.1, bottomPos.getZ() + 0.5);
                    }
                });
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos bottomPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            BlockEntity be = level.getBlockEntity(bottomPos);
            if (be instanceof LockerBlockEntity locker && locker.getOccupant() != null) {
                Player player = level.getPlayerByUUID(locker.getOccupant());
                if (player != null) {
                    player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> sanity.setHidden(false));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new LockerBlockEntity(pos, state);
        }
        return null;
    }
}
