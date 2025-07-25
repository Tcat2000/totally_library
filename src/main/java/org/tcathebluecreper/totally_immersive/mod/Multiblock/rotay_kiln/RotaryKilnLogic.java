package org.tcathebluecreper.totally_immersive.mod.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class RotaryKilnLogic implements IMultiblockLogic<RotaryKilnState> {
    @Override
    public RotaryKilnState createInitialState(IInitialMultiblockContext capabilitySource) {
        return null;
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
        return null;
    }
}
