package org.tcathebluecreper.totally_lib.display_info;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StandardInfo {
    public void standard(BlockEntity be, ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData, int processLength) {
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
//        if(sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(input.getValue().getStackInSlot(0));
//        if(sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")));
//        if(sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_items")).item(output.getValue().getStackInSlot(0));
//        if(sideHasCap) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.local_energy")).progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
//
//        iProbeInfo.padding(10,10);
//
//        if(!sideHasCap) iProbeInfo.progress(energy.getEnergyStored(), energy.getMaxEnergyStored(), iProbeInfo.defaultProgressStyle().suffix("FE").filledColor(Config.rfbarFilledColor).alternateFilledColor(Config.rfbarAlternateFilledColor).borderColor(Config.rfbarBorderColor).numberFormat(Config.rfFormat.get()));
//        process.displayBars(iProbeInfo, processLength, true, true);
//
//        if(!sideHasInput && !input.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.input_items")).item(input.getValue().getStackInSlot(0));
//        if(!sideHasProcess && !processSlot.getValue().getStackInSlot(0).isEmpty()) processSlot.getValue().displayItems(iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.processing_items")));
//        if(!sideHasOutput && !output.getValue().getStackInSlot(0).isEmpty()) iProbeInfo.vertical().text(Component.translatable("top.totally_immersive.chemical_bath.output_items")).item(output.getValue().getStackInSlot(0));
    }
}
