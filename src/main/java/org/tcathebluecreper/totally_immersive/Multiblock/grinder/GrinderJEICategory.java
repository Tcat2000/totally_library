package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.tcathebluecreper.totally_immersive.TIContent;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class GrinderJEICategory implements IRecipeCategory<GrinderRecipe> {
    public static RecipeType<GrinderRecipe> UID = new RecipeType<>(ResourceLocation.fromNamespaceAndPath(MODID, "chemical_bath"), GrinderRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawable TANK;
    private IDrawableAnimated ARROW;
    public GrinderJEICategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(TIMultiblocks.CHEMICAL_BATH.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chemical_bath.png"), 9, 22, 143, 59);
        this.TANK = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chemical_bath.png"), 197, 1, 18, 48);
    }
    @Override
    public RecipeType<GrinderRecipe> getRecipeType() {
        return UID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("totally_immersive.jei.chemical_bath.tittle");
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, GrinderRecipe grinderRecipe, IFocusGroup iFocusGroup) {

    }

    @Override
    public void draw(GrinderRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        TANK.draw(guiGraphics, 20, 10);
    }
}
