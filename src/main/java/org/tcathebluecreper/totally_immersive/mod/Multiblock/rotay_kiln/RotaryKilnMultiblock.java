package org.tcathebluecreper.totally_immersive.mod.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_immersive.mod.TIMultiblocks;

import static org.tcathebluecreper.totally_immersive.mod.TotallyImmersive.MODID;

public class RotaryKilnMultiblock extends IETemplateMultiblock {
    public RotaryKilnMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/rotary_kiln"), new BlockPos(3,7,18), new BlockPos(0,0,0), new BlockPos(3,7,18), TIMultiblocks.ROTARY_KILN);
    }
    @Override
    public float getManualScale() {
        return 0;
    }
}
