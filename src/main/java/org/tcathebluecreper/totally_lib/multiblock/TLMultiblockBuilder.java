package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.NativeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.common.util.Lazy;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.recipe.RecipeBuilder;

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
    public RecipeBuilder.TLBuiltRecipeInfo recipeInfo = null;

    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<RegistrableMultiblock> consumer;

    private final TIMultiblock multiblock;

    private final boolean reload;
    private final static HashMap<ResourceLocation, MultiblockInfo> multiblockInfo = new HashMap<>();

    public TLMultiblockBuilder(ResourceLocation id, RegistrationManager manager, Consumer<RegistrableMultiblock> consumer, boolean reload) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;

        if(reload) {
            multiblock = multiblocksToRegister.get(id).get();
        }
        else multiblock = null;
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
    public TLMultiblockBuilder recipe(Function<RecipeBuilder, RecipeBuilder.TLBuiltRecipeInfo> builder) {
        recipeInfo = builder.apply(new RecipeBuilder(id, TotallyLibrary.regManager, (b) -> {}, reload));
        return this;
    }

    public RegistrableMultiblock build() {
        MultiblockInfo info = reload ? multiblockInfo.getOrDefault(id, new MultiblockInfo()) : new MultiblockInfo();
        info.state = (capabilitySource) -> {
            if(recipeInfo == null) new TraitMultiblockState(capabilitySource, traits.get());
            return new RecipeTraitMultiblockState(capabilitySource, traits.get(), recipeInfo.createProcess);
        };

        info.tickLogic = recipeInfo == null ? (s, c) -> {} : (s, c) -> {
            if(c.getState() instanceof RecipeTraitMultiblockState) {
                ((RecipeTraitMultiblockState) c.getState()).process.tick(c.getLevel().getRawLevel());
            }
        };
        info.logic = reload ? info.logic.reconstruct(pos -> Shapes.block(), info.tickLogic, info.tickLogic, info.state) : new TLMultiblockLogic(pos -> Shapes.block(), info.tickLogic, info.tickLogic, info.state);


        info.registration = reload ? info.registration : new IEMultiblockBuilder<>(info.logic, id.getPath())
            .defaultBEs(manager.getRegistry(id.getNamespace()).blockEntityType())
            .customBlock(
                manager.getRegistry(id.getNamespace()).blocks(), manager.getRegistry(id.getNamespace()).items(),
                r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                MultiblockItem::new)
            .structure(() -> multiblocksToRegister.get(id).get())
            .build();


        info.multiblockClass = reload ? info.multiblockClass : new TIMultiblock(id, masterOffset, triggerOffset, size, info.registration, manualModel) {
            @Override
            public float getManualScale() {
                return manualScale;
            }
        };
        multiblocksToRegister.put(id, Lazy.of(() -> info.multiblockClass));

        info.reg = reload ? info.reg.reconstruct(id, info.multiblockClass, info.state, info.logic, hasModelInfo() ? new RegistrableMultiblock.AssetGenerationData(blocks, model) : null) : new RegistrableMultiblock(id, info.multiblockClass, info.state, info.logic, hasModelInfo() ? new RegistrableMultiblock.AssetGenerationData(blocks, model) : null);
        consumer.accept(info.reg);

        multiblockInfo.put(id, info);

        return info.reg;
    }

    public static void init() {
        multiblocksToRegister.forEach((l,mb)->MultiblockHandler.registerMultiblock(mb.get()));
    }

    private boolean hasModelInfo() {
        return model != null && blocks.length != 0;
    }

    private static class MultiblockInfo {
        RegistrableMultiblock reg;
        TIMultiblock multiblockClass;
        MultiblockRegistration<TraitMultiblockState> registration;
        TLMultiblockLogic logic;
        BiConsumer<TLMultiblockLogic, IMultiblockContext<TraitMultiblockState>> tickLogic;
        Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state;
    }
}
