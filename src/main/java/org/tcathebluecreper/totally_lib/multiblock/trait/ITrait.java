package org.tcathebluecreper.totally_lib.multiblock.trait;

import net.minecraft.nbt.CompoundTag;

public interface ITrait {
    String getName();
    void readSaveNBT(CompoundTag tag);
    void writeSaveNBT(CompoundTag tag);

    default <T> T nullDefault(T main, T fallback) {
        if(main == null) return fallback;
        return main;
    }
}
