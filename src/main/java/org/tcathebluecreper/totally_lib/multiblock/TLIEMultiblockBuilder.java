package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;

import java.util.function.Supplier;

public class TLIEMultiblockBuilder<S extends IMultiblockState> extends IEMultiblockBuilder<S> {
    public TLIEMultiblockBuilder(IMultiblockLogic<S> logic, String name) {
        super(logic, name);
    }
    public TLIEMultiblockBuilder<S> masterBE(Supplier<> be) {
        this.
    }
}
