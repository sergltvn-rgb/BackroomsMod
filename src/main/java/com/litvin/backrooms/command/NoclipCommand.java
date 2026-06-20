package com.litvin.backrooms.command;

import com.litvin.backrooms.BackroomsMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class NoclipCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("noclip")
                .requires(source -> source.hasPermission(2)) // Требуются права оператора (опка)
                .executes(NoclipCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel backrooms = player.getServer().getLevel(ResourceKey.create(
                    Registries.DIMENSION,
                    new ResourceLocation(BackroomsMod.MODID, "level_0")
            ));

            if (backrooms != null) {
                if (player.level().dimension() == backrooms.dimension()) {
                    context.getSource().sendFailure(Component.literal("Вы уже находитесь в Закулисье!"));
                    return 0;
                }
                // Телепортируем на Уровень 0 в координаты (0.5, 65, 0.5)
                player.teleportTo(backrooms, 0.5D, 65.0D, 0.5D, player.getYRot(), player.getXRot());
                context.getSource().sendSuccess(() -> Component.literal("Вы провалились сквозь реальность..."), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("Не удалось найти измерение Закулисья!"));
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Эту команду может использовать только игрок!"));
        }
        return 0;
    }
}
