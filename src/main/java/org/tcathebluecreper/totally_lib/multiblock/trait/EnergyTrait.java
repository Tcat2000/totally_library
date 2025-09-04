package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyTrait extends TLTrait {
    public final String name;

    StoredCapability<EnergyStorage> storage;

    public EnergyTrait(String name, int maxPower) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, 1000));
    }

    public EnergyTrait(String name, int maxPower, int maxTransfer) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxTransfer));
    }

    public EnergyTrait(String name, int maxPower, int maxInsert, int maxExtract) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxInsert, maxExtract));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        if(tag.contains(getName())) storage.getValue().deserializeNBT(tag.get(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), storage.getValue().serializeNBT());
    }

    @Override
    public StoredCapability<?> getCap() {
        return storage;
    }
}
