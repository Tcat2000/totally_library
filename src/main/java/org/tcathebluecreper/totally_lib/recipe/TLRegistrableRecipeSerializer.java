package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathRecipe;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TLRegistrableRecipeSerializer extends TLRecipeSerializer<TLRegistrableRecipe> {
    private static final List<TLRegistrableRecipeSerializer> serializers = new ArrayList<>();
    public static List<TLRegistrableRecipeSerializer> getSerializers() {return serializers;}


    public final ProviderList<Provider<?>> providers;
    private List<TLRegistrableRecipe> recipes = new ArrayList<>();
    private final IERecipeTypes.TypeWithClass<TLRegistrableRecipe> ieType;
    private final CachedRecipeList<TLRegistrableRecipe> allRecipes;

    public TLRegistrableRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, TLRegistrableRecipe> constructor, Class<? extends TLRecipe> type, ProviderList<Provider<?>> providers, IERecipeTypes.TypeWithClass<TLRegistrableRecipe> ieType) {
        super(constructor, type);
        this.providers = providers;
        serializers.add(this);
        this.ieType = ieType;
        this.allRecipes = new CachedRecipeList<>(ieType);
    }

    @Override
    public TLRegistrableRecipe findRecipe(IMultiblockState state, Level level) {
//        for(ChemicalBathRecipe recipe : Util.memoize(
//        (lvl) -> {
//            List<TLRecipe> list =
//                new ArrayList<>(
//                    ChemicalBathRecipe.recipes.getRecipes((Level) lvl)
//                        .stream()
//                        .sorted(
//                            Comparator.comparingInt(a -> a.priority.get())
//                        )
//                        .toList());
//            Collections.reverse(list);
//            return list;
//        }).apply(level)) {
//        if(recipe.checkCanExecute(state)) return recipe;
        return null;
    }

    @Override
    public TLRegistrableRecipe resumeRecipe(IMultiblockState state, Level level, Integer parallel) {
        return null;
    }

    @Override
    public ProviderList<Provider<?>> getProviders() {
        return providers;
    }

    @Override
    public TLRegistrableRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context) {
        TLRegistrableRecipe recipe = super.fromJson(recipeLoc, recipeJson, context);
        recipes.add(recipe);
        return recipe;
    }

    public List<TLRegistrableRecipe> getRecipes() {
        recipes = new ArrayList<>();

        allRecipes.getRecipes(Minecraft.getInstance().level).forEach(recipe -> {
            if(recipe.type == this) recipes.add(recipe);
        });
        return recipes;
    }

    public void clearRecipes() {
        recipes = new ArrayList<>();
    }
}
