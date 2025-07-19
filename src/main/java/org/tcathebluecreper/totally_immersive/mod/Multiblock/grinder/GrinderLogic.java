package org.tcathebluecreper.totally_immersive.mod.Multiblock.grinder;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Function;

public class GrinderLogic implements IClientTickableComponent<GrinderState>, IMultiblockLogic<GrinderState>, IServerTickableComponent<GrinderState> {
    @Override
    public void tickClient(IMultiblockContext<GrinderState> iMultiblockContext) {
        GrinderState state = iMultiblockContext.getState();
        state.process.tick(iMultiblockContext.getLevel().getRawLevel());
    }

    @Override
    public void tickServer(IMultiblockContext<GrinderState> iMultiblockContext) {
        GrinderState state = iMultiblockContext.getState();
        state.process.tick(iMultiblockContext.getLevel().getRawLevel());
        iMultiblockContext.requestMasterBESync();
    }

    @Override
    public GrinderState createInitialState(IInitialMultiblockContext<GrinderState> iInitialMultiblockContext) {
        return new GrinderState(iInitialMultiblockContext);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return GrinderLogic::getShape;
    }

    static VoxelShape getShape(BlockPos pos) {
        if(
                pos.equals(new BlockPos(0,0,0)) ||
                pos.equals(new BlockPos(0,0,1)) ||
                pos.equals(new BlockPos(0,0,2)) ||
                pos.equals(new BlockPos(2,0,0)) ||
                pos.equals(new BlockPos(2,0,1)) ||
                pos.equals(new BlockPos(2,0,2))) {
            return Shapes.box(0,0,0,1,0.5,1);
        }
        if(
                pos.equals(new BlockPos(2,1,1)) ||
                pos.equals(new BlockPos(2,1,2))) {
            return Shapes.box(0,0.5,0,0.5,1,1);
        }
        if(
                pos.equals(new BlockPos(0,1,1)) ||
                pos.equals(new BlockPos(0,1,2))) {
            return Shapes.box(0.5,0.5,0,1,1,1);
        }
        if(
                pos.equals(new BlockPos(2,2,1)) ||
                pos.equals(new BlockPos(2,2,2)) ||
                pos.equals(new BlockPos(2,3,1)) ||
                pos.equals(new BlockPos(2,3,2))) {
            return Shapes.box(0,0,0,0.5,1,1);
        }
        if(
                pos.equals(new BlockPos(0,2,1)) ||
                pos.equals(new BlockPos(0,2,2)) ||
                pos.equals(new BlockPos(0,3,1)) ||
                pos.equals(new BlockPos(0,3,2))) {
            return Shapes.box(0.5,0,0,1,1,1);
        }
        if(
                pos.equals(new BlockPos(0,1,3)) ||
                pos.equals(new BlockPos(1,1,3)) ||
                pos.equals(new BlockPos(0,2,3)) ||
                pos.equals(new BlockPos(0,3,3)) ||
                pos.equals(new BlockPos(2,2,3)) ||
                pos.equals(new BlockPos(2,3,3))) {
            return Shapes.box(0,0,0,1,1,0.5);
        }
        if(
                pos.equals(new BlockPos(1,3,3))) {
            return Shapes.or(Shapes.box(0,0,0,1,1,0.5), Shapes.box(0,0,0,1,0.5,1));
        }
        if(
                pos.equals(new BlockPos(1,2,3))) {
            return Shapes.or(Shapes.box(0,0,0,1,1,0.5), Shapes.box(0,0.5,0,1,1,1));
        }
        if(
                pos.equals(new BlockPos(2,1,3))) {
            return Shapes.or(Shapes.box(0,0,0,1,1,0.5), Shapes.box(0,0,0.5,1,1,1));
        }
        if(
                pos.equals(new BlockPos(1,1,0))) {
            return Shapes.or(
                Shapes.box(0,0,8/16f,1,1/16f,1),
                Shapes.box(0,0,9/16f,1,2/16f,1),
                Shapes.box(0,0,10/16f,1,3/16f,1),
                Shapes.box(0,0,11/16f,1,4/16f,1),
                Shapes.box(0,0,12/16f,1,5/16f,1),
                Shapes.box(0,0,13/16f,1,6/16f,1),
                Shapes.box(0,0,14/16f,1,7/16f,1),
                Shapes.box(0,0,15/16f,1,8/16f,1),
                Shapes.box(0,0,16/16f,1,8/16f,1)
            );
        }
        if(
            pos.equals(new BlockPos(1,4,3))) {
            return Shapes.or(Shapes.box(2/16f,2/16f,0,14/16f,14/16f,1), Shapes.box(6/16f,0,0,10/16f,2/16f,8/16f));
        }
        if(
            pos.equals(new BlockPos(1,4,2))) {
            return Shapes.or(Shapes.box(2/16f,2/16f,0,14/16f,14/16f,1), Shapes.box(2/16f,0,0,14/16f,14/16f,6/16f));
        }
        if(
            pos.equals(new BlockPos(1,4,1))) {
            return Shapes.box(2/16f,0,10/16f,14/16f,14/16f,1);
        }
        else return Shapes.block();
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<GrinderState> ctx, CapabilityPosition position, Capability<T> cap) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(position.posInMultiblock().equals(new BlockPos(1,4,3))) return ctx.getState().input.cast(ctx);
            if(position.posInMultiblock().equals(new BlockPos(1,0,0))) return ctx.getState().output.cast(ctx);
        }
        if(cap == ForgeCapabilities.ENERGY) {
            if(position.side() == RelativeBlockFace.BACK || position.side() == null && position.posInMultiblock().equals(new BlockPos(1,0,3))) return ctx.getState().power.cast(ctx);
        }
        return LazyOptional.empty();
    }
}
