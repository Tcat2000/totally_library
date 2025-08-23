package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
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
import net.minecraftforge.common.util.Lazy;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TLMultiblockBuilder {
    public static final Map<ResourceLocation, Lazy<TIMultiblock>> multiblocksToRegister = new HashMap<>();
    public BlockPos masterOffset;
    public BlockPos triggerOffset;
    public BlockPos size;
    public int manualScale = 0;
    public TIDynamicModel manualModel;
    public Supplier<List<ITrait>> traits = ArrayList::new;

    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<RegisterableMultiblock> consumer;

    public TLMultiblockBuilder(ResourceLocation id, RegistrationManager manager, Consumer<RegisterableMultiblock> consumer) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
    }

    public TLMultiblockBuilder masterOffset(BlockPos offset) {masterOffset = offset; return this;}
    public TLMultiblockBuilder masterOffset(int x, int y, int z) {masterOffset = new BlockPos(x,y,z); return this;}
    public TLMultiblockBuilder triggerOffset(BlockPos offset) {triggerOffset = offset; return this;}
    public TLMultiblockBuilder triggerOffset(int x, int y, int z) {triggerOffset = new BlockPos(x,y,z); return this;}
    public TLMultiblockBuilder size(BlockPos size) {this.size = size; return this;}
    public TLMultiblockBuilder size(int x, int y, int z) {size = new BlockPos(x,y,z); return this;}
    public TLMultiblockBuilder traits(Supplier<List<ITrait>> traits) {
        this.traits = () -> new ArrayList<>(traits.get());
        return this;
    }

    public RegisterableMultiblock bake() {
        Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state = (capabilitySource) -> new TraitMultiblockState(traits.get());

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
            .structure(() -> multiblocksToRegister.get(id).get())
            .build();

        TIMultiblock multiblockClass = new TIMultiblock(id, masterOffset, triggerOffset, size, registration, manualModel) {
            @Override
            public float getManualScale() {
                return manualScale;
            }
        };
        multiblocksToRegister.put(id, Lazy.of(() -> multiblockClass));

        RegisterableMultiblock reg = new RegisterableMultiblock(multiblockClass, state, logic);
        consumer.accept(reg);
        return reg;
    }
    public static void init() {
        multiblocksToRegister.forEach((l,mb)->MultiblockHandler.registerMultiblock(mb.get()));
    }
}
