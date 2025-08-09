package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.TIMultiblock;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ChemicalBathMultiblock extends TIMultiblock {
    public ChemicalBathMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/chemical_bath"), new BlockPos(0,0,1), new BlockPos(0,0,1), new BlockPos(4,2,2), TIMultiblocks.CHEMICAL_BATH, new TIDynamicModel("chemical_bath/chemical_bath"));
    }

    @Override
    public float getManualScale() {
        return 12;
    }
}
