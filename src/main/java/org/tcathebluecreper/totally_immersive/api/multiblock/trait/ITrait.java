package org.tcathebluecreper.totally_immersive.api.multiblock.trait;

import net.minecraft.nbt.CompoundTag;

public interface ITrait {
    String getName();
    void readSaveNBT(CompoundTag tag);
    void writeSaveNBT(CompoundTag tag);
}
