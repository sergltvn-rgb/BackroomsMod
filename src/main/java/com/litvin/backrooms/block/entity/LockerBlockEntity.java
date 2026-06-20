package com.litvin.backrooms.block.entity;

import com.litvin.backrooms.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class LockerBlockEntity extends BlockEntity {
    private UUID occupant = null;

    public LockerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.LOCKER_BLOCK_ENTITY.get(), pos, state);
    }

    public UUID getOccupant() {
        return occupant;
    }

    public void setOccupant(UUID occupant) {
        this.occupant = occupant;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (occupant != null) {
            tag.putUUID("Occupant", occupant);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("Occupant")) {
            occupant = tag.getUUID("Occupant");
        } else {
            occupant = null;
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
