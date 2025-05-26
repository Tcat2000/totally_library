package org.tcathebluecreper.totally_immersive.Multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Function;

public class ChemicalBathLogic implements IClientTickableComponent<ChemicalBathState>, IMultiblockLogic<ChemicalBathState>, IServerTickableComponent<ChemicalBathState> {
    @Override
    public void tickClient(IMultiblockContext<ChemicalBathState> iMultiblockContext) {
        ChemicalBathState state = iMultiblockContext.getState();
        state.process.tickClient();
    }

    @Override
    public void tickServer(IMultiblockContext<ChemicalBathState> iMultiblockContext) {
        ChemicalBathState state = iMultiblockContext.getState();
        state.process.tick(iMultiblockContext.getLevel().getRawLevel(), state);
        iMultiblockContext.requestMasterBESync();
    }

    @Override
    public ChemicalBathState createInitialState(IInitialMultiblockContext<ChemicalBathState> iInitialMultiblockContext) {
        return new ChemicalBathState(iInitialMultiblockContext);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return Util.memoize(ChemicalBathLogic::getShape);
    }

    static VoxelShape getShape(BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<ChemicalBathState> ctx, CapabilityPosition position, Capability<T> cap) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(position.posInMultiblock().equals(new BlockPos(0,0,1))) return ctx.getState().input.cast(ctx);
            if(position.posInMultiblock().equals(new BlockPos(3,0,1))) return ctx.getState().output.cast(ctx);
        }
        if(cap == ForgeCapabilities.ENERGY) {
            if(position.side() == RelativeBlockFace.UP || position.side() == null && position.posInMultiblock().equals(new BlockPos(3,1,0))) return ctx.getState().power.cast(ctx);
        }
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            if(position.posInMultiblock().equals(new BlockPos(1,0,1))) return ctx.getState().chemTank.cast(ctx);
        }
        return LazyOptional.empty();
    }
}
