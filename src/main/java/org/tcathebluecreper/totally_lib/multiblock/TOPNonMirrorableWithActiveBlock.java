package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;

public class TOPNonMirrorableWithActiveBlock<S extends IMultiblockState> extends NonMirrorableWithActiveBlock<S> {
    public  TOPNonMirrorableWithActiveBlock(Properties properties, MultiblockRegistration multiblock) {
        super(properties, multiblock);
    }
}
