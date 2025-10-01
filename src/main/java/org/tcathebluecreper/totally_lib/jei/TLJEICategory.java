package org.tcathebluecreper.totally_lib.jei;

import com.lowdragmc.lowdraglib.utils.Size;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_lib.recipe.ModularRecipe;

public class TLJEICategory implements IRecipeCategory<ModularRecipe> {
    private final RecipeType<ModularRecipe> recipeType;
    public final Component title;
    public final Size size;
    public final IDrawable icon;
    private final SetRecipeLogic setRecipeLogic;
    private final DrawLogic drawLogic;

    private final IDrawable nullIcon;

    public TLJEICategory(IGuiHelper guiHelper, RecipeType<ModularRecipe> recipeType, Component title, Size size, IDrawable icon, SetRecipeLogic setRecipeLogic, DrawLogic drawLogic) {
        this.recipeType = recipeType;
        this.title = title;
        this.size = size;
        this.icon = icon;
        this.setRecipeLogic = setRecipeLogic;
        this.drawLogic = drawLogic;


        this.nullIcon = guiHelper.createDrawableItemStack(Items.DEBUG_STICK.getDefaultInstance());
    }

    @Override
    public RecipeType<ModularRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title != null ? title : Component.literal("unassigned");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon != null ? icon : nullIcon;
    }

    @Override
    public int getWidth() {
        return size.width;
    }

    @Override
    public int getHeight() {
        return size.height;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ModularRecipe tlRecipe, IFocusGroup iFocusGroup) {
        if(setRecipeLogic != null) setRecipeLogic.setRecipe(iRecipeLayoutBuilder, tlRecipe, iFocusGroup);
    }

    @Override
    public void draw(ModularRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if(drawLogic != null) drawLogic.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    public interface SetRecipeLogic {
        void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ModularRecipe tlRecipe, IFocusGroup iFocusGroup);
    }
    public interface DrawLogic {
        void draw(ModularRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY);
    }
}
