package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tcathebluecreper.totally_lib.jei.JEICategoryBuilder;
import org.tcathebluecreper.totally_lib.jei.JeiRecipeCatalyst;
import org.tcathebluecreper.totally_lib.recipe.RecipeBuilder;
import org.tcathebluecreper.totally_lib.trait.ITrait;
import org.tcathebluecreper.totally_lib.trait.TraitList;

import java.util.List;
import java.util.function.Function;

public class TLMultiblockInfo {
    private static final Logger log = LogManager.getLogger(TLMultiblockInfo.class);
    private ResourceLocation id;
    public ResourceLocation getId() {return id;}
    private TLMultiblock multiblock;
    public TLMultiblock getMultiblock() {return multiblock;}
    private Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> state;
    public Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> getState() {return state;}
    private IMultiblockLogic<TLTraitMultiblockState> logic;
    public IMultiblockLogic<TLTraitMultiblockState> getLogic() {return logic;}
    private AssetGenerationData assetGenData;
    public AssetGenerationData getAssetGenData() {return assetGenData;}
    private TraitList traits;
    private JEICategoryBuilder.TLJEICategoryInfo jeiInfo;
    public JEICategoryBuilder.TLJEICategoryInfo getJeiInfo() {return jeiInfo;}
    private RecipeBuilder.RecipeInfo recipeType;
    public RecipeBuilder.RecipeInfo getRecipeType() {return recipeType;}
    public TraitList getTraits() {return traits;}
    private List<JeiRecipeCatalyst> recipeCatalysts;
    public List<JeiRecipeCatalyst> getRecipeCatalysts() {return recipeCatalysts;}

    public TLMultiblockInfo(ResourceLocation id, TLMultiblock multiblock, Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> state, IMultiblockLogic<TLTraitMultiblockState> logic, AssetGenerationData assetGenData, TraitList traits, JEICategoryBuilder.TLJEICategoryInfo jeiInfo, RecipeBuilder.RecipeInfo recipeType, List<JeiRecipeCatalyst> recipeCatalysts) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
        this.traits = traits;
        this.jeiInfo = jeiInfo;
        this.recipeType = recipeType;
        this.recipeCatalysts = recipeCatalysts;
    }
    public TLMultiblockInfo reconstruct(ResourceLocation id, TLMultiblock multiblock, Function<IInitialMultiblockContext<TLTraitMultiblockState>, TLTraitMultiblockState> state, IMultiblockLogic<TLTraitMultiblockState> logic, AssetGenerationData assetGenData, TraitList traits, JEICategoryBuilder.TLJEICategoryInfo jeiInfo, RecipeBuilder.RecipeInfo recipeType, List<JeiRecipeCatalyst> recipeCatalysts) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
        this.traits = traits;
        this.jeiInfo = jeiInfo;
        this.recipeType = recipeType;
        this.recipeCatalysts = recipeCatalysts;
        return this;
    }

    public boolean needsBER() {
        for(ITrait trait : traits) {
            if(trait.needsBER()) return true;
        }
        return false;
    }

    public BlockEntityRenderer<? extends MultiblockBlockEntityMaster<TLTraitMultiblockState>> createRenderer() {
        return new TLMultiblockRenderer();
    }


    public static class AssetGenerationData {
        private int[][] blocks;
        public int[][] getBlocks() {return blocks;}
        private JsonObject innerModel;
        public JsonObject getInnerModel() {return innerModel;}
        private JsonObject manualModel;
        public JsonObject getManualModel() {return manualModel;}

        public AssetGenerationData(int[][] blocks, JsonObject innerModel, JsonObject manualModel) {
            this.blocks = blocks;
            this.innerModel = innerModel;
            this.manualModel = manualModel;
        }
        public AssetGenerationData reconstruct(int[][] blocks, JsonObject innerModel, JsonObject manualModel) {
            this.blocks = blocks;
            this.innerModel = innerModel;
            this.manualModel = manualModel;
            return this;
        }
    }
}
