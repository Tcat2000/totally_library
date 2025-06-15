package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import java.util.function.Consumer;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ChemicalBathMultiblock extends IETemplateMultiblock {
    public ChemicalBathMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(MODID, "multiblocks/chemical_bath"), new BlockPos(0,0,1), new BlockPos(0,0,1), new BlockPos(4,2,2), TIMultiblocks.CHEMICAL_BATH);
    }

    @Override
    public float getManualScale() {
        return 12;
    }
}
