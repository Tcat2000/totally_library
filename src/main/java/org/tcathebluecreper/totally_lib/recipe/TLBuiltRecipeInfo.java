package org.tcathebluecreper.totally_lib.recipe;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TLBuiltRecipeInfo {
    public final Supplier<TLRegistrableRecipeSerializer> getSerializer;
    public final BiFunction<ResourceLocation, ProviderList<TLRecipeSerializer.Provider<?>>, TLRegistrableRecipe> recipeConstructor;
    public final ProviderList<TLRecipeSerializer.Provider<?>> recipeProviders;
    public final Function<TraitMultiblockState, TLRecipeProcess<TLRecipe, TraitMultiblockState>> createProcess;

    public TLBuiltRecipeInfo(Supplier<TLRegistrableRecipeSerializer> getSerializer, BiFunction<ResourceLocation, ProviderList<TLRecipeSerializer.Provider<?>>, TLRegistrableRecipe> recipeConstructor, ProviderList<TLRecipeSerializer.Provider<?>> recipeProviders, Function<TraitMultiblockState, TLRecipeProcess<TLRecipe, TraitMultiblockState>> createProcess) {
        this.getSerializer = getSerializer;
        this.recipeConstructor = recipeConstructor;
        this.recipeProviders = recipeProviders;
        this.createProcess = createProcess;
    }
}
