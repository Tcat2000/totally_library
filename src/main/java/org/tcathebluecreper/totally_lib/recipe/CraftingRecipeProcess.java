package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;
import org.tcathebluecreper.totally_lib.crafting.TIAPIException;
import org.tcathebluecreper.totally_lib.recipe.action.Action;

import java.util.List;
import java.util.function.BiFunction;

public class CraftingRecipeProcess<R extends TLRecipe,S extends IMultiblockState> extends TLRecipeProcess<R,S> {
    private final TLRecipeSerializer<R> serializer;
    public CraftingRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, TriFunction<TLRecipeProcess<R, S>, Integer, Integer, Boolean> tickLogic, TLRecipeSerializer<R> serializer) {
        super(type, actions, state, tickLogic);
        this.serializer = serializer;
    }

    public CraftingRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, TriFunction<TLRecipeProcess<R, S>, Integer, Integer, Boolean> tickLogic, int initialTick, TLRecipeSerializer<R> serializer) {
        super(type, actions, state, tickLogic, initialTick);
        this.serializer = serializer;
    }

    public CraftingRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, TriFunction<TLRecipeProcess<R, S>, Integer, Integer, Boolean> tickLogic, int[] initialTick, int maxParallel, boolean allowDifferentRecipes, TLRecipeSerializer<R> serializer) {
        super(type, actions, state, tickLogic, initialTick, maxParallel, allowDifferentRecipes);
        this.serializer = serializer;
    }

    public CraftingRecipeProcess(Class<R> type, List<Action<R, S>> actions, S state, TriFunction<TLRecipeProcess<R, S>, Integer, Integer, Boolean> tickLogic, int maxParallel, boolean allowDifferentRecipes, TLRecipeSerializer<R> serializer) {
        super(type, actions, state, tickLogic, maxParallel, allowDifferentRecipes);
        this.serializer = serializer;
    }

    public R findRecipe(Level level) {
        try {
            return serializer.findRecipe(state, level);
        } catch(ClassCastException e) {
            throw new TIAPIException("A serializer for recipe " + type + " was initialized with an invalid type class, should the the same as the recipe it is for.");
        }
    }
    public R getRecipe(ResourceLocation id) {
        return serializer.getRecipe(id);
    }
}
