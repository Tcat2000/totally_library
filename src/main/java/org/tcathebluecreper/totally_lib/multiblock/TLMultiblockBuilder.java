package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.NativeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitHolder;
import org.tcathebluecreper.totally_lib.recipe.TLBuiltRecipeInfo;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeBuilder;

import java.util.*;
import java.util.function.BiConsumer;
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
    public int[][] blocks = new int[0][];
    public JsonObject model = null;
    public TLBuiltRecipeInfo recipeInfo = null;

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
    public TLMultiblockBuilder form(List<List<Double>> blocks) {
        this.blocks = new int[blocks.size()][];
        for(int i = 0; i < blocks.size(); i++) {
            this.blocks[i] = new int[]{(int) Math.round(blocks.get(i).get(0)), (int) Math.round(blocks.get(i).get(1)), (int) Math.round(blocks.get(i).get(2))};
        }
        return this;
    }
    public TLMultiblockBuilder obj(String modelLocation, NativeObject o) {
        model = new JsonObject();
        model.addProperty("parent","minecraft:block/block");

        JsonObject textures = new JsonObject();

        o.entrySet().forEach(entry -> {
            textures.addProperty((String) entry.getKey(), (String) entry.getValue());
        });

        textures.addProperty("side","immersiveengineering:block/multiblocks/coke_oven");
        model.add("textures", textures);

        model.addProperty("loader", "forge:obj");
        model.addProperty("model", modelLocation);
        model.addProperty("automatic_culling", false);
        model.addProperty("flip_v", true);
        return this;
    }
    public TLMultiblockBuilder recipe(Function<TLRecipeBuilder, TLBuiltRecipeInfo> builder) {
        recipeInfo = builder.apply(new TLRecipeBuilder(id, TotallyLibrary.regManager, (b) -> {}));
        return this;
    }

    public RegisterableMultiblock build() {
        Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state = (capabilitySource) -> {
            if(recipeInfo == null) new TraitMultiblockState(capabilitySource, traits.get());
            return new RecipeTraitMultiblockState(capabilitySource, traits.get(), recipeInfo.createProcess);
        };

        BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> tickLogic = recipeInfo == null ? (s, c) -> {} : (s, c) -> {
            if(c.getState() instanceof RecipeTraitMultiblockState) {
                ((RecipeTraitMultiblockState) c.getState()).process.tick(c.getLevel().getRawLevel());
            }
        };
        IMultiblockLogic<TraitMultiblockState> logic = new TLMultiblockLogic(pos -> Shapes.block(), (s,c) -> {}, (s,c) -> {}, state);


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

        RegisterableMultiblock reg = new RegisterableMultiblock(id, multiblockClass, state, logic, hasModelInfo() ? new RegisterableMultiblock.AssetGenerationData(blocks, model) : null);
        consumer.accept(reg);
        return reg;
    }

    public static void init() {
        multiblocksToRegister.forEach((l,mb)->MultiblockHandler.registerMultiblock(mb.get()));
    }

    private boolean hasModelInfo() {
        return model != null && blocks.length != 0;
    }
}
