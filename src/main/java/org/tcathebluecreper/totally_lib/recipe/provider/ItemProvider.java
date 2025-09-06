package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemProvider extends Provider<Item> {
    protected ItemProvider(String field, Item value) {
        super(field, value);
    }

    @Override
    public Provider<Item> fromJson(ResourceLocation recipeID, JsonObject json) {
        return new ItemProvider(field, ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get(field).getAsString())));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeItem(value.getDefaultInstance());
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new ItemProvider(field, buf.readItem().getItem());
    }
}
