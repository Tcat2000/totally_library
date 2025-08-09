package org.tcathebluecreper.totally_immersive.block.track;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.TIBlocks;

public class BridgeSlaveBlock extends Block implements EntityBlock {
    public BridgeSlaveBlock() {
        super(BlockBehaviour.Properties.of().destroyTime(-1));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TIBlocks.BRIDGE_SLAVE_BLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return blockGetter.getBlockEntity(pos) != null ? ((BridgeSlaveBlockEntity) blockGetter.getBlockEntity(pos)).blockShape : Shapes.block();
    }
}
