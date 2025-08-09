package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;

public class TLMultiblock {
    public final MultiblockHandler.IMultiblock multiblock;
    public final TraitMultiblockState state;
    public final IMultiblockLogic<TraitMultiblockState> logic;

    public TLMultiblock(MultiblockHandler.IMultiblock multiblock, TraitMultiblockState state, IMultiblockLogic<TraitMultiblockState> logic) {
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
    }
}
