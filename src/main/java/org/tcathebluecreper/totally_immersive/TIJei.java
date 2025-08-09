package org.tcathebluecreper.totally_immersive;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathJEICategory;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.ChemicalBathRecipe;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.GrinderJEICategory;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.GrinderRecipe;

import java.util.ArrayList;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

@JeiPlugin
public class TIJei implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(TIMultiblocks.CHEMICAL_BATH.blockItem().get()), ChemicalBathJEICategory.UID);
        registration.addRecipeCatalyst(new ItemStack(TIMultiblocks.GRINDER.blockItem().get()), GrinderJEICategory.UID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        registration.addRecipes(ChemicalBathJEICategory.UID, new ArrayList<>(ChemicalBathRecipe.recipes.getRecipes(world)).stream().filter((r) -> !r.jeiHide.get()).toList());
        registration.addRecipes(GrinderJEICategory.UID, new ArrayList<>(GrinderRecipe.recipes.getRecipes(world)).stream().filter((r) -> !r.jeiHide.get()).toList());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ChemicalBathJEICategory(guiHelper),
                new GrinderJEICategory(guiHelper)
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IIngredientManager manager = registration.getJeiHelpers().getIngredientManager();
//        registration.addGenericGuiContainerHandler(ChemicalBathJEIScreen.class, new JEIGuiContainerHandler<>(manager, 76, 35, 21, 25, ChemicalBathJEICategory.UID));
    }
}
