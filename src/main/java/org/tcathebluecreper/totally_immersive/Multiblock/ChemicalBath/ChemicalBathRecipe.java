package org.tcathebluecreper.totally_immersive.Multiblock.ChemicalBath;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.TIContent;

import java.util.List;
import java.util.Optional;

public class ChemicalBathRecipe extends IESerializableRecipe {
    public static RegistryObject<IERecipeSerializer<ChemicalBathRecipe>> SERIALIZER;
    public static CachedRecipeList<ChemicalBathRecipe> recipes = new CachedRecipeList<>(TIContent.TIRecipes.CHEMICAL_BATH);

    public final ItemStack dummy;
    public final Ingredient input;
    public final Fluid fluidInput;
    public final ItemStack output;
    public final int energyCost;
    public final int fluidAmount;
    public final int fluidMinAmount;
    protected <T extends Recipe<?>> ChemicalBathRecipe(ItemStack outputDummy, ResourceLocation id, Ingredient input, FluidStack fluidInput, ItemStack output, int energyCost, int fluidAmount, int fluidMinAmount) {
        super(() -> outputDummy, TIContent.TIRecipes.CHEMICAL_BATH, id);
        this.dummy = outputDummy;
        this.input = input;
        this.fluidInput = fluidInput.getFluid();
        this.output = output;
        this.energyCost = energyCost;
        this.fluidAmount = fluidInput.getAmount();
        this.fluidMinAmount = fluidMinAmount;
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(input);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.output;
    }
    public static ChemicalBathRecipe findRecipe(Level level, ItemStack input, FluidStack fluid) {
        for(ChemicalBathRecipe recipe : recipes.getRecipes(level)) {
            if(recipe.input.test(input) && recipe.fluidInput == fluid.getFluid() && recipe.fluidAmount <= fluid.getAmount() && recipe.fluidMinAmount <= fluid.getAmount()) return recipe;
        }
        return null;
    }
    public static ChemicalBathRecipe recipeById(Level level, ResourceLocation id) {
        Optional<ChemicalBathRecipe> rec = recipes.getRecipes(level).stream().filter(recipe -> recipe.getId().equals(id)).findFirst();
        return rec.orElse(null);
    }
}
