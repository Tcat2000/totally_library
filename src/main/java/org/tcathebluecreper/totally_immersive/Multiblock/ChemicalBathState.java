package org.tcathebluecreper.totally_immersive.Multiblock;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ChemicalBathState implements IMultiblockState {
    ItemStackHandler inventory;
    FluidTank tank;
    EnergyStorage energy;

    StoredCapability<IItemHandler> input;
    StoredCapability<IItemHandler> output;
    StoredCapability<ArrayFluidHandler> chemTank;
    StoredCapability<IEnergyStorage> power;

    int processingTime = 0;

    public ChemicalBathState(IInitialMultiblockContext<ChemicalBathState> capabilitySource) {
        inventory = new ItemStackHandler(2);
        tank = new FluidTank(4000);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedWrapper(inventory, 0, 1));
        output = new StoredCapability<>(new RangedWrapper(inventory, 1, 2));
        chemTank = new StoredCapability<>(ArrayFluidHandler.drainOnly(tank, () -> {}));
        power = new StoredCapability<>(new WrappingEnergyStorage(energy, true, false));
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {
        compoundTag.put("inventory", inventory.serializeNBT());
        compoundTag.put("tank", tank.writeToNBT(new CompoundTag()));
        compoundTag.put("energy", energy.serializeNBT());
        compoundTag.putInt("progress", processingTime);
    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {
        inventory.deserializeNBT(compoundTag.getCompound("inventory"));
        tank.readFromNBT(compoundTag.getCompound("tank"));
        energy.deserializeNBT(compoundTag.get("energy"));
        processingTime = compoundTag.getInt("progress");
    }
}
