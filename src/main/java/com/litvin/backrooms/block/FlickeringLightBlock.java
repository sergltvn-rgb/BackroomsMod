package com.litvin.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FlickeringLightBlock extends FluorescentLightBlock {
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");
    public static final BooleanProperty LIT = net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

    public FlickeringLightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, true).setValue(BROKEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, BROKEN);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(BROKEN);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BROKEN)) return;

        // 12% chance to trigger a rapid flicker sequence
        if (random.nextInt(100) < 12) {
            level.scheduleTick(pos, this, 2);
            level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.05F, 2.0F);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BROKEN)) return;

        boolean nextLit = !state.getValue(LIT);

        // 4% chance to explode during rapid flicker sequence
        if (random.nextInt(100) < 4) {
            level.setBlock(pos, state.setValue(BROKEN, true).setValue(LIT, false), 3);
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.2F);
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.4F, 1.8F);
            
            // Spawn spark and smoke particles
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 0.4, 0.4, 0.4, 0.15);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.2, 0.2, 0.2, 0.02);
            return;
        }

        // 20% chance to end the flicker sequence (returns to lit state)
        if (random.nextInt(100) < 20) {
            level.setBlock(pos, state.setValue(LIT, true), 3);
        } else {
            // Toggle light state
            level.setBlock(pos, state.setValue(LIT, nextLit), 3);
            
            // Play crackling spark sound
            level.playSound(null, pos, com.litvin.backrooms.init.SoundInit.LAMP_FLICKER.get(), SoundSource.BLOCKS, 0.15F, 1.4F + random.nextFloat() * 0.4F);
            
            // Schedule next tick in 1 to 3 game ticks
            level.scheduleTick(pos, this, random.nextInt(3) + 1);
        }
    }
}
