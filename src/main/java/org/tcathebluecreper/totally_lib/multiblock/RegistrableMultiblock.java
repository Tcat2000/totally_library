package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RegistrableMultiblock {
    private ResourceLocation id;
    public ResourceLocation getId() {return id;}
    private TIMultiblock multiblock;
    public TIMultiblock getMultiblock() {return multiblock;}
    private Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state;
    public Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> getState() {return state;}
    private IMultiblockLogic<TraitMultiblockState> logic;
    public IMultiblockLogic<TraitMultiblockState> getLogic() {return logic;}
    private AssetGenerationData assetGenData;
    public AssetGenerationData getAssetGenData() {return assetGenData;}

    public RegistrableMultiblock(ResourceLocation id, TIMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic, AssetGenerationData assetGenData) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
    }
    public RegistrableMultiblock reconstruct(ResourceLocation id, TIMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic, AssetGenerationData assetGenData) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
        return this;
    }


    public static class AssetGenerationData {
        private int[][] blocks;
        public int[][] getBlocks() {return blocks;}
        private JsonObject innerModel;
        public JsonObject getInnerModel() {return innerModel;}

        public AssetGenerationData(int[][] blocks, JsonObject innerModel) {
            this.blocks = blocks;
            this.innerModel = innerModel;
        }
        public AssetGenerationData reconstruct(int[][] blocks, JsonObject innerModel) {
            this.blocks = blocks;
            this.innerModel = innerModel;
            return this;
        }
    }
}
