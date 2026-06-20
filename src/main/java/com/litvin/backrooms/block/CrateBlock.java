package com.litvin.backrooms.block;

import com.litvin.backrooms.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;

public class CrateBlock extends Block {
    public CrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        java.util.List<ItemStack> drops = new java.util.ArrayList<>();
        double rand = Math.random();
        if (rand < 0.6) {
            // 60% chance for Almond Water
            int count = 1 + (int)(Math.random() * 3);
            drops.add(new ItemStack(ItemInit.ALMOND_WATER.get(), count));
        } else if (rand < 0.9) {
            // 30% chance for Flashlight
            drops.add(new ItemStack(ItemInit.FLASHLIGHT.get(), 1));
        } else {
            // 10% chance for Hazmat Armor
            drops.add(new ItemStack(ItemInit.HAZMAT_HELMET.get(), 1));
        }
        return drops;
    }
}
