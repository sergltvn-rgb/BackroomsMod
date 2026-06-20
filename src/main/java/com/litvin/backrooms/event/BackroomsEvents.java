package com.litvin.backrooms.event;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.command.NoclipCommand;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BackroomsEvents {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        NoclipCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Выполняем проверку только в конце тика на стороне сервера для реального игрока
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide() && event.player instanceof ServerPlayer player) {
            Level level = player.level();
            ResourceKey<Level> dim = level.dimension();

            // Если игрок в Обычном мире или Энде падает ниже Y = -64 (в Бездну)
            if ((dim == Level.OVERWORLD || dim == Level.END) && player.getY() < -64.0D) {
                ServerLevel backrooms = player.getServer().getLevel(ResourceKey.create(
                        Registries.DIMENSION,
                        new ResourceLocation(BackroomsMod.MODID, "level_0")
                ));

                if (backrooms != null) {
                    // Телепортируем на Уровень 0 в центр безопасной комнаты (не в угол)
                    com.litvin.backrooms.util.BackroomsTeleporter.teleportToSafePosition(player, backrooms, 11.5D, 11.5D);

                    // Выводим сообщение в чат желтым цветом (стиль Закулисья)
                    player.sendSystemMessage(Component.literal("§eВы выпали из реальности и очутились в Закулисье..."));
                }
            }
        }
    }
}
