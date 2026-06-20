package com.litvin.backrooms.util;

import com.litvin.backrooms.BackroomsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Центральный менеджер переходов между уровнями Закулисья.
 *
 * Механика "ноклипа": игрок проваливается сквозь реальность последовательно
 * Уровень 0 -> 1 -> 2 -> 3 -> 4, а с последнего уровня может выбраться обратно
 * в обычный мир (побег из Закулисья).
 */
public class LevelTransitionManager {

    // Последовательность уровней Закулисья (порядок спуска)
    public static final String[] LEVELS = {"level_0", "level_1", "level_2", "level_3", "level_4"};

    private LevelTransitionManager() {}

    public static ResourceKey<Level> levelKey(String name) {
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(BackroomsMod.MODID, name));
    }

    /** Индекс уровня по измерению, либо -1 если это не Закулисье. */
    public static int getLevelIndex(ResourceLocation dim) {
        if (dim == null || !dim.getNamespace().equals(BackroomsMod.MODID)) return -1;
        String path = dim.getPath();
        for (int i = 0; i < LEVELS.length; i++) {
            if (LEVELS[i].equals(path)) return i;
        }
        return -1;
    }

    /**
     * Ноклип на следующий уровень.
     * Из обычного мира — вход на Уровень 0.
     * С последнего уровня — побег в обычный мир.
     */
    public static void noclipToNextLevel(ServerPlayer player) {
        if (player.getServer() == null) return;
        int idx = getLevelIndex(player.level().dimension().location());

        if (idx < 0) {
            descendTo(player, 0, true);
        } else if (idx >= LEVELS.length - 1) {
            escapeToOverworld(player);
        } else {
            descendTo(player, idx + 1, false);
        }
    }

    private static void descendTo(ServerPlayer player, int idx, boolean fromOutside) {
        ServerLevel target = player.getServer().getLevel(levelKey(LEVELS[idx]));
        if (target == null) return;

        double rx, rz;
        if (fromOutside) {
            // Вход с улицы — в безопасную комнату у начала координат
            rx = 11.5D;
            rz = 11.5D;
        } else {
            // Спуск между уровнями — со случайным смещением, чтобы не толпиться в одной точке
            rx = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 256.0D;
            rz = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 256.0D;
        }

        BackroomsTeleporter.teleportToSafePosition(player, target, rx, rz);

        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false));
        target.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);

        String msg;
        if (fromOutside) {
            msg = "\u00a7eВы провалились сквозь реальность и очутились в Закулисье...";
        } else {
            msg = "\u00a7cВы провалились сквозь текстуры — Уровень " + idx + "...";
        }
        player.sendSystemMessage(Component.literal(msg));
    }

    private static void escapeToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos spawn = overworld.getSharedSpawnPos();
        int y = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());

        player.fallDistance = 0.0F;
        player.setDeltaMovement(0.0D, 0.0D, 0.0D);
        player.hurtMarked = true;
        player.teleportTo(overworld, spawn.getX() + 0.5D, y + 0.1D, spawn.getZ() + 0.5D, player.getYRot(), player.getXRot());

        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
        overworld.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sendSystemMessage(Component.literal("\u00a7aВы наконец нашли выход из Закулисья и вернулись домой!"));
    }
}
