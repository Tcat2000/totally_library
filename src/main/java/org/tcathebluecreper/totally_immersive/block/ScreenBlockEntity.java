package org.tcathebluecreper.totally_immersive.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_immersive.TIBlocks;

public class ScreenBlockEntity extends BlockEntity {
    public ScreenBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TIBlocks.SCREEN_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
