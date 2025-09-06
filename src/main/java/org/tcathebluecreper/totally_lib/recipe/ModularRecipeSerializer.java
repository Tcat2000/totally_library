package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class ModularRecipeSerializer extends TLRecipeSerializer<ModularRecipe> {
    private static final List<ModularRecipeSerializer> serializers = new ArrayList<>();
    public static List<ModularRecipeSerializer> getSerializers() {return serializers;}


    public final ProviderList<Provider<?>> providers;
    private List<ModularRecipe> recipes = new ArrayList<>();
    private final IERecipeTypes.TypeWithClass<ModularRecipe> ieType;
    private final CachedRecipeList<ModularRecipe> allRecipes;

    public ModularRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, ModularRecipe> constructor, Class<? extends TLRecipe> type, ProviderList<Provider<?>> providers, IERecipeTypes.TypeWithClass<ModularRecipe> ieType) {
        super(constructor, type);
        this.providers = providers;
        serializers.add(this);
        this.ieType = ieType;
        this.allRecipes = new CachedRecipeList<>(ieType);
    }

    @Override
    public ModularRecipe findRecipe(IMultiblockState state, Level level) {
        for(ModularRecipe recipe : recipes) {
            if(recipe.checkCanExecute(state)) return recipe;
        }
        return null;
    }

    @Override
    public ModularRecipe getRecipe(ResourceLocation id) {
        for(ModularRecipe recipe : recipes) {
            if(recipe.id == id) return recipe;
        }
        return null;
    }

    @Override
    public ProviderList<Provider<?>> getProviders() {
        return providers;
    }

    @Override
    public ModularRecipe fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context) {
        ModularRecipe recipe = super.fromJson(recipeLoc, recipeJson, context);
        recipes.add(recipe);
        return recipe;
    }

    public List<ModularRecipe> getRecipes() {
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
