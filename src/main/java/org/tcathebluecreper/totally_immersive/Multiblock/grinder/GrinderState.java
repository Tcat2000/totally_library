package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeProcess;

import java.util.List;

public class GrinderState implements IMultiblockState {
    ItemStackHandler inventory;
    EnergyStorage energy;

    public StoredCapability<IItemHandlerModifiable> input;
    public StoredCapability<IItemHandlerModifiable> processSlot;
    public StoredCapability<IItemHandlerModifiable> output;
    public StoredCapability<IEnergyStorage> power;

    TIRecipeProcess<GrinderRecipe, GrinderState> process;

    public GrinderState(IInitialMultiblockContext<GrinderState> capabilitySource) {
        inventory = new ItemStackHandler(10);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedWrapper(inventory, 0, 1));
        processSlot = new StoredCapability<>(new RangedWrapper(inventory, 1, 9));
        output = new StoredCapability<>(new RangedWrapper(inventory, 9, 10));
        power = new StoredCapability<>(new WrappingEnergyStorage(energy, true, true));

        process = createProcess(0);
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put("inventory", inventory.serializeNBT());
        tag.put("energy", energy.serializeNBT());
        tag.put("process", process.serialize());
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        inventory.deserializeNBT(tag.getCompound("inventory"));
        energy.deserializeNBT(tag.get("energy"));
        process.deserialize(tag.getCompound("process"));
    }
    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("process", process.serialize());
    }
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("inventory"));
        process.deserialize(nbt.getCompound("process"));
    }

    protected TIRecipeProcess<GrinderRecipe, GrinderState> createProcess(int tick) {
        return new TIRecipeProcess<>(
            GrinderRecipe.class,
            List.of(
                new TIRecipeProcess.TickAction<>(0, ((process, parallel) -> {
                    if(process.recipe[0].input.canExtractFrom(input.getValue().getStackInSlot(0))) {
                        processSlot.getValue().setStackInSlot(parallel, process.recipe[0].input.extractFrom(input.getValue().getStackInSlot(0)));
                        process.stuck[parallel] = false;
                    }
                    else process.stuck[parallel] = true;
                })),
                new TIRecipeProcess.TickAction<>(140, ((process, parallel) -> {
                    if(process.recipe[0].output.insertTo(output.getValue(), 0)) {
                        processSlot.getValue().setStackInSlot(0, ItemStack.EMPTY);
                        process.tick[parallel] = 0;
                        process.stuck[parallel] = false;
                        return true;
                    }
                    process.stuck[parallel] = true;
                    return false;
                }))
            ),
            this,
            (process, parallel) -> {
                if(process.recipe[process.getRecipeIndex(parallel)] == null) return false;
                if(process.state.power.getValue().extractEnergy(process.recipe[0].energyCost.value, true) == process.recipe[0].energyCost.value) {
                    process.state.power.getValue().extractEnergy(process.recipe[0].energyCost.value, false);
                    return true;
                }
                return false;
            },
            new int[8],
            8,
            false
        );
    }
}