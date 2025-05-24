package org.tcathebluecreper.totally_immersive.Multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class ChemicalBathState implements IClientTickableComponent<ChemicalBathState>, IMultiblockLogic<ChemicalBathState>, IServerTickableComponent<ChemicalBathState>, IMultiblockState {
    @Override
    public void tickClient(IMultiblockContext<ChemicalBathState> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<ChemicalBathState> iMultiblockContext) {

    }

    @Override
    public ChemicalBathState createInitialState(IInitialMultiblockContext<ChemicalBathState> iInitialMultiblockContext) {
        return null;
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return null;
    }

    @Override
    public void writeSaveNBT(CompoundTag compoundTag) {

    }

    @Override
    public void readSaveNBT(CompoundTag compoundTag) {

    }
}
