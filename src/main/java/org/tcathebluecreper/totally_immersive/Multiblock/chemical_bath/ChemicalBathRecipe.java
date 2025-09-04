package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

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
import org.tcathebluecreper.totally_immersive.TIContent;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeSerializer;

import java.util.*;

public class ChemicalBathRecipe extends TLRecipe {
    public static RegistryObject<ChemicalBathRecipeSerializer> SERIALIZER;
    public static CachedRecipeList<ChemicalBathRecipe> recipes = new CachedRecipeList<>(TIContent.TIRecipes.CHEMICAL_BATH);

    public final TLRecipeSerializer.IngredientProvider input;
    public final TLRecipeSerializer.FluidStackProvider fluidInput;
    public final TLRecipeSerializer.ItemStackProvider output;
    public final TLRecipeSerializer.IntProvider energyCost;
    public final TLRecipeSerializer.IntProvider fluidRequirement;
    public final TLRecipeSerializer.IntProvider priority;
    public final TLRecipeSerializer.BooleanProvider jeiHide;
    public <T extends Recipe<?>> ChemicalBathRecipe(ResourceLocation id, ProviderList<?> providers) {
        super(id, providers);
        this.input = (TLRecipeSerializer.IngredientProvider) providers.get("inputItem").get();
        this.output = (TLRecipeSerializer.ItemStackProvider) providers.get("outputItem").get();
        this.fluidInput = (TLRecipeSerializer.FluidStackProvider) providers.get("fluidInput").get();
        this.energyCost = (TLRecipeSerializer.IntProvider) providers.get("energyCost").get();
        this.fluidRequirement = (TLRecipeSerializer.IntProvider) providers.get("fluidRequirement").get();
        this.priority = (TLRecipeSerializer.IntProvider) providers.get("priority").get();
        this.jeiHide = (TLRecipeSerializer.BooleanProvider) providers.get("jeiHide").get();
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
