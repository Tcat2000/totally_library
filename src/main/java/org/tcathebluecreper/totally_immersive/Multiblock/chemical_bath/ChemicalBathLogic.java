package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;
import java.util.function.Function;

public class ChemicalBathLogic implements IClientTickableComponent<ChemicalBathState>, IMultiblockLogic<ChemicalBathState>, IServerTickableComponent<ChemicalBathState> {
    @Override
    public void tickClient(IMultiblockContext<ChemicalBathState> iMultiblockContext) {
        ChemicalBathState state = iMultiblockContext.getState();
//        state.process.tickClient();
        state.process.tick(iMultiblockContext.getLevel().getRawLevel(), state, iMultiblockContext);
    }

    @Override
    public void tickServer(IMultiblockContext<ChemicalBathState> iMultiblockContext) {
        ChemicalBathState state = iMultiblockContext.getState();
        state.process.tick(iMultiblockContext.getLevel().getRawLevel(), state, iMultiblockContext);
        iMultiblockContext.requestMasterBESync();
    }

    @Override
    public ChemicalBathState createInitialState(IInitialMultiblockContext<ChemicalBathState> iInitialMultiblockContext) {
        return new ChemicalBathState(iInitialMultiblockContext);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        //return ChemicalBathLogic::getShape;
        return Util.memoize(ChemicalBathLogic::getShape);
    }

    static VoxelShape getShape(BlockPos pos) {
        if(pos.equals(new BlockPos(0,0,0)) || pos.equals(new BlockPos(0,0,1)) ||pos.equals(new BlockPos(3,0,0)) ||pos.equals(new BlockPos(3,0,1))) {
            return Shapes.block();
        }
        else if(pos.equals(new BlockPos(1,1,1)) || pos.equals(new BlockPos(2,1,1))) {
            return Shapes.box(0,11/16f,6/16f,16/16f,15/16f,10/16f);
        }
        else if(pos.equals(new BlockPos(1,1,0)) || pos.equals(new BlockPos(2,1,0))) {
            return Shapes.box(0,0,0,16/16f,4/16f,9/16f);
        }
        else if(pos.equals(new BlockPos(1,0,0)) || pos.equals(new BlockPos(2,0,0))) {
            return Shapes.or(Shapes.box(0,0,0,16/16f,16/16f,9/16f), Shapes.box(0,0,0,16/16f,6/16f,16/16f));
        }
        else if(pos.equals(new BlockPos(0,1,1))) {
            return Shapes.box(3/16f,11/16f,0,16/16f,15/16f,10/16f);
        }
        else if(pos.equals(new BlockPos(3,1,1))) {
            return Shapes.box(0,11/16f,0,13/16f,15/16f,10/16f);
        }
        else if(pos.equals(new BlockPos(2,0,1))) {
            return Shapes.or(Shapes.box(0,0,0,16/16f,4/16f,16/16f), Shapes.box(0,0,0,16/16f,6/16f,11/16f), Shapes.box(0,6/16f,11/16f,16/16f,8/16f,12/16f), Shapes.box(0,8/16f,12/16f,16/16f,10/16f,13/16f), Shapes.box(0,10/16f,13/16f,16/16f,12/16f,14/16f), Shapes.box(0,12/16f,14/16f,16/16f,14/16f,15/16f), Shapes.box(0,14/16f,15/16f,16/16f,16/16f,16/16f));
        }
        else if(pos.equals(new BlockPos(1,0,1))) {
            return Shapes.or(Shapes.box(0,0,0,16/16f,4/16f,16/16f), Shapes.box(0,0,0,16/16f,6/16f,11/16f), Shapes.box(0,6/16f,11/16f,16/16f,8/16f,12/16f), Shapes.box(0,8/16f,12/16f,16/16f,10/16f,13/16f), Shapes.box(0,10/16f,13/16f,16/16f,12/16f,14/16f), Shapes.box(0,12/16f,14/16f,16/16f,14/16f,15/16f), Shapes.box(0,14/16f,15/16f,16/16f,16/16f,16/16f), Shapes.box(2/16f,2/16f,14/16f,14/16f,14/16f,16/16f), Shapes.box(2/16f,2/16f,12/16f,14/16f,10/16f,14/16f), Shapes.box(2/16f,2/16f,10/16f,14/16f,6/16f,12/16f));
        }
        else return Shapes.block();
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

    @Override
    public void dropExtraItems(ChemicalBathState state, Consumer<ItemStack> drop) {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }
}
