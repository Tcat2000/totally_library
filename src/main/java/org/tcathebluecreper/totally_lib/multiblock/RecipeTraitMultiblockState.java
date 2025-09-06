package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import net.minecraft.nbt.CompoundTag;
import org.tcathebluecreper.totally_lib.multiblock.trait.ITrait;
import org.tcathebluecreper.totally_lib.multiblock.trait.TraitHolder;
import org.tcathebluecreper.totally_lib.recipe.TLCraftingRecipeProcess;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;
import org.tcathebluecreper.totally_lib.recipe.TLRegistrableRecipe;

import java.util.List;
import java.util.function.Function;

public class RecipeTraitMultiblockState extends TraitMultiblockState {
    public TLRecipeProcess<TLRegistrableRecipe, TraitMultiblockState> process;

    public RecipeTraitMultiblockState(TraitHolder traits, Function<TraitMultiblockState, TLRecipeProcess<TLRegistrableRecipe, TraitMultiblockState>> process) {
        super(traits);
        this.process = process.apply(this);
    }

    public RecipeTraitMultiblockState(IInitialMultiblockContext capSource, List<ITrait> traits, Function<TraitMultiblockState, TLCraftingRecipeProcess<TLRegistrableRecipe, TraitMultiblockState>> process) {
        super(capSource, traits);
        this.process = process.apply(this);
    }

    @Override
    public void writeSaveNBT(CompoundTag nbt) {
        super.writeSaveNBT(nbt);
        nbt.put("recipe", process.serialize());
    }

    @Override
    public void writeSyncNBT(CompoundTag nbt) {
        super.writeSyncNBT(nbt);
        nbt.put("recipe", process.serialize());
    }

    @Override
    public void readSaveNBT(CompoundTag nbt) {
        super.readSaveNBT(nbt);
        process.deserialize(nbt.getCompound("recipe"));
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        super.readSyncNBT(nbt);
        process.deserialize(nbt.getCompound("recipe"));
    }
}
