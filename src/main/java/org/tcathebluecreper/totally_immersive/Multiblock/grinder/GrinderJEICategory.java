package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import java.util.List;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class GrinderJEICategory implements IRecipeCategory<GrinderRecipe> {
    public static RecipeType<GrinderRecipe> UID = new RecipeType<>(ResourceLocation.fromNamespaceAndPath(MODID, "grinder"), GrinderRecipe.class);
    private IDrawable BACKGROUND;
    private IDrawable ICON;
    private IDrawableAnimated ARROW;

    public GrinderJEICategory(IGuiHelper guiHelper) {
        this.ICON = guiHelper.createDrawableItemStack(new ItemStack(TIMultiblocks.GRINDER.blockItem().get()));
    }

    @Override
    public RecipeType<GrinderRecipe> getRecipeType() {
        return UID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.totally_immersive.grinder.tittle");
    }

    @Override
    public int getWidth() {
        return 144;
    }

    @Override
    public int getHeight() {
        return 59;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GrinderRecipe recipe, IFocusGroup iFocusGroup) {
        builder.addInputSlot(10, 16).addItemStacks(List.of(recipe.input.value.getItems()));
        builder.addOutputSlot(24 + 94, 16).addItemStack(recipe.output.value);
    }
}
