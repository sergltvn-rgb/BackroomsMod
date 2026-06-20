package com.litvin.backrooms.item;

import com.litvin.backrooms.capability.SanityProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SanityPillsItem extends Item {
    public SanityPillsItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        ItemStack itemstack = super.finishUsingItem(stack, level, entityLiving);
        if (entityLiving instanceof Player player && !level.isClientSide()) {
            player.getCapability(SanityProvider.SANITY).ifPresent(sanity -> {
                sanity.setSanity(Math.min(100.0f, sanity.getSanity() + 25.0f));
            });
        }
        return itemstack;
    }
}
