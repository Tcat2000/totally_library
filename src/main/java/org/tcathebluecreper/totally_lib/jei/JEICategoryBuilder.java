package org.tcathebluecreper.totally_lib.jei;

import com.lowdragmc.lowdraglib.utils.Size;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.tcathebluecreper.totally_lib.recipe.ModularRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class JEICategoryBuilder {
    protected final RecipeType<ModularRecipe> recipeType;
    protected Component title = null;
    protected Size size = new Size(0,0);
    protected Function<IGuiHelper, IDrawable> icon = null;
    protected TLJEICategory.SetRecipeLogic setRecipeLogic = null;
    protected TLJEICategory.DrawLogic drawLogic = null;
    protected ArrayList<Supplier<ItemStack>> catalystItemStacks = new ArrayList<>();
    protected ArrayList<Supplier<Item>> catalystItems = new ArrayList<>();

    protected LinkedHashMap<String, Function<IGuiHelper, IDrawable>> createDrawables = new LinkedHashMap<>();
    protected HashMap<String, IDrawable> drawables = new HashMap<>();

    public JEICategoryBuilder title(Component component) {this.title = component; return this;}
    @RemapForJS("titleLang")
    public JEICategoryBuilder title(String translatable) {this.title = Component.translatable(translatable); return this;}
    @RemapForJS("titleText")
    public JEICategoryBuilder titleLiteral(String literal) {this.title = Component.literal(literal); return this;}

    public JEICategoryBuilder size(Size size) {this.size = size; return this;}
    public JEICategoryBuilder sizeX(int width) {this.size = new Size(width, size != null ? size.height : 0); return this;}
    public JEICategoryBuilder sizeY(int height) {this.size = new Size(size != null ? size.width : 0, height); return this;}

    public JEICategoryBuilder icon(Function<IGuiHelper, IDrawable> creator) {this.icon = creator; return this;}
    public JEICategoryBuilder iconItemStack(Supplier<ItemStack> item) {this.icon = helper -> helper.createDrawableItemStack(item.get()); return this;}
    public JEICategoryBuilder iconItem(Supplier<Item> item) {this.icon = helper -> helper.createDrawableItemLike(item.get()); return this;}

    public JEICategoryBuilder catalystItemStack(Supplier<ItemStack> item) {catalystItemStacks.add(item); return this;}
    public JEICategoryBuilder catalystItem(Supplier<Item> item) {catalystItems.add(item); return this;}

    public JEICategoryBuilder showRecipeLogic(TLJEICategory.SetRecipeLogic setRecipeLogic) {this.setRecipeLogic = setRecipeLogic; return this;}
    public JEICategoryBuilder drawLogic(TLJEICategory.DrawLogic drawLogic) {this.drawLogic = drawLogic; return this;}

    public JEICategoryBuilder addDrawable(Function<IGuiHelper, IDrawable> creator, String id) {createDrawables.put(id, creator); return this;}
    public IDrawable getDrawable(String id) {return drawables.get(id);}

    public JEICategoryBuilder(RecipeType<ModularRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @HideFromJS
    public TLJEICategoryInfo build() {
        return new TLJEICategoryInfo((helper) -> {
            drawables.clear();
            createDrawables.forEach((id, creator) -> {
                drawables.put(id, creator.apply(helper));
            });
            return new TLJEICategory(helper, recipeType, title, size, icon.apply(helper), setRecipeLogic, drawLogic);
        }, recipeType, catalystItemStacks, catalystItems);
    }

    public static class TLJEICategoryInfo {
        public final Function<IGuiHelper, TLJEICategory> constructor;
        public final RecipeType<ModularRecipe> UID;
        public final List<Supplier<ItemStack>> catalystItemStacks;
        public final List<Supplier<Item>> catalystItems;

        public TLJEICategoryInfo(Function<IGuiHelper, TLJEICategory> constructor, RecipeType<ModularRecipe> uid, List<Supplier<ItemStack>> catalystItemStacks, List<Supplier<Item>> catalystItems) {
            this.constructor = constructor;
            UID = uid;
            this.catalystItemStacks = catalystItemStacks;
            this.catalystItems = catalystItems;
        }
    }
}
