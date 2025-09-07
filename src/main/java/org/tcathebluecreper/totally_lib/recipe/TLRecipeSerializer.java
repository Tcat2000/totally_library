package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.provider.Provider;

import java.util.*;
import java.util.function.BiFunction;

public abstract class TLRecipeSerializer<R extends TLRecipe> implements RecipeSerializer<R> {
    protected static final Map<Class<? extends TLRecipe>, BiFunction<IMultiblockState, Level, ? extends TLRecipe>> RecipeFinders = new HashMap<>();
    protected BiFunction<ResourceLocation, ProviderList<Provider<?>>, R> constructor;

    public ProviderList<Provider<?>> getProviders() {
        return new ProviderList<>();
    }

    public TLRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, R> constructor, Class<? extends TLRecipe> type) {
        this.constructor = constructor;
        RecipeFinders.put(type, this::findRecipe);
    }

    @Override
    public final @NotNull R fromJson(@NotNull ResourceLocation recipeID, @NotNull JsonObject jsonObject) {
        ProviderList<Provider<?>> list = new ProviderList<>();
        getProviders().forEach(provider -> {
            list.add(provider.fromJson(recipeID, jsonObject));
        });
        return constructor.apply(recipeID, list);
    }

    @Override
    public final @Nullable R fromNetwork(@NotNull ResourceLocation recipeID, @NotNull FriendlyByteBuf friendlyByteBuf) {
        ProviderList<Provider<?>> list = new ProviderList<>();
        getProviders().forEach(provider -> {
            list.add(provider.fromNetwork(recipeID, friendlyByteBuf));
        });
        return constructor.apply(recipeID, list);
    }

    @Override
    public final void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull R r) {
        getProviders().forEach(provider -> {
            provider.toNetwork(friendlyByteBuf);
        });
    }

    public abstract R findRecipe(IMultiblockState state, Level level);
    public abstract R getRecipe(ResourceLocation id);
}
