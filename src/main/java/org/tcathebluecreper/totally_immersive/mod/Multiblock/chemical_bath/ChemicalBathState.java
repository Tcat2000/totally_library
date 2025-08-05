package org.tcathebluecreper.totally_immersive.mod.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.NumberFormat;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.tcathebluecreper.totally_immersive.api.crafting.RangedDetectorWrapper;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeProcess;
import org.tcathebluecreper.totally_immersive.api.multiblock.TIMultiblockState;

import java.util.List;

public class ChemicalBathState implements TIMultiblockState<ChemicalBathRecipe, ChemicalBathState> {
    public static final int RESET_TIME = 40;
    public static final int PROCESS_TIME = 160;
    ItemStackHandler inventory;
    FluidTank tank;
    EnergyStorage energy;

    StoredCapability<RangedDetectorWrapper> input;
    StoredCapability<RangedDetectorWrapper> processSlot;
    StoredCapability<RangedDetectorWrapper> output;
    StoredCapability<ArrayFluidHandler> chemTank;
    StoredCapability<IEnergyStorage> power;

    TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState> process;

    public RedstoneControl.RSState redstoneState = RedstoneControl.RSState.enabledByDefault();

    public ChemicalBathState(IInitialMultiblockContext<ChemicalBathState> capabilitySource) {
        process = createProcess(0);

        inventory = new ItemStackHandler(3);
        tank = new FluidTank(4000);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedDetectorWrapper(inventory, 0, 1, (mode, stack) -> process.triggerUpdate()));
        processSlot = new StoredCapability<>(new RangedDetectorWrapper(inventory, 1, 2, (mode, stack) -> process.triggerUpdate()));
        output = new StoredCapability<>(new RangedDetectorWrapper(inventory, 2, 3, (mode, stack) -> process.triggerUpdate()));
        chemTank = new StoredCapability<>(new ArrayFluidHandler(tank, true, true, process::triggerUpdate));
        power = new StoredCapability<>(new WrappingEnergyStorage(energy, true, true));
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {
        compoundTag.put("inventory", inventory.serializeNBT());
        compoundTag.put("tank", tank.writeToNBT(new CompoundTag()));
        compoundTag.put("energy", energy.serializeNBT());
        compoundTag.put("process", process.serialize());
    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {
        inventory.deserializeNBT(compoundTag.getCompound("inventory"));
        tank.readFromNBT(compoundTag.getCompound("tank"));
        energy.deserializeNBT(compoundTag.get("energy"));
        process.deserialize(compoundTag.getCompound("process"));
    }

    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        nbt.put("inventory", inventory.serializeNBT());
        nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        redstoneState.writeSyncNBT(nbt.getCompound("redstone_mode"));
        nbt.put("process", process.serialize());
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("inventory"));
        this.tank.readFromNBT(nbt.getCompound("tank"));
        this.redstoneState.readSyncNBT(nbt.getCompound("redstone_mode"));
        process.deserialize(nbt.getCompound("process"));
    }

    protected TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState> createProcess(int tick) {
        return new TIRecipeProcess<>(
            ChemicalBathRecipe.class,
            List.of(
                new TIRecipeProcess.TickAction<>(0, ((process, parallel) -> {
                    if(process.tick[parallel] != 0) return;
                    if(process.recipe[0].input.canExtractFrom(input.getValue().getStackInSlot(0))) {
                        processSlot.getValue().setStackInSlot(parallel, process.recipe[0].input.extractFrom(input.getValue().getStackInSlot(0)));
                        process.stuck[parallel] = false;
                    }
                    else process.stuck[parallel] = true;
                })),
                new TIRecipeProcess.TickAction<>(140, ((process, parallel) -> {
                    if(process.recipe[0].output.insertTo(output.getValue(), 0)) {
                       processSlot.getValue().setStackInSlot(0, ItemStack.EMPTY);
                       process.stuck[parallel] = false;
                    }
                    else process.stuck[parallel] = true;
                })),
                new TIRecipeProcess.TickAction<>(200, ((process, parallel) -> true))
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

    @Override
    public TIRecipeProcess<ChemicalBathRecipe, ChemicalBathState> getRecipeProcess() {
        return process;
    }

    @Override
    public void getTOPData(BlockEntity be, ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        LazyOptional<IItemHandler> sideItemCap;
        LazyOptional<IFluidHandler> sideFluidCap;
        LazyOptional<IEnergyStorage> sideEnergyCap;
        if(be instanceof MultiblockBlockEntityMaster<?>) {
            sideItemCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
            sideFluidCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.FLUID_HANDLER, iProbeHitData.getSideHit());
            sideEnergyCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
        }
        else {
            sideItemCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
            sideFluidCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.FLUID_HANDLER, iProbeHitData.getSideHit());
            sideEnergyCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
        }

        boolean sideHasInput = false;
        boolean sideHasProcess = false;
        boolean sideHasOutput = false;
        boolean sideHasTank = sideFluidCap.isPresent();
        boolean sideHasCap = sideEnergyCap.isPresent();

        if(sideItemCap.isPresent()) {
            RangedDetectorWrapper itemCap = (RangedDetectorWrapper) sideItemCap.orElse(null);

            sideHasInput = input.getValue().compare(itemCap);
            sideHasProcess = processSlot.getValue().compare(itemCap);
            sideHasOutput = output.getValue().compare(itemCap);
        }


        if(sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(input.getValue().getStackInSlot(0));
        if(sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(processSlot.getValue().getStackInSlot(0));
        if(sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(output.getValue().getStackInSlot(0));
        if(sideHasTank) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_fluids")).tank(tank);
        if(sideHasCap) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_energy")).progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));

        iProbeInfo.padding(10,10);

        if(!sideHasTank) iProbeInfo.tank(tank);
        if(!sideHasCap) iProbeInfo.progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
        iProbeInfo.progress(process.tick[0], PROCESS_TIME);

        if(!sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.input_items")).item(input.getValue().getStackInSlot(0));
        if(!sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.processing_items")).item(processSlot.getValue().getStackInSlot(0));
        if(!sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.output_items")).item(output.getValue().getStackInSlot(0));
    }
}