package org.tcathebluecreper.totally_immersive.Multiblock.ChemicalBath;

import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.tcathebluecreper.totally_immersive.TIContent;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class ChemicalBathJEICategory implements IRecipeCategory<ChemicalBathRecipe> {
    public static RecipeType<ChemicalBathRecipe> UID = new RecipeType<>(ResourceLocation.fromNamespaceAndPath(MODID, "chemical_bath"),ChemicalBathRecipe.class);
    private final IDrawable BACKGROUND;
    private final IDrawable ICON;
    private IDrawableAnimated ARROW;
    public ChemicalBathJEICategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(TIContent.TIMultiblocks.CHEMICAL_BATH.blockItem().get()));
        this.BACKGROUND = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chemical_bath.png"), 0, 0, 144, 59);
//        this.TANK = guiHelper.createDrawable(ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/chemical_bath.png"), 197, 1, 18, 48);
//        this.INPUT = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Ingredient.of(Items.DIAMOND.asItem()));
    }
    @Override
    public RecipeType<ChemicalBathRecipe> getRecipeType() {
        return UID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.totally_immersive.chemical_bath.tittle");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ChemicalBathRecipe chemicalBathRecipe, IFocusGroup iFocusGroup) {
        builder.addInputSlot(10, 16).addIngredients(chemicalBathRecipe.input);
        builder.addOutputSlot(24+94, 16).addItemLike(chemicalBathRecipe.output.getItem());
        builder.addInputSlot(24, 30).addFluidStack(chemicalBathRecipe.fluidInput).setFluidRenderer(1000, false, 96, 16);
    }

    @Override
    public void draw(ChemicalBathRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//        TANK.draw(guiGraphics, 20, 10);
    }
}
