package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.nbt.CompoundTag;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitHolder;

import java.util.ArrayList;

public class TraitMultiblockState implements IMultiblockState {
    final TraitHolder traits;
    public CompoundTag customData;

    public TraitMultiblockState(TraitHolder traits) {
        this.traits = traits;
    }

    public TraitMultiblockState(ArrayList<ITrait> traits) {
        this.traits = new TraitHolder(traits);
    }


    @Override
    public void writeSaveNBT(CompoundTag nbt) {
        nbt.put("data", customData);
        traits.save(nbt);
    }

    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.put("data", customData);
        traits.load(nbt);
    }

    @Override
    public void readSaveNBT(CompoundTag nbt) {
        customData = nbt.getCompound("data");
        traits.load(nbt);
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        customData = nbt.getCompound("data");
        traits.load(nbt);
    }
}
