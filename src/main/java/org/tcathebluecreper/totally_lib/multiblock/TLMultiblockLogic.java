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

public class TLMultiblockLogic implements IMultiblockLogic<TLTraitMultiblockState>, IClientTickableComponent<TLTraitMultiblockState>, IServerTickableComponent<TLTraitMultiblockState> {
    private Function<BlockPos, VoxelShape> shape;
    public Function<BlockPos, VoxelShape> getShape() {return shape;}
    private BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> serverTick;
    public BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> getServerTick() {return serverTick;}
    private BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> clientTick;
    public BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> getClientTick() {return clientTick;}
    private Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> stateConstructor;
    public Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> getStateConstructor() {return stateConstructor;}

    public TLMultiblockLogic construct(Function<BlockPos, VoxelShape> shape, BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> serverTick, BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> clientTick, Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> stateConstructor) {
        this.shape = shape;
        this.serverTick = serverTick;
        this.clientTick = clientTick;
        this.stateConstructor = stateConstructor;
        return this;
    }

    @Override
    public TLTraitMultiblockState createInitialState(IInitialMultiblockContext<TLTraitMultiblockState> iInitialMultiblockContext) {
        iInitialMultiblockContext.getSyncRunnable().run();
        return stateConstructor.apply(iInitialMultiblockContext);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType shapeType) {
        return shape;
    }

    @Override
    public void tickServer(IMultiblockContext<TLTraitMultiblockState> iMultiblockContext) {
        serverTick.accept(this, iMultiblockContext);
        iMultiblockContext.requestMasterBESync();
    }

    @Override
    public void tickClient(IMultiblockContext<TLTraitMultiblockState> iMultiblockContext) {
        clientTick.accept(this, iMultiblockContext);
        iMultiblockContext.requestMasterBESync();
    }


    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<TLTraitMultiblockState> ctx, CapabilityPosition position, Capability<T> cap) {
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
