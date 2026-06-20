package com.litvin.backrooms.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Фонарик: ПКМ — вкл/выкл. Пока включён, даёт ночное зрение и расходует заряд.
 * Shift+ПКМ — перезарядка батарейкой из инвентаря.
 */
public class FlashlightItem extends Item {
    private static final String TAG_ON = "FlashlightOn";

    public FlashlightItem(Properties properties) {
        super(properties);
    }

    public static boolean isOn(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getBoolean(TAG_ON);
    }

    private static void setOn(ItemStack stack, boolean on) {
        stack.getOrCreateTag().putBoolean(TAG_ON, on);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift+ПКМ — перезарядка
        if (player.isShiftKeyDown()) {
            if (stack.getDamageValue() > 0 && consumeBattery(player)) {
                stack.setDamageValue(0);
                if (!level.isClientSide()) {
                    level.playSound(null, player.blockPosition(), SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 0.8F, 1.2F);
                    player.displayClientMessage(Component.translatable("item.backroomsmod.flashlight.recharged"), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
            return InteractionResultHolder.pass(stack);
        }

        // ПКМ — переключение
        boolean newState = !isOn(stack);
        if (newState && stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("item.backroomsmod.flashlight.empty"), true);
            }
            return InteractionResultHolder.pass(stack);
        }
        setOn(stack, newState);
        if (!level.isClientSide()) {
            level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.6F, newState ? 1.4F : 0.9F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private boolean consumeBattery(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(com.litvin.backrooms.init.ItemInit.BATTERY.get())) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        if (!isOn(stack)) return;

        // Размещение динамического света фонарика
        // Пытаемся поставить свет на уровне глаз
        net.minecraft.core.BlockPos pos = player.blockPosition().above();
        if (level.getBlockState(pos).canBeReplaced() && level.getBlockState(pos).getBlock() != com.litvin.backrooms.init.BlockInit.FLASHLIGHT_LIGHT.get()) {
            level.setBlock(pos, com.litvin.backrooms.init.BlockInit.FLASHLIGHT_LIGHT.get().defaultBlockState(), 3);
        }

        // Расход заряда раз в секунду
        if (level.getGameTime() % 20L == 0L) {
            int dmg = stack.getDamageValue() + 1;
            if (dmg >= stack.getMaxDamage()) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
                setOn(stack, false);
                level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.5F, 0.6F);
                player.displayClientMessage(Component.translatable("item.backroomsmod.flashlight.died"), true);
            } else {
                stack.setDamageValue(dmg);
            }
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Жёлтый индикатор заряда
        return 0xFFE000;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        int charge = stack.getMaxDamage() - stack.getDamageValue();
        tooltip.add(Component.translatable("item.backroomsmod.flashlight.charge", charge, stack.getMaxDamage()));
        tooltip.add(isOn(stack)
                ? Component.translatable("item.backroomsmod.flashlight.state_on")
                : Component.translatable("item.backroomsmod.flashlight.state_off"));
        tooltip.add(Component.translatable("item.backroomsmod.flashlight.hint"));
    }
}
