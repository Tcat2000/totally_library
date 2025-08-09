package org.tcathebluecreper.totally_immersive.integration.theoneprobe;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tcathebluecreper.totally_lib.TIMath;
import org.tcathebluecreper.totally_lib.multiblock.TIMultiblockState;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class MultiblocksTOPProvider implements IProbeInfoProvider {
    private static final Logger log = LogManager.getLogger(MultiblocksTOPProvider.class);

    @Override
    public ResourceLocation getID() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "top");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity BE = level.getBlockEntity(iProbeHitData.getPos());
        if(!(BE instanceof MultiblockBlockEntityDummy<?> || BE instanceof MultiblockBlockEntityMaster<?>)) return;
        MultiblockBlockEntityMaster<?> master;
        if(BE instanceof MultiblockBlockEntityDummy<?>) {
            MultiblockBlockEntityDummy<?> dummy = (MultiblockBlockEntityDummy<?>) BE;
            master = (MultiblockBlockEntityMaster<?>) level.getBlockEntity(dummy.getBlockPos().offset(TIMath.rotateBlockPos(TIMath.invertBlockPosAxis(dummy.getHelper().getPositionInMB(), Direction.Axis.Y), Direction.SOUTH, dummy.getBlockState().getValue(IEProperties.FACING_HORIZONTAL))).offset(TIMath.rotateBlockPos(TIMath.invertBlockPos(dummy.getHelper().getMultiblock().masterPosInMB()), Direction.SOUTH, dummy.getBlockState().getValue(IEProperties.FACING_HORIZONTAL))));
        }
        else master = (MultiblockBlockEntityMaster<?>) BE;
        if(master == null) return;
        try {
            ((MultiblockBlockEntityMaster<TIMultiblockState<?,?>>)master).getHelper().getContext().getState().getTOPData(BE, probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
        } catch(Exception e) {
            log.error("error: ", e);
        }
    }
}
