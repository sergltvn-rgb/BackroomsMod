package com.litvin.backrooms.block;

import com.litvin.backrooms.init.SoundInit;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FluorescentLightBlock extends Block {
    public FluorescentLightBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Каждые несколько тиков проигрываем тихий звук гудения на клиенте
        if (random.nextInt(80) == 0) {
            level.playLocalSound(
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                SoundInit.FLUORESCENT_BUZZ.get(),
                SoundSource.BLOCKS,
                0.03F, // Сильно уменьшенная громкость, чтобы не выедало уши
                1.0F,  // Высота тона
                false
            );
        }
    }
}
