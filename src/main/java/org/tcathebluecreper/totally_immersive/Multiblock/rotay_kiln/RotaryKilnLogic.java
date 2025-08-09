package org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class RotaryKilnLogic implements IMultiblockLogic<RotaryKilnState> {
    @Override
    public RotaryKilnState createInitialState(IInitialMultiblockContext capabilitySource) {
        return new RotaryKilnState();
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return pos -> Shapes.block();
    }
}
