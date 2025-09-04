package org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.world.level.Level;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeSerializer;

import java.util.function.BiFunction;

public class RotaryKilnRecipeSerializer extends TLRecipeSerializer<RotaryKilnRecipe> {
    public RotaryKilnRecipeSerializer(BiFunction constructor, Class type) {
        super(constructor, type);
    }

    @Override
    public RotaryKilnRecipe findRecipe(IMultiblockState state, Level level) {
        return null;
    }

    @Override
    public RotaryKilnRecipe resumeRecipe(IMultiblockState state, Level level, Integer parallel) {
        return null;
    }

    @Override
    public ProviderList<Provider<?>> getProviders() {
        ProviderList<Provider<?>> list = super.getProviders();
        list.add(new IngredientProvider("inputItem"));
        list.add(new ItemStackProvider("outputItem"));
        list.add(new IntProvider("energyCost"));
        list.add(new IntProvider("priority", 1));
        list.add(new BooleanProvider("jeiHide", false));
        return list;
    }
}
