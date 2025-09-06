package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.function.TriFunction;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModularRecipe extends TLRecipe {
    public final RecipeSerializer<?> serializer;
    public final Supplier<RecipeType<?>> getRecipeType;
    public final int length;
    public final BiFunction<ModularRecipe, IMultiblockState, Boolean> checkCanExecuteFunction;
    public final ModularRecipeSerializer type;

    public ModularRecipe(ResourceLocation id, ProviderList<?> providers, RecipeSerializer<?> serializer, Supplier<RecipeType<?>> getRecipeType, int length, BiFunction<ModularRecipe, IMultiblockState, Boolean> checkCanExecuteFunction, TriFunction<ModularRecipe, IMultiblockState, Integer, Boolean> checkCanResumeFunction, ModularRecipeSerializer type) {
        super(id, providers);
        this.serializer = serializer;
        this.getRecipeType = getRecipeType;
        this.length = length;
        this.checkCanExecuteFunction = checkCanExecuteFunction;
        this.type = type;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public boolean checkCanExecute(IMultiblockState state) {
        return checkCanExecuteFunction.apply(this, state);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return getRecipeType.get();
    }
}
