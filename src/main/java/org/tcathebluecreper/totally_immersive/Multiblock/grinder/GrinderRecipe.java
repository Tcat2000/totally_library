package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.TIContent;

import java.util.List;
import java.util.Optional;

public class GrinderRecipe extends IESerializableRecipe {
    public static RegistryObject<IERecipeSerializer<GrinderRecipe>> SERIALIZER;
    public static CachedRecipeList<GrinderRecipe> recipes = new CachedRecipeList<>(TIContent.TIRecipes.GRINDER);

    public final ItemStack dummy;
    public final Ingredient input;
    public final List<StackWithChance> outputs;
    public final int energyCost;
    protected <T extends Recipe<?>> GrinderRecipe(ItemStack outputDummy, ResourceLocation id, Ingredient input, List<StackWithChance> outputs, int energyCost) {
        super(() -> outputDummy, TIContent.TIRecipes.CHEMICAL_BATH, id);
        this.dummy = outputDummy;
        this.input = input;
        this.outputs = outputs;
        this.energyCost = energyCost;
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer() {
        return SERIALIZER.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.outputs.get(0).stack().get();
    }
    public static GrinderRecipe findRecipe(Level level, ItemStack input) {
        for(GrinderRecipe recipe : recipes.getRecipes(level)) {
            if(recipe.input.test(input)) return recipe;
        }
        return null;
    }
    public static GrinderRecipe recipeById(Level level, ResourceLocation id) {
        Optional<GrinderRecipe> rec = recipes.getRecipes(level).stream().filter(recipe -> recipe.getId().equals(id)).findFirst();
        return rec.orElse(null);
    }
}
