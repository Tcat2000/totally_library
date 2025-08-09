package org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class RotaryKilnMultiblock extends IETemplateMultiblock {
    public RotaryKilnMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/rotary_kiln"), new BlockPos(1,1,17), new BlockPos(0,1,16), new BlockPos(3,7,18), TIMultiblocks.ROTARY_KILN);
    }
    @Override
    public float getManualScale() {
        return 0;
    }
}
