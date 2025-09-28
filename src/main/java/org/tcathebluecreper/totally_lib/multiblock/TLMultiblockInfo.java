package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tcathebluecreper.totally_lib.trait.ITrait;
import org.tcathebluecreper.totally_lib.trait.TraitList;

import java.util.function.Function;

public class TLMultiblockInfo {
    private static final Logger log = LogManager.getLogger(TLMultiblockInfo.class);
    private ResourceLocation id;
    public ResourceLocation getId() {return id;}
    private TLMultiblock multiblock;
    public TLMultiblock getMultiblock() {return multiblock;}
    private Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state;
    public Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> getState() {return state;}
    private IMultiblockLogic<TraitMultiblockState> logic;
    public IMultiblockLogic<TraitMultiblockState> getLogic() {return logic;}
    private AssetGenerationData assetGenData;
    public AssetGenerationData getAssetGenData() {return assetGenData;}
    private TraitList traits;
    public TraitList getTraits() {return traits;}

    public TLMultiblockInfo(ResourceLocation id, TLMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic, AssetGenerationData assetGenData, TraitList traits) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
        this.traits = traits;
    }
    public TLMultiblockInfo reconstruct(ResourceLocation id, TLMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic, AssetGenerationData assetGenData, TraitList traits) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
        this.traits = traits;
        return this;
    }

    public boolean needsBER() {
        for(ITrait trait : traits) {
            if(trait.needsBER()) return true;
        }
        return false;
    }

    public BlockEntityRenderer<? extends MultiblockBlockEntityMaster<TraitMultiblockState>> createRenderer() {
        return new TLMultiblockRenderer();
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
