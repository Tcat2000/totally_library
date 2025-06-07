package org.tcathebluecreper.totally_immersive.api.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class TIRecipe implements Recipe<Container> {
    public final ResourceLocation id;
    public final ProviderList<?> providers;
    public TIRecipe(ResourceLocation id, ProviderList<?> providers) {
        this.id = id;
        this.providers = providers;
    }
    @Override
    public boolean matches(Container c, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container c, RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public final ResourceLocation getId() {
        return id;
    }
}
