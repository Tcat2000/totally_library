package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.recipe.provider.Provider;

import java.util.NoSuchElementException;

public abstract class TLRecipe implements Recipe<Container> {
    private static final Logger log = LogManager.getLogger(TLRecipe.class);
    public final ResourceLocation id;
    public final ProviderList<?> providers;

    public TLRecipe(ResourceLocation id, ProviderList<?> providers) {
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

    public abstract int length();
    public abstract boolean checkCanExecute(IMultiblockState state);

    public Provider<?> getProvider(String id) {
        try {
            return providers.get(id).get();
        } catch(NoSuchElementException e) {
            log.error("Recipe does not have provider '{}': {}", id, e);
            return null;
        }
    }
}
