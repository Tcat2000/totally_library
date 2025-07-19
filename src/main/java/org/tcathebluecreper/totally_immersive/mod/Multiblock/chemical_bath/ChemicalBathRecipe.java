package org.tcathebluecreper.totally_immersive.mod.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.mod.TIContent;
import org.tcathebluecreper.totally_immersive.api.crafting.ProviderList;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipe;
import org.tcathebluecreper.totally_immersive.api.crafting.TIRecipeSerializer;

import java.util.*;

public class ChemicalBathRecipe extends TIRecipe {
    public static RegistryObject<ChemicalBathRecipeSerializer> SERIALIZER;
    public static CachedRecipeList<ChemicalBathRecipe> recipes = new CachedRecipeList<>(TIContent.TIRecipes.CHEMICAL_BATH);

    public final TIRecipeSerializer.IngredientProvider input;
    public final TIRecipeSerializer.FluidStackProvider fluidInput;
    public final TIRecipeSerializer.ItemStackProvider output;
    public final TIRecipeSerializer.IntProvider energyCost;
    public final TIRecipeSerializer.IntProvider fluidRequirement;
    public final TIRecipeSerializer.IntProvider priority;
    public final TIRecipeSerializer.BooleanProvider jeiHide;
    public <T extends Recipe<?>> ChemicalBathRecipe(ResourceLocation id, ProviderList<?> providers) {
        super(id, providers);
        this.input = (TIRecipeSerializer.IngredientProvider) providers.get("inputItem").get();
        this.output = (TIRecipeSerializer.ItemStackProvider) providers.get("outputItem").get();
        this.fluidInput = (TIRecipeSerializer.FluidStackProvider) providers.get("fluidInput").get();
        this.energyCost = (TIRecipeSerializer.IntProvider) providers.get("energyCost").get();
        this.fluidRequirement = (TIRecipeSerializer.IntProvider) providers.get("fluidRequirement").get();
        this.priority = (TIRecipeSerializer.IntProvider) providers.get("priority").get();
        this.jeiHide = (TIRecipeSerializer.BooleanProvider) providers.get("jeiHide").get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.output.value;
    }

    @Override
    public int length() {
        return 140;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TIContent.TIRecipes.CHEMICAL_BATH.get();
    }

    @Override
    public boolean checkCanExecute(IMultiblockState IState) {
        ChemicalBathState state = (ChemicalBathState) IState;
        return this.input.canExtractFrom(state.input.getValue().getStackInSlot(0)) && this.output.canInsertTo(state.output.getValue().getStackInSlot(0)) && this.fluidInput.canExtract(state.tank.getFluid()) && this.fluidRequirement.canExtract(state.tank.getFluid());
    }
    @Override
    public boolean checkCanResume(IMultiblockState IState, Integer parallel) {
        ChemicalBathState state = (ChemicalBathState) IState;
        return this.input.canExtractFrom(state.processSlot.getValue().getStackInSlot(parallel)) && this.output.canInsertTo(state.output.getValue().getStackInSlot(0)) && this.fluidInput.canExtract(state.tank.getFluid()) && this.fluidRequirement.canExtract(state.tank.getFluid());
    }
    public static ChemicalBathRecipe recipeById(Level level, ResourceLocation id) {
        Optional<ChemicalBathRecipe> rec = recipes.getRecipes(level).stream().filter(recipe -> recipe.getId().equals(id)).findFirst();
        return rec.orElse(null);
    }
}
