package org.tcathebluecreper.totally_immersive.lib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class ITMultiblockBlock<S extends IMultiblockState> extends MultiblockPartBlock.WithMirrorState<S> {


    public ITMultiblockBlock(Properties properties, MultiblockRegistration<S> multiblock) {
        super(properties, multiblock);
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 0.8f;
    }
}
