package org.tcathebluecreper.totally_immersive.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.crafting.TIRecipe;

public class RotaryKilnRecipe extends TIRecipe {
    public RotaryKilnRecipe(ResourceLocation id, ProviderList<?> providers) {
        super(id, providers);
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public boolean checkCanExecute(IMultiblockState state) {
        return false;
    }

    @Override
    public boolean checkCanResume(IMultiblockState state, Integer parallel) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
