package org.tcathebluecreper.totally_lib.recipe;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TLBuiltRecipeInfo {
    public final Supplier<TLRegistrableRecipeSerializer> getSerializer;
    public final BiFunction<ResourceLocation, ProviderList<TLRegistrableRecipeSerializer.Provider<?>>, TLRegistrableRecipe> recipeConstructor;
    public final ProviderList<TLRegistrableRecipeSerializer.Provider<?>> recipeProviders;
    public final Function<TraitMultiblockState, TLCraftingRecipeProcess<TLRegistrableRecipe, TraitMultiblockState>> createProcess;

    public TLBuiltRecipeInfo(Supplier<TLRegistrableRecipeSerializer> getSerializer, BiFunction<ResourceLocation, ProviderList<TLRegistrableRecipeSerializer.Provider<?>>, TLRegistrableRecipe> recipeConstructor, ProviderList<TLRegistrableRecipeSerializer.Provider<?>> recipeProviders, Function<TraitMultiblockState, TLCraftingRecipeProcess<TLRegistrableRecipe, TraitMultiblockState>> createProcess) {
        this.getSerializer = getSerializer;
        this.recipeConstructor = recipeConstructor;
        this.recipeProviders = recipeProviders;
        this.createProcess = createProcess;
    }
}
