package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.tcathebluecreper.totally_immersive.api.crafting.ProviderList;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeSerializer;

import java.util.List;
import java.util.function.BiFunction;

public class ChemicalBathRecipeSerializer extends TIRecipeSerializer<ChemicalBathRecipe> {
    public ChemicalBathRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, ChemicalBathRecipe> constructor) {
        super(constructor);
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
        return list;
    }
}
