package org.tcathebluecreper.totally_lib.lib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class TLMultiblockBlock<S extends IMultiblockState> extends MultiblockPartBlock.WithMirrorState<S> {


    public TLMultiblockBlock(Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 0.8f;
    }
}
