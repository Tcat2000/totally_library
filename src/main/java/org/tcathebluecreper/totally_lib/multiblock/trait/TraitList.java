package org.tcathebluecreper.totally_lib.multiblock.trait;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class TraitList extends ArrayList<ITrait> {
    public TraitList() {
    }

    public Optional<ITrait> get(String id) {
        return stream().filter(trait -> trait.getName().equals(id)).findFirst();
    }

    public TraitList(@NotNull Collection<? extends ITrait> c) {
        super(c);
    }

    public void save(CompoundTag tag) {
        forEach(iTrait -> iTrait.writeSaveNBT(tag));
    }
    public void load(CompoundTag tag) {
        forEach(iTrait -> iTrait.readSaveNBT(tag));
    }

    public void setOnValueChanged(Consumer<ITrait> consumer) {
        forEach(trait -> trait.setOnValueChanged(consumer));
    }
}
