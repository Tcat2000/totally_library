package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ChemicalBathState implements IMultiblockState {
    ItemStackHandler inventory;
    FluidTank tank;
    EnergyStorage energy;

    StoredCapability<IItemHandlerModifiable> input;
    StoredCapability<IItemHandlerModifiable> processSlot;
    StoredCapability<IItemHandlerModifiable> output;
    StoredCapability<ArrayFluidHandler> chemTank;
    StoredCapability<IEnergyStorage> power;

    ChemicalBathProcess process;

    public RedstoneControl.RSState redstoneState = RedstoneControl.RSState.enabledByDefault();

    public ChemicalBathState(IInitialMultiblockContext<ChemicalBathState> capabilitySource) {
        inventory = new ItemStackHandler(3);
        tank = new FluidTank(4000);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedWrapper(inventory, 0, 1));
        processSlot = new StoredCapability<>(new RangedWrapper(inventory, 1, 2));
        output = new StoredCapability<>(new RangedWrapper(inventory, 2, 3));
        chemTank = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, () -> {}));
        power = new StoredCapability<>(new WrappingEnergyStorage(energy, true, true));

        process = new ChemicalBathProcess();
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {
        compoundTag.put("inventory", inventory.serializeNBT());
        compoundTag.put("tank", tank.writeToNBT(new CompoundTag()));
        compoundTag.put("energy", energy.serializeNBT());
        compoundTag.put("process", process.serializeNBT());
    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {
        inventory.deserializeNBT(compoundTag.getCompound("inventory"));
        tank.readFromNBT(compoundTag.getCompound("tank"));
        energy.deserializeNBT(compoundTag.get("energy"));
        process = new ChemicalBathProcess(compoundTag.getCompound("process"));
    }
    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.putInt("progress", this.process.progress);
        nbt.putInt("cooldown", this.process.resetCooldown);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        redstoneState.writeSyncNBT(nbt.getCompound("redstone_mode"));
    }
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        this.process.progress = nbt.getInt("progress");
        this.process.resetCooldown = nbt.getInt("cooldown");
        this.inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.tank.readFromNBT(nbt.getCompound("tank"));
        this.redstoneState.readSyncNBT(nbt.getCompound("redstone_mode"));
    }
}