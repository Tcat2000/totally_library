package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;

import java.util.function.Function;

public class RegisterableMultiblock {
    public final MultiblockHandler.IMultiblock multiblock;
    public final Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state;
    public final IMultiblockLogic<TraitMultiblockState> logic;

    public RegisterableMultiblock(MultiblockHandler.IMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic) {
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
    }
}
