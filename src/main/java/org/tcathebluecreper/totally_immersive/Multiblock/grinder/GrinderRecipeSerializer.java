package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class GrinderRecipeSerializer extends TLRecipeSerializer<GrinderRecipe> {
    public GrinderRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, GrinderRecipe> constructor, Class<? extends TLRecipe> type) {
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
    public ProviderList<TLRecipeSerializer.Provider<?>> getProviders() {
        ProviderList<TLRecipeSerializer.Provider<?>> list = super.getProviders();
        list.add(new TLRecipeSerializer.IngredientProvider("inputItem"));
        list.add(new TLRecipeSerializer.ItemStackProvider("outputItem"));
        list.add(new TLRecipeSerializer.IntProvider("energyCost"));
        list.add(new TLRecipeSerializer.IntProvider("priority", 1));
        list.add(new TLRecipeSerializer.BooleanProvider("jeiHide", false));
        return list;
    }
}
