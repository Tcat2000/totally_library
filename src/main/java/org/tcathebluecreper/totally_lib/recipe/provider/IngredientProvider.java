package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_lib.crafting.RecipeSerializationException;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IngredientProvider extends Provider<Ingredient> {
    protected IngredientProvider(String field, Ingredient value) {
        super(field, value);
    }

    public IngredientProvider(String field) {
        super(field, null);
    }

    @Override
    public Provider<Ingredient> fromJson(ResourceLocation recipeID, JsonObject json) {
        JsonObject data = json.getAsJsonObject(field);
        if(data == null) throw new RecipeSerializationException(recipeID, "Missing '" + field + "'");
        int count = data.has("amount") ? data.get("amount").getAsInt() : 1;
        if(data.has("item")) {
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(data.get("item").getAsString()));
            if(item == null)
                throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(data.get("item").getAsString()) + "'");
            return new IngredientProvider(field, Ingredient.of(new ItemStack(item, count)));
        }
        if(data.has("items")) {
            JsonArray itemObj = data.getAsJsonArray("items");
            return new IngredientProvider(field, Ingredient.of(itemObj.asList().stream().map(j -> {
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(j.getAsString()));
                if(item == null)
                    throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(j.getAsString()) + "'");
                return new ItemStack(item, count);
            })));
        }
        if(data.has("tag")) {
            return new IngredientProvider(field, Ingredient.fromValues(Stream.of(new Ingredient.TagValue(TagKey.create(Registries.ITEM, ResourceLocation.parse(data.get("tag").getAsString()))) {
                @Override
                public @NotNull Collection<ItemStack> getItems() {
                    return super.getItems().stream().peek(stack -> stack.setCount(count)).collect(Collectors.toSet());
                }
            })));
        }
        if(data.has("tags")) {
            JsonArray tagObj = data.getAsJsonArray("tags");
            Ingredient.fromValues(tagObj.asList().stream().map(j -> new Ingredient.TagValue(TagKey.create(Registries.ITEM, ResourceLocation.parse(tagObj.getAsString()))) {
                @Override
                public @NotNull Collection<ItemStack> getItems() {
                    return super.getItems().stream().peek(stack -> stack.setCount(count)).collect(Collectors.toSet());
                }
            }));
        } else
            throw new RecipeSerializationException(recipeID, "Ingredient '" + field + "' missing value, needs 'item', 'items', 'tag', or 'tags'");
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        value.toNetwork(buf);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new IngredientProvider(field, Ingredient.fromNetwork(buf));
    }

    public boolean canExtractFrom(IItemHandler handler, int slot) {
        return canExtractFrom(handler.getStackInSlot(slot));
    }

    public boolean canExtractFrom(ItemStack stack) {
        assert value != null;
        for(ItemStack itemstack : value.getItems()) {
            if(itemstack.is(stack.getItem()) && itemstack.getCount() <= stack.getCount()) {
                return true;
            }
        }
        return false;
    }

    public boolean canExtractFromAny(IItemHandler handler) {
        return canExtractFromAny(handler, 0, handler.getSlots());
    }

    public boolean canExtractFromAny(IItemHandler handler, int min, int max) {
        for(int i = min; i < max; i++) {
            if(canExtractFrom(handler, i)) return true;
        }
        return false;
    }

    public ItemStack extractFrom(IItemHandler handler, int slot) {
        return extractFrom(handler.getStackInSlot(slot));
    }

    public ItemStack extractFrom(ItemStack stack) {
        assert value != null;
        for(ItemStack itemstack : value.getItems()) {
            if(itemstack.is(stack.getItem()) && itemstack.getCount() <= stack.getCount()) {
                stack.setCount(stack.getCount() - itemstack.getCount());
                return itemstack.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack extractFromAny(IItemHandler handler) {
        return extractFromAny(handler, 0, handler.getSlots());
    }

    public ItemStack extractFromAny(IItemHandler handler, int min, int max) {
        assert value != null;
        for(int i = min; i < max; i++) {
            if(canExtractFrom(handler, i)) return extractFrom(handler, i);
        }
        return ItemStack.EMPTY;
    }
}
