package org.tcathebluecreper.totally_immersive.Multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class ChemicalBathLogic implements IClientTickableComponent<ChemicalBathState>, IMultiblockLogic<ChemicalBathState>, IServerTickableComponent<ChemicalBathState> {
    @Override
    public void tickClient(IMultiblockContext<ChemicalBathState> iMultiblockContext) {

    }

    @Override
    public void tickServer(IMultiblockContext<ChemicalBathState> iMultiblockContext) {

    }

    @Override
    public ChemicalBathState createInitialState(IInitialMultiblockContext<ChemicalBathState> iInitialMultiblockContext) {
        return new ChemicalBathState();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return Util.memoize(ChemicalBathLogic::getShape);
    }

    static VoxelShape getShape(BlockPos pos) {
        return Shapes.block();
    }
}
