package org.tcathebluecreper.totally_immersive.api.multiblock.trait;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class TraitHolder extends ArrayList<ITrait> {
    public TraitHolder() {
    }

    public TraitHolder(@NotNull Collection<? extends ITrait> c) {
        super(c);
    }

    public void save(CompoundTag tag) {
        forEach(iTrait -> iTrait.writeSaveNBT(tag));
    }
    public void load(CompoundTag tag) {
        forEach(iTrait -> iTrait.readSaveNBT(tag));
    }
}
