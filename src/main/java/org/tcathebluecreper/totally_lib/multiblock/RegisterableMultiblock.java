package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RegisterableMultiblock {
    public final ResourceLocation id;
    public final TIMultiblock multiblock;
    public final Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state;
    public final IMultiblockLogic<TraitMultiblockState> logic;
    public final AssetGenerationData assetGenData;

    public RegisterableMultiblock(ResourceLocation id, TIMultiblock multiblock, Function<IInitialMultiblockContext<TraitMultiblockState>, TraitMultiblockState> state, IMultiblockLogic<TraitMultiblockState> logic, AssetGenerationData assetGenData) {
        this.id = id;
        this.multiblock = multiblock;
        this.state = state;
        this.logic = logic;
        this.assetGenData = assetGenData;
    }


    public static class AssetGenerationData {
        public final int[][] blocks;
        public final JsonObject innerModel;

        public AssetGenerationData(int[][] blocks, JsonObject innerModel) {
            this.blocks = blocks;
            this.innerModel = innerModel;
        }
    }
}
