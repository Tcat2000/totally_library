package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.tcathebluecreper.totally_immersive.api.crafting.ProviderList;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipe;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class ChemicalBathRecipeSerializer extends TIRecipeSerializer<ChemicalBathRecipe> {
    public ChemicalBathRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, ChemicalBathRecipe> constructor, Class<? extends TIRecipe> type) {
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
