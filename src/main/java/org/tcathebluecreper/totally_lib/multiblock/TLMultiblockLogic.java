package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.tcathebluecreper.totally_lib.trait.ITrait;
import org.tcathebluecreper.totally_lib.trait.TraitList;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TLMultiblockLogic implements IMultiblockLogic<TraitMultiblockState>, IClientTickableComponent<TraitMultiblockState>, IServerTickableComponent<TraitMultiblockState> {
    private Function<BlockPos, VoxelShape> shape;
    public Function<BlockPos, VoxelShape> getShape() {return shape;}
    private BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> serverTick;
    public BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> getServerTick() {return serverTick;}
    private BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> clientTick;
    public BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> getClientTick() {return clientTick;}
    private Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> stateConstructor;
    public Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> getStateConstructor() {return stateConstructor;}

    public TLMultiblockLogic(Function<BlockPos, VoxelShape> shape, BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> serverTick, BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> clientTick, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> stateConstructor) {
        this.shape = shape;
        this.serverTick = serverTick;
        this.clientTick = clientTick;
        this.stateConstructor = stateConstructor;
    }
    public TLMultiblockLogic reconstruct(Function<BlockPos, VoxelShape> shape, BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> serverTick, BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> clientTick, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> stateConstructor) {
        this.shape = shape;
        this.serverTick = serverTick;
        this.clientTick = clientTick;
        this.stateConstructor = stateConstructor;
        return this;
    }

    @Override
    public TraitMultiblockState createInitialState(IInitialMultiblockContext<TraitMultiblockState> iInitialMultiblockContext) {
        iInitialMultiblockContext.getSyncRunnable().run();
        return stateConstructor.apply(iInitialMultiblockContext);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return shape;
    }

    @Override
    public void tickServer(IMultiblockContext<TraitMultiblockState> iMultiblockContext) {
        serverTick.accept(this, iMultiblockContext);
        iMultiblockContext.requestMasterBESync();
    }

    @Override
    public void tickClient(IMultiblockContext<TraitMultiblockState> iMultiblockContext) {
        clientTick.accept(this, iMultiblockContext);
        iMultiblockContext.requestMasterBESync();
    }


    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<TraitMultiblockState> ctx, CapabilityPosition position, Capability<T> cap) {
        TraitList traits = ctx.getState().traits;
        for(ITrait trait : traits) {
            if(trait.getCapType() != cap) continue;
            if(trait.getExposure().containsKey(position.posInMultiblock()) && (trait.getExposure().get(position.posInMultiblock()).test(position.side()) || position.side() == null)) {
                 return trait.getCap().cast(ctx);
            }
        }
        return LazyOptional.empty();
    }
}
