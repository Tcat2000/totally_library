package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

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

public class ChemicalBathRecipeSerializer extends TLRecipeSerializer<ChemicalBathRecipe> {
    public ChemicalBathRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, ChemicalBathRecipe> constructor, Class<? extends TLRecipe> type) {
        super(constructor, type);
    }

    @Override
    public ChemicalBathRecipe findRecipe(IMultiblockState state, Level level) {
        for(ChemicalBathRecipe recipe : Util.memoize(
            (lvl) -> {
                List<ChemicalBathRecipe> list =
                    new ArrayList<>(
                        ChemicalBathRecipe.recipes.getRecipes((Level) lvl)
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
    public ChemicalBathRecipe resumeRecipe(IMultiblockState state, Level level, Integer parallel) {
        for(ChemicalBathRecipe recipe : Util.memoize(
            (lvl) -> {
                List<ChemicalBathRecipe> list =
                    new ArrayList<>(
                        ChemicalBathRecipe.recipes.getRecipes((Level) lvl)
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
    public ProviderList<Provider<?>> getProviders() {
        ProviderList<Provider<?>> list = super.getProviders();
        list.add(new IngredientProvider("inputItem"));
        list.add(new ItemStackProvider("outputItem"));
        list.add(new FluidStackProvider("fluidInput"));
        list.add(new IntProvider("energyCost"));
        list.add(new IntProvider("fluidRequirement"));
        list.add(new IntProvider("priority", 1));
        list.add(new BooleanProvider("jeiHide", false));
        return list;
    }
}
