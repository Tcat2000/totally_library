package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.nbt.CompoundTag;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitHolder;

import java.util.ArrayList;
import java.util.List;

public class TraitMultiblockState implements IMultiblockState {
    public final TraitHolder traits;
    public CompoundTag customData = new CompoundTag();

    public TraitMultiblockState(TraitHolder traits) {
        this.traits = traits;
    }

    public TraitMultiblockState(IInitialMultiblockContext capSource, List<ITrait> traits) {
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
        traits.save(nbt);
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
