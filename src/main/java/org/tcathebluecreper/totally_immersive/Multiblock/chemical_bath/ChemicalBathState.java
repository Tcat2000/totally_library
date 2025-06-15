package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.tcathebluecreper.totally_immersive.api.crafting.RangedDetectorWrapper;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeProcess;

import java.util.List;

public class ChemicalBathState implements IMultiblockState {
    ItemStackHandler inventory;
    FluidTank tank;
    EnergyStorage energy;

    StoredCapability<IItemHandlerModifiable> input;
    StoredCapability<IItemHandlerModifiable> processSlot;
    StoredCapability<IItemHandlerModifiable> output;
    StoredCapability<ArrayFluidHandler> chemTank;
    StoredCapability<IEnergyStorage> power;

    TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState> TIProcess;

    public RedstoneControl.RSState redstoneState = RedstoneControl.RSState.enabledByDefault();

    public ChemicalBathState(IInitialMultiblockContext<ChemicalBathState> capabilitySource) {
        TIProcess = createProcess(0);

        inventory = new ItemStackHandler(3);
        tank = new FluidTank(4000);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedDetectorWrapper(inventory, 0, 1, (mode, stack) -> TIProcess.triggerUpdate()));
        processSlot = new StoredCapability<>(new RangedDetectorWrapper(inventory, 1, 2, (mode, stack) -> TIProcess.triggerUpdate()));
        output = new StoredCapability<>(new RangedDetectorWrapper(inventory, 2, 3, (mode, stack) -> TIProcess.triggerUpdate()));
        chemTank = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, TIProcess::triggerUpdate));
        power = new StoredCapability<>(new WrappingEnergyStorage(energy, true, true));
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {
        compoundTag.put("inventory", inventory.serializeNBT());
        compoundTag.put("tank", tank.writeToNBT(new CompoundTag()));
        compoundTag.put("energy", energy.serializeNBT());
    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {
        inventory.deserializeNBT(compoundTag.getCompound("inventory"));
        tank.readFromNBT(compoundTag.getCompound("tank"));
        energy.deserializeNBT(compoundTag.get("energy"));
    }

    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        redstoneState.writeSyncNBT(nbt.getCompound("redstone_mode"));
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.tank.readFromNBT(nbt.getCompound("tank"));
        this.redstoneState.readSyncNBT(nbt.getCompound("redstone_mode"));
    }

    protected TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState> createProcess(int tick) {
        return new TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState>(
            ChemicalBathRecipe.class,
            List.of(
                new TIRecipeProcess.TickAction<ChemicalBathRecipe, ChemicalBathState>(0, ((process, parallel) -> {
                    processSlot.getValue().setStackInSlot(0, process.recipe[0].input.extractFrom(input.getValue().getStackInSlot(0)));
                    return true;
                })),
                new TIRecipeProcess.TickAction<ChemicalBathRecipe, ChemicalBathState>(140, ((process, parallel) -> {
                    processSlot.getValue().setStackInSlot(0, ItemStack.EMPTY);
                    ItemStack stack = process.state.output.getValue().getStackInSlot(0);
                    process.recipe[0].output.insertTo(output.getValue(), 0);
                    return true;
                }))
            ),
            this,
            (process, parallel) -> {
                if(process.recipe[0] == null) return false;
                if(process.state.power.getValue().extractEnergy(process.recipe[0].energyCost.value, true) == process.recipe[0].energyCost.value) {
                    process.state.power.getValue().extractEnergy(process.recipe[0].energyCost.value, false);
                    return true;
                }
                return false;
            },
            tick
        );
    }
}