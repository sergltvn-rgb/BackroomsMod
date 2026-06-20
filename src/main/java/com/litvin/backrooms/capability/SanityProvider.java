package com.litvin.backrooms.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SanityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<ISanity> SANITY = CapabilityManager.get(new CapabilityToken<ISanity>() {});

    private ISanity sanity = null;
    private final LazyOptional<ISanity> optional = LazyOptional.of(this::createSanity);

    private ISanity createSanity() {
        if (this.sanity == null) {
            this.sanity = new Sanity();
        }
        return this.sanity;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SANITY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return ((Sanity) createSanity()).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ((Sanity) createSanity()).deserializeNBT(nbt);
    }
}
