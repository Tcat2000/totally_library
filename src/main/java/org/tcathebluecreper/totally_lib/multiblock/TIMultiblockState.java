package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_lib.crafting.TIRecipe;
import org.tcathebluecreper.totally_lib.crafting.TIRecipeProcess;

public interface TIMultiblockState<R extends TIRecipe,S extends IMultiblockState> extends IMultiblockState {
    TIRecipeProcess<R,S> getRecipeProcess();
    void getTOPData(BlockEntity be, ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData);
}
