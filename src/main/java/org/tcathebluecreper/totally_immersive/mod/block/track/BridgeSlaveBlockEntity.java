package org.tcathebluecreper.totally_immersive.mod.block.track;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.tcathebluecreper.totally_immersive.api.NBT.TINBTUtils;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;

public class BridgeSlaveBlockEntity extends BlockEntity {
    public BlockPos coreBlock = new BlockPos(0,0,0);
    public VoxelShape blockShape = Shapes.block();
    public BridgeSlaveBlockEntity(BlockPos pos, BlockState state) {
        super(TIBlocks.BRIDGE_SLAVE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("CoreBlockPos", TINBTUtils.blockPosToTag(coreBlock));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        coreBlock = TINBTUtils.tagToBlockPos((IntArrayTag) tag.get("CoreBlockPos"));
        super.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("CoreBlockPos", TINBTUtils.blockPosToTag(coreBlock));
        return tag;
    }
}
