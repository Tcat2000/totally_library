package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TLMultiblockBuilder {
    public BlockPos masterOffset;
    public BlockPos triggerOffset;
    public BlockPos size;
    public int manualScale = 0;
    public TIDynamicModel manualModel;
    public ArrayList<ITrait> traits = new ArrayList<>();

    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<RegisterableMultiblock> consumer;

    public TLMultiblockBuilder(ResourceLocation id, RegistrationManager manager, Consumer<RegisterableMultiblock> consumer) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
    }


    public RegisterableMultiblock bake() {
        Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state = (capabilitySource) -> new TraitMultiblockState(traits);
        IMultiblockLogic<TraitMultiblockState> logic = new IMultiblockLogic<>() {
            @Override
            public TraitMultiblockState createInitialState(IInitialMultiblockContext capabilitySource) {
                return state.apply(capabilitySource);
            }

            @Override
            public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
                return pos -> Shapes.block();
            }
        };
        MultiblockRegistration<TraitMultiblockState> registration = new IEMultiblockBuilder<>(logic, id.getPath())
                .defaultBEs(manager.getRegistry(id.getNamespace()).blockEntityType())
                .customBlock(
                        manager.getRegistry(id.getNamespace()).blocks(), manager.getRegistry(id.getNamespace()).items(),
                        r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                        MultiblockItem::new)
                .build();
        TIMultiblock multiblock = new TIMultiblock(id, masterOffset, triggerOffset, size, registration, manualModel) {
            @Override
            public float getManualScale() {
                return manualScale;
            }
        };

        return new RegisterableMultiblock(multiblock, state, logic);
    }
}
