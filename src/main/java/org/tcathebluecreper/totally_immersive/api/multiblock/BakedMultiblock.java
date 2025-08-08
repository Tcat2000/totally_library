package org.tcathebluecreper.totally_immersive.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;

public class BakedMultiblock<S extends IMultiblockState> {
    public final MultiblockHandler.IMultiblock multiblock;
    public final S state;
    public final IMultiblockLogic<S> logic;

    public BakedMultiblock(MultiblockHandler.IMultiblock multiblock, S state, IMultiblockLogic<S> logic) {
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
    }
}
