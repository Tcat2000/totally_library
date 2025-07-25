package org.tcathebluecreper.totally_immersive.mod.Multiblock.rotay_kiln;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeProcess;
import org.tcathebluecreper.totally_immersive.api.multiblock.TIMultiblockState;

import java.util.ArrayList;

public class RotaryKilnState implements TIMultiblockState<RotaryKilnRecipe, RotaryKilnState> {
    @Override
    public TIRecipeProcess<RotaryKilnRecipe, RotaryKilnState> getRecipeProcess() {
        return new TIRecipeProcess<>(RotaryKilnRecipe.class, new ArrayList<>(), this);
    }

    @Override
    public void getTOPData(BlockEntity be, ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {

    }

    @Override
    public void writeSaveNBT(CompoundTag nbt) {

    }

    @Override
    public void readSaveNBT(CompoundTag nbt) {

    }
}
