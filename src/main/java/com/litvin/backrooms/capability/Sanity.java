package com.litvin.backrooms.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class Sanity implements ISanity, INBTSerializable<CompoundTag> {
    private float sanity = 100.0f;
    private final float maxSanity = 100.0f;
    private boolean hidden = false;

    @Override
    public float getSanity() {
        return sanity;
    }

    @Override
    public void setSanity(float sanity) {
        this.sanity = Math.max(0, Math.min(sanity, maxSanity));
    }

    @Override
    public void addSanity(float amount) {
        this.setSanity(this.sanity + amount);
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public void copyFrom(ISanity source) {
        this.sanity = source.getSanity();
        this.hidden = source.isHidden();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("sanity", this.sanity);
        tag.putBoolean("hidden", this.hidden);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.sanity = nbt.getFloat("sanity");
        this.hidden = nbt.getBoolean("hidden");
    }
}
