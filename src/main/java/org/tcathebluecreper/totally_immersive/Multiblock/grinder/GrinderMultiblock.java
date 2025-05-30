package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_immersive.TIContent;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class GrinderMultiblock extends IETemplateMultiblock {
    public GrinderMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/grinder/grinder"), new BlockPos(1,0,0), new BlockPos(1,0,3), new BlockPos(3,5,4), TIContent.TIMultiblocks.GRINDER);
    }

    @Override
    public float getManualScale() {
        return 12;
    }
}
