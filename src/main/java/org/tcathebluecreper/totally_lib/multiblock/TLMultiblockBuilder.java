package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.jei.JEICategoryBuilder;
import org.tcathebluecreper.totally_lib.jei.JeiRecipeCatalyst;
import org.tcathebluecreper.totally_lib.lib.ITMultiblockBlock;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;
import org.tcathebluecreper.totally_lib.recipe.ModularRecipe;
import org.tcathebluecreper.totally_lib.trait.ITrait;
import org.tcathebluecreper.totally_lib.trait.TraitList;
import org.tcathebluecreper.totally_lib.recipe.RecipeBuilder;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TLMultiblockBuilder {
    public static final Map<ResourceLocation, Lazy<TLMultiblock>> multiblocksToRegister = new HashMap<>();
    public BlockPos masterOffset;
    public BlockPos triggerOffset;
    public BlockPos size;
    public float manualScale = 16;
    public TIDynamicModel manualModel = new TIDynamicModel("");
    private boolean hasCustomManualModel = false;
    public Supplier<List<ITrait>> traits = ArrayList::new;
    public int[][] blocks = new int[0][];
    public JsonObject modelJson = null;
    public JsonObject manualModelJson = null;
    public RecipeBuilder.RecipeInfo recipeInfo = null;
    public MachineShape shape = new MachineShape.SolidMachineShape();
    public Consumer<JEICategoryBuilder> jeiCategoryBuilder;
    public List<JeiRecipeCatalyst> jeiCatalysts = new ArrayList<>();

    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<TLMultiblockInfo> consumer;

    private final boolean reload;
    private final static HashMap<ResourceLocation, MultiblockInfo> multiblockInfo = new HashMap<>();

    public TLMultiblockBuilder(ResourceLocation id, RegistrationManager manager, Consumer<TLMultiblockInfo> consumer, boolean reload) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
        this.reload = reload;
    }

    @Info("Defines offset from the origin that the master block is located. (+Z is forward)")
    public TLMultiblockBuilder masterOffset(BlockPos offset) {masterOffset = offset; return this;}
    @Info("Defines offset from the origin that the master block is located. (+Z is forward)")
    public TLMultiblockBuilder masterOffset(int x, int y, int z) {masterOffset = new BlockPos(x,y,z); return this;}
    @Info("Defines offset from the origin that needs clicked with the hammer to form. (+Z is forward)")
    public TLMultiblockBuilder triggerOffset(BlockPos offset) {triggerOffset = offset; return this;}
    @Info("Defines offset from the origin that needs clicked with the hammer to form. (+Z is forward)")
    public TLMultiblockBuilder triggerOffset(int x, int y, int z) {triggerOffset = new BlockPos(x,y,z); return this;}
    @Info("Defines the cubic size of the machine.")
    public TLMultiblockBuilder size(BlockPos size) {this.size = size; return this;}
    @Info("Defines the cubic size of the machine.")
    public TLMultiblockBuilder size(int x, int y, int z) {size = new BlockPos(x,y,z); return this;}
    @Info("Sets the size of the multiblock when rendered in the manual.")
    public TLMultiblockBuilder manualScale(int scale) {manualScale = scale; return this;}
    @Info("Overrides the automatically generated model for display in the manual. Useful for modeling fake BER components.")
    public TLMultiblockBuilder manualModel(ResourceLocation location) {manualModel = new TIDynamicModel(location); hasCustomManualModel = true; return this;}

    @Info("Proves capability to your machine like items, fluids, powers, ect.")
    public TLMultiblockBuilder traits(Supplier<List<ITrait>> traits) {
        this.traits = () -> new ArrayList<>(traits.get());
        return this;
    }

    @Info("The blocks of the structure, allows auto generation of the split model. Can be generated by using /tlib_utils selection and /tlib_utils generate form....")
    public TLMultiblockBuilder form(List<List<Double>> blocks) {
        this.blocks = new int[blocks.size()][];
        for(int i = 0; i < blocks.size(); i++) {
            this.blocks[i] = new int[]{(int) Math.round(blocks.get(i).get(0)), (int) Math.round(blocks.get(i).get(1)), (int) Math.round(blocks.get(i).get(2))};
        }
        return this;
    }

    @Info("Also required for auto generation of split model, provide the resourceLocation, textures, automatic culling (assume true), and flipV (assume true)")
    public TLMultiblockBuilder obj(String modelLocation, NativeObject textures, boolean automaticCulling, boolean flipV) {
        modelJson = new JsonObject();
        modelJson.addProperty("parent","minecraft:block/block");

        JsonObject texturesObject = new JsonObject();

        textures.forEach((key, value) -> texturesObject.addProperty((String) key, (String) value));

        modelJson.add("textures", texturesObject);

        modelJson.addProperty("loader", "forge:obj");
        modelJson.addProperty("model", modelLocation);
        modelJson.addProperty("automatic_culling", automaticCulling);
        modelJson.addProperty("flip_v", flipV);

        if(!hasCustomManualModel) manualModelJson = modelJson.deepCopy();

        return this;
    }

    @Info("Provides a recipe type builder, contains processing logic")
    public TLMultiblockBuilder recipe(Consumer<RecipeBuilder> builder) {
        RecipeBuilder recipeBuilder = new RecipeBuilder(id, TotallyLibrary.regManager, (b) -> {}, reload);
        builder.accept(recipeBuilder);
        recipeInfo = recipeBuilder.build();
        return this;
    }

    @Info("Finishes the builder.")
    public TLMultiblockInfo build() {
        if(reload) return pRebuild();
        return pBuild();
    }

    @HideFromJS
    private TLMultiblockInfo pRebuild() {
        MultiblockInfo info = multiblockInfo.getOrDefault(id, new MultiblockInfo());

        info.state = (capabilitySource) -> {
            if(recipeInfo == null) return new TLTraitMultiblockState(capabilitySource, traits.get());
            return new TLRecipeTraitMultiblockState(capabilitySource, traits.get(), recipeInfo.getCreateProcess());
        };

        info.tickLogic = recipeInfo == null ? (s, c) -> {} : (s, c) -> {
            if(c.getState() instanceof TLRecipeTraitMultiblockState) {
                ((TLRecipeTraitMultiblockState) c.getState()).process.tick(c.getLevel().getRawLevel());
            }
        };

        if(!hasCustomManualModel) manualModel = new TIDynamicModel(id.withPrefix("manual/"));

        info.logic.reconstruct(pos -> shape.get(pos), info.tickLogic, info.tickLogic, info.state);

        info.reg.reconstruct(id, info.multiblockClass, info.state, info.logic, hasModelInfo() ? new TLMultiblockInfo.AssetGenerationData(blocks, modelJson, manualModelJson) : null, new TraitList(traits.get()), info.jeiInfo, recipeInfo, jeiCatalysts);

        return info.reg;
    }

    @HideFromJS
    private TLMultiblockInfo pBuild() {
        MultiblockInfo info = new MultiblockInfo();
        info.state = (capabilitySource) -> {
            if(recipeInfo == null) return new TLTraitMultiblockState(capabilitySource, traits.get());
            return new TLRecipeTraitMultiblockState(capabilitySource, traits.get(), recipeInfo.getCreateProcess());
        };

        info.tickLogic = recipeInfo == null ? (s, c) -> {} : (s, c) -> {
            if(c.getState() instanceof TLRecipeTraitMultiblockState) {
                ((TLRecipeTraitMultiblockState) c.getState()).process.tick(c.getLevel().getRawLevel());
            }
        };
        info.logic = new TLMultiblockLogic(pos -> shape.get(pos), info.tickLogic, info.tickLogic, info.state);


        info.registration = new IEMultiblockBuilder<>(info.logic, id.getPath())
            .defaultBEs(manager.getRegistry(id.getNamespace()).blockEntityType())
            .customBlock(
                manager.getRegistry(id.getNamespace()).blocks(), manager.getRegistry(id.getNamespace()).items(),
                r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                MultiblockItem::new)
            .structure(() -> multiblocksToRegister.get(id).get())
            .build();

        if(!hasCustomManualModel) manualModel = new TIDynamicModel(id.withPrefix("manual/"));

        info.multiblockClass = new TLMultiblock(id, masterOffset, triggerOffset, size, info.registration, manualModel, manualScale);
        multiblocksToRegister.put(id, Lazy.of(() -> info.multiblockClass));



        JEICategoryBuilder categoryBuilder = new JEICategoryBuilder(new RecipeType<>(id, ModularRecipe.class));
        if(jeiCategoryBuilder != null) {
            jeiCategoryBuilder.accept(categoryBuilder);
            info.jeiInfo = categoryBuilder.build();
        }


        info.reg = new TLMultiblockInfo(id, info.multiblockClass, info.state, info.logic, hasModelInfo() ? new TLMultiblockInfo.AssetGenerationData(blocks, modelJson, manualModelJson) : null, new TraitList(traits.get()), info.jeiInfo, recipeInfo, jeiCatalysts);
        consumer.accept(info.reg);

        multiblockInfo.put(id, info);

        return info.reg;
    }

    @Info("Creates the VoxelShape for the machines, use `/tl_editor shape` to open editor, then export to copy here.")
    public TLMultiblockBuilder shape(List<VoxelShape> shapes) {
        shape = new MachineShape(shapes);
        return this;
    }

    @Info("Provides a builder for a JEI category.")
    public TLMultiblockBuilder jeiCategory(Consumer<JEICategoryBuilder> jeiCategoryBuilder) {this.jeiCategoryBuilder = jeiCategoryBuilder; return this;}

    @Info("Adds a catalyst to another recipe type, good for making machines that perform existing recipes")
    public TLMultiblockBuilder jeiCatalyst(ResourceLocation id) {
        jeiCatalysts.add(new JeiRecipeCatalyst.ItemCatalyst(id, () -> ForgeRegistries.ITEMS.getValue(this.id)));
        return this;
    }

    @Info("Adds a catalyst to another recipe type, good for making machines that perform existing recipes")
    public TLMultiblockBuilder jeiCatalystItem(ResourceLocation id, Supplier<ItemLike> item) {
        jeiCatalysts.add(new JeiRecipeCatalyst.ItemCatalyst(id, item));
        return this;
    }

    @Info("Adds a catalyst to another recipe type, good for making machines that perform existing recipes")
    public TLMultiblockBuilder jeiCatalystItemStack(ResourceLocation id, Supplier<ItemStack> item) {
        jeiCatalysts.add(new JeiRecipeCatalyst.ItemStackCatalyst(id, item));
        return this;
    }

    @HideFromJS
    public static void init() {
        multiblocksToRegister.forEach((l,mb)->MultiblockHandler.registerMultiblock(mb.get()));
    }

    @HideFromJS
    private boolean hasModelInfo() {
        return modelJson != null && blocks.length != 0;
    }

    @HideFromJS
    private static class MultiblockInfo {
        TLMultiblockInfo reg;
        TLMultiblock multiblockClass;
        MultiblockRegistration<TLTraitMultiblockState> registration;
        TLMultiblockLogic logic;
        BiConsumer<TLMultiblockLogic, IMultiblockContext<TLTraitMultiblockState>> tickLogic;
        Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> state;
        JEICategoryBuilder.TLJEICategoryInfo jeiInfo;
    }
}
