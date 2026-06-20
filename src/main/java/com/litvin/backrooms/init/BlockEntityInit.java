package com.litvin.backrooms.init;

import com.litvin.backrooms.BackroomsMod;
import com.litvin.backrooms.block.entity.FileCabinetBlockEntity;
import com.litvin.backrooms.block.entity.LockerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BackroomsMod.MODID);

    public static final RegistryObject<BlockEntityType<FileCabinetBlockEntity>> FILE_CABINET_BLOCK_ENTITY = BLOCK_ENTITIES.register("file_cabinet_block_entity",
            () -> BlockEntityType.Builder.of(FileCabinetBlockEntity::new, BlockInit.FILE_CABINET.get()).build(null));

    public static final RegistryObject<BlockEntityType<LockerBlockEntity>> LOCKER_BLOCK_ENTITY = BLOCK_ENTITIES.register("locker_block_entity",
            () -> BlockEntityType.Builder.of(LockerBlockEntity::new, BlockInit.LOCKER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
