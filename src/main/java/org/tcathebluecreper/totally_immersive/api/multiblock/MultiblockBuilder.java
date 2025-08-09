package org.tcathebluecreper.totally_immersive.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.tcathebluecreper.totally_immersive.api.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;
import org.tcathebluecreper.totally_immersive.api.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;
import org.tcathebluecreper.totally_immersive.mod.TIItems;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiblockBuilder {
    public BlockPos masterOffset;
    public BlockPos triggerOffset;
    public BlockPos size;
    public TIDynamicModel manualModel;
    public ArrayList<ITrait> traits = new ArrayList<>();

    public BakedMultiblock bake(ResourceLocation id) {
        Supplier<TraitMultiblockState> state = () -> new TraitMultiblockState(traits) {
            @Override
            public void writeSaveNBT(CompoundTag nbt) {

            }

            @Override
            public void readSaveNBT(CompoundTag nbt) {

            }
        };
        IMultiblockLogic<TraitMultiblockState> logic = new IMultiblockLogic<TraitMultiblockState>() {
            @Override
            public TraitMultiblockState createInitialState(IInitialMultiblockContext capabilitySource) {
                return null;
            }

            @Override
            public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
                return pos -> Shapes.block();
            }
        };
        TIMultiblock multiblock = new BakedMultiblock(
            new TIMultiblock(id, masterOffset, triggerOffset, size, logic, manualModel) {
                @Override
                public float getManualScale() {
                    return 0;
                }
            }
        );
        MultiblockRegistration registration = new IEMultiblockBuilder<>(logic, id.getPath())
            .defaultBEs(TIBlocks.BETs)
            .customBlock(
                TIBlocks.BLOCKS, TIItems.ITEMS,
                r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                MultiblockItem::new)
            .build();
    }
}
