package org.tcathebluecreper.totally_immersive.mod.Multiblock.grinder;

import blusunrize.immersiveengineering.api.energy.WrappingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.tcathebluecreper.totally_immersive.api.crafting.RangedDetectorWrapper;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeProcess;
import org.tcathebluecreper.totally_immersive.api.multiblock.TIMultiblockState;
import org.tcathebluecreper.totally_immersive.api.waila.IUnifiedWaila;
import org.tcathebluecreper.totally_immersive.api.waila.UnifiedWaila;

import java.util.List;

public class GrinderState implements TIMultiblockState<GrinderRecipe, GrinderState> {
    public static int PROCESS_TIME = 140;
    ItemStackHandler inventory;
    EnergyStorage energy;

    public StoredCapability<RangedDetectorWrapper> input;
    public StoredCapability<RangedDetectorWrapper> processSlot;
    public StoredCapability<RangedDetectorWrapper> output;
    public StoredCapability<IEnergyStorage> power;

    TIRecipeProcess<GrinderRecipe, GrinderState> process;

    public GrinderState(IInitialMultiblockContext<GrinderState> capabilitySource) {
        inventory = new ItemStackHandler(10);
        energy = new EnergyStorage(8000);

        input = new StoredCapability<>(new RangedDetectorWrapper(inventory, 0, 1));
        processSlot = new StoredCapability<>(new RangedDetectorWrapper(inventory, 1, 9));
        output = new StoredCapability<>(new RangedDetectorWrapper(inventory, 9, 10));
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
                        processSlot.getValue().setStackInSlot(parallel, ItemStack.EMPTY);
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

    @Override
    public TIRecipeProcess<GrinderRecipe, GrinderState> getRecipeProcess() {
        return process;
    }

    @Override
    public void getTOPData(BlockEntity be, ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        LazyOptional<IItemHandler> sideItemCap;
        LazyOptional<IEnergyStorage> sideEnergyCap;
        if(be instanceof MultiblockBlockEntityMaster<?>) {
            sideItemCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
            sideEnergyCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
        }
        else {
            sideItemCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
            sideEnergyCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
        }

        boolean sideHasInput = false;
        boolean sideHasProcess = false;
        boolean sideHasOutput = false;
        boolean sideHasCap = sideEnergyCap.isPresent();

        if(sideItemCap.isPresent()) {
            RangedDetectorWrapper itemCap = (RangedDetectorWrapper) sideItemCap.orElse(null);

            sideHasInput = input.getValue().compare(itemCap);
            sideHasProcess = processSlot.getValue().compare(itemCap);
            sideHasOutput = output.getValue().compare(itemCap);
        }


        if(sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(input.getValue().getStackInSlot(0));
        if(sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")));
        if(sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(output.getValue().getStackInSlot(0));
        if(sideHasCap) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_energy")).progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));

        iProbeInfo.padding(10,10);

        if(!sideHasCap) iProbeInfo.progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
        process.displayBars(iProbeInfo, PROCESS_TIME, true, true);

        if(!sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.input_items")).item(input.getValue().getStackInSlot(0));
        if(!sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.processing_items")));
        if(!sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.output_items")).item(output.getValue().getStackInSlot(0));
    }

    public void getWailaData(BlockEntity be, IUnifiedWaila waila, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
//        LazyOptional<IItemHandler> sideItemCap;
//        LazyOptional<IEnergyStorage> sideEnergyCap;
//        if(be instanceof MultiblockBlockEntityMaster<?>) {
//            sideItemCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
//            sideEnergyCap = ((MultiblockBlockEntityMaster<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
//        }
//        else {
//            sideItemCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ITEM_HANDLER, iProbeHitData.getSideHit());
//            sideEnergyCap = ((MultiblockBlockEntityDummy<?>) be).getHelper().getCapability(ForgeCapabilities.ENERGY, iProbeHitData.getSideHit());
//        }
//
//        boolean sideHasInput = false;
//        boolean sideHasProcess = false;
//        boolean sideHasOutput = false;
//        boolean sideHasCap = sideEnergyCap.isPresent();
//
//        if(sideItemCap.isPresent()) {
//            RangedDetectorWrapper itemCap = (RangedDetectorWrapper) sideItemCap.orElse(null);
//
//            sideHasInput = input.getValue().compare(itemCap);
//            sideHasProcess = processSlot.getValue().compare(itemCap);
//            sideHasOutput = output.getValue().compare(itemCap);
//        }
//
//
//        if(sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(input.getValue().getStackInSlot(0));
//        if(sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")));
//        if(sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(output.getValue().getStackInSlot(0));
//        if(sideHasCap) waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_energy")).progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), waila.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
//
//        waila.padding(10,10);
//
//        if(!sideHasCap) waila.progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), waila.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
//        process.displayBars(waila, PROCESS_TIME, true, true);
//
//        if(!sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.input_items")).item(input.getValue().getStackInSlot(0));
//        if(!sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.processing_items")));
//        if(!sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) waila.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.output_items")).item(output.getValue().getStackInSlot(0));
    }
}