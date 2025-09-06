package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import org.tcathebluecreper.totally_lib.crafting.RecipeSerializationException;

public class ItemStackProvider extends Provider<ItemStack> {
    protected ItemStackProvider(String field, ItemStack value) {
        super(field, value);
    }

    public ItemStackProvider(String field) {
        super(field, null);
    }

    @Override
    public Provider<ItemStack> fromJson(ResourceLocation recipeID, JsonObject json) {
        JsonObject data = json.getAsJsonObject(field);
        if(!data.has("item")) throw new RecipeSerializationException(recipeID, "Missing json field 'item'");
        int count = data.has("count") ? data.get("count").getAsInt() : 1;
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(data.get("item").getAsString()));
        if(item == null)
            throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(data.get("item").getAsString()) + "'");
        return new ItemStackProvider(field, new ItemStack(item, count));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeItemStack(value, true);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new ItemStackProvider(field, buf.readItem());
    }

    public boolean canInsertTo(ItemStack stack) {
        assert value != null;
        if(stack.isEmpty()) return true;
        if(stack.getCount() + value.getCount() <= stack.getMaxStackSize()) {
            if(stack.getTag() == null) return value.getTag() == null;
            return stack.getTag().equals(value.getTag());
        }
        return false;
    }

    public boolean insertTo(IItemHandlerModifiable handler, int slot) {
        assert value != null;
        if(!canInsertTo(handler.getStackInSlot(slot))) return false;
        if(handler.getStackInSlot(slot).isEmpty()) handler.setStackInSlot(slot, value.copy());
        else handler.getStackInSlot(slot).setCount(handler.getStackInSlot(slot).getCount() + value.getCount());
        return true;
    }
}
