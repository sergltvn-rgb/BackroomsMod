package com.litvin.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingVentBlock extends FurnitureBlock {
    public CeilingVentBlock(Properties properties, VoxelShape shape) {
        super(properties, shape);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(12) == 0) {
            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.05D;
            double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;
            level.addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
