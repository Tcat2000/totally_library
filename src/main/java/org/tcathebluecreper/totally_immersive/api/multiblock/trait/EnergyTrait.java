package org.tcathebluecreper.totally_immersive.api.multiblock.trait;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyTrait implements ITrait {
    public final String name;

    EnergyStorage storage;

    public EnergyTrait(String name, int maxPower) {
        this.name = name;
        this.storage = new EnergyStorage(maxPower, 1000);
    }

    public EnergyTrait(String name, int maxPower, int maxTransfer) {
        this.name = name;
        this.storage = new EnergyStorage(maxPower, maxTransfer);
    }

    public EnergyTrait(String name, int maxPower, int maxInsert, int maxExtract) {
        this.name = name;
        this.storage = new EnergyStorage(maxPower, maxInsert, maxExtract);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        storage.deserializeNBT(tag.get(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), storage.serializeNBT());
    }
}
