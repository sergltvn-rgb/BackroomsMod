package com.litvin.backrooms.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AlmondWaterItem extends Item {
    public AlmondWaterItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            // Очищаем негативные эффекты, часто встречающиеся в опасных измерениях
            entity.removeEffect(MobEffects.HUNGER);
            entity.removeEffect(MobEffects.POISON);
            entity.removeEffect(MobEffects.WITHER);
            entity.removeEffect(MobEffects.WEAKNESS);
            entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            entity.removeEffect(MobEffects.CONFUSION);

            // Накладываем полезные эффекты (Регенерация II на 10 секунд и Насыщение)
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0));

            // Восстанавливаем рассудок
            entity.getCapability(com.litvin.backrooms.capability.SanityProvider.SANITY).ifPresent(sanity -> {
                sanity.addSanity(30.0f);
            });
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32; // Время питья как у бутылочки с водой
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }
}
