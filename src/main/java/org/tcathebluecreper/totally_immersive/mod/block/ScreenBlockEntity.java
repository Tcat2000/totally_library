package org.tcathebluecreper.totally_immersive.mod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;

public class ScreenBlockEntity extends BlockEntity {
    public ScreenBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TIBlocks.SCREEN_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
