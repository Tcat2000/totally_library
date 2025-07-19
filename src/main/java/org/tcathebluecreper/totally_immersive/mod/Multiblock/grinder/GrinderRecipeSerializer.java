package org.tcathebluecreper.totally_immersive.mod.Multiblock.grinder;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.tcathebluecreper.totally_immersive.api.crafting.ProviderList;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipe;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class GrinderRecipeSerializer extends TIRecipeSerializer<GrinderRecipe> {
    public GrinderRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, GrinderRecipe> constructor, Class<? extends TIRecipe> type) {
        super(constructor, type);
    }

    @Override
    public GrinderRecipe findRecipe(IMultiblockState state, Level level) {
        for(GrinderRecipe recipe : Util.memoize(
            (lvl) -> {
                List<GrinderRecipe> list =
                    new ArrayList<>(
                        GrinderRecipe.recipes.getRecipes((Level) lvl)
                            .stream()
                            .sorted(
                                Comparator.comparingInt(a -> a.priority.get())
                            )
                            .toList());
                Collections.reverse(list);
                return list;
            }).apply(level)) {
            if(recipe.checkCanExecute(state)) return recipe;
        }
        return null;
    }

    @Override
    public GrinderRecipe resumeRecipe(IMultiblockState state, Level level, Integer parallel) {
        for(GrinderRecipe recipe : Util.memoize(
            (lvl) -> {
                List<GrinderRecipe> list =
                    new ArrayList<>(
                        GrinderRecipe.recipes.getRecipes((Level) lvl)
                            .stream()
                            .sorted(
                                Comparator.comparingInt(a -> a.priority.get())
                            )
                            .toList());
                Collections.reverse(list);
                return list;
            }).apply(level)) {
            if(recipe.checkCanResume(state, parallel)) return recipe;
        }
        return null;
    }

    @Override
    public ProviderList<TIRecipeSerializer.Provider<?>> getProviders() {
        ProviderList<TIRecipeSerializer.Provider<?>> list = super.getProviders();
        list.add(new TIRecipeSerializer.IngredientProvider("inputItem"));
        list.add(new TIRecipeSerializer.ItemStackProvider("outputItem"));
        list.add(new TIRecipeSerializer.IntProvider("energyCost"));
        list.add(new TIRecipeSerializer.IntProvider("priority", 1));
        list.add(new TIRecipeSerializer.BooleanProvider("jeiHide", false));
        return list;
    }
}
