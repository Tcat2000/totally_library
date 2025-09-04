package org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;
import org.tcathebluecreper.totally_lib.multiblock.TIMultiblockState;

import java.util.ArrayList;

public class RotaryKilnState implements TIMultiblockState<RotaryKilnRecipe, RotaryKilnState> {
    @Override
    public TLRecipeProcess<RotaryKilnRecipe, RotaryKilnState> getRecipeProcess() {
        return new TLRecipeProcess<>(RotaryKilnRecipe.class, new ArrayList<>(), this, (process, tick) -> true, 16, true);
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

    public TLRecipe findRecipe(RotaryKilnState state) {
        return null;
    }
}
