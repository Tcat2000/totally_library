package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import net.minecraft.nbt.CompoundTag;
import org.tcathebluecreper.totally_lib.trait.ITrait;
import org.tcathebluecreper.totally_lib.trait.TraitList;
import org.tcathebluecreper.totally_lib.recipe.CraftingRecipeProcess;
import org.tcathebluecreper.totally_lib.recipe.ModularRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeProcess;

import java.util.List;
import java.util.function.Function;

public class RecipeTraitMultiblockState extends TraitMultiblockState {
    public TLRecipeProcess<ModularRecipe, TraitMultiblockState> process;

    public RecipeTraitMultiblockState(TraitList traits, Function<TraitMultiblockState, TLRecipeProcess<ModularRecipe, TraitMultiblockState>> process) {
        super(traits);
        this.process = process.apply(this);
    }

    public RecipeTraitMultiblockState(IInitialMultiblockContext capSource, List<ITrait> traits, Function<TraitMultiblockState, CraftingRecipeProcess<ModularRecipe, TraitMultiblockState>> process) {
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
