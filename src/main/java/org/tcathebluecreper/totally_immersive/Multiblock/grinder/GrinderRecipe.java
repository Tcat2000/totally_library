package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.TIContent;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.TLRecipe;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeSerializer;

import java.util.*;

public class GrinderRecipe extends TLRecipe {
    public static RegistryObject<GrinderRecipeSerializer> SERIALIZER;
    public static CachedRecipeList<GrinderRecipe> recipes = new CachedRecipeList<>(TIContent.TIRecipes.GRINDER);

    public final TLRecipeSerializer.IngredientProvider input;
    public final TLRecipeSerializer.ItemStackProvider output;
    public final TLRecipeSerializer.IntProvider energyCost;
    public final TLRecipeSerializer.IntProvider priority;
    public final TLRecipeSerializer.BooleanProvider jeiHide;
    public <T extends Recipe<?>> GrinderRecipe(ResourceLocation id, ProviderList<?> providers) {
        super(id, providers);
        this.input = (TLRecipeSerializer.IngredientProvider) providers.get("inputItem").get();
        this.output = (TLRecipeSerializer.ItemStackProvider) providers.get("outputItem").get();
        this.energyCost = (TLRecipeSerializer.IntProvider) providers.get("energyCost").get();
        this.priority = (TLRecipeSerializer.IntProvider) providers.get("priority").get();
        this.jeiHide = (TLRecipeSerializer.BooleanProvider) providers.get("jeiHide").get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.output.value;
    }

    @Override
    public int length() {
        return 60;
    }

    @Override
    public boolean checkCanExecute(IMultiblockState IState) {
        GrinderState state = (GrinderState) IState;
        for(int i = 0; i < state.processSlot.getValue().getSlots(); i++) {
            ItemStack itemstack = state.processSlot.getValue().getStackInSlot(i);
            if(!(itemstack.isEmpty() || input.canExtractFrom(itemstack))) return false;
        }
        return input.canExtractFrom(state.processSlot.getValue(), 0) || input.canExtractFromAny(state.input.getValue());
    }

    @Override
    public boolean checkCanResume(IMultiblockState IState, Integer parallel) {
        GrinderState state = (GrinderState) IState;
        for(int i = 0; i < state.processSlot.getValue().getSlots(); i++) {
            ItemStack itemstack = state.processSlot.getValue().getStackInSlot(i);
            if(!(itemstack.isEmpty() || input.canExtractFrom(itemstack))) return false;
        }
        return input.canExtractFrom(state.processSlot.getValue(), parallel) || input.canExtractFromAny(state.input.getValue());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return TIContent.TIRecipes.GRINDER.get();
    }

    public static GrinderRecipe findRecipe(Level level, ItemStack input, ItemStack output, FluidStack fluid) {
        for(GrinderRecipe recipe : Util.memoize(
            (lvl) -> {
                List<GrinderRecipe> list =
                    new ArrayList<>(
                        recipes.getRecipes((Level) lvl)
                            .stream()
                            .sorted(
                                Comparator.comparingInt(a -> a.priority.get())
                            )
                            .toList());
                Collections.reverse(list);
                return list;
            }).apply(level)) {
            if(recipe.validate(input, output)) return recipe;
        }
        return null;
    }
    public boolean validate(ItemStack input, ItemStack output) {
        return this.input.canExtractFrom(input) && this.output.canInsertTo(output);
    }
    public static GrinderRecipe recipeById(Level level, ResourceLocation id) {
        Optional<GrinderRecipe> rec = recipes.getRecipes(level).stream().filter(recipe -> recipe.getId().equals(id)).findFirst();
        return rec.orElse(null);
    }
}
