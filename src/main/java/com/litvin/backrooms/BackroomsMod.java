package com.litvin.backrooms;

import com.litvin.backrooms.init.*;
import com.litvin.backrooms.event.BackroomsEvents;
import com.litvin.backrooms.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BackroomsMod.MODID)
public class BackroomsMod {
    public static final String MODID = "backroomsmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public BackroomsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Регистрируем все инициализаторы на шине событий мода
        ItemInit.ITEMS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        SoundInit.SOUNDS.register(modEventBus);
        GeneratorInit.CHUNK_GENERATORS.register(modEventBus);
        com.litvin.backrooms.init.EffectInit.EFFECTS.register(modEventBus);
        CreativeTabInit.TABS.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);

        software.bernie.geckolib.GeckoLib.initialize();

        // Инициализируем сетевые пакеты
        PacketHandler.register();

        // Регистрируем игровой обработчик событий на шине Forge
        MinecraftForge.EVENT_BUS.register(new BackroomsEvents());
    }
}

