package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.crafting.RecipeSerializationException;

public class BooleanProvider extends Provider<Boolean> {
    protected final Boolean defaultValue;

    protected BooleanProvider(String field, Boolean value, Boolean defaultValue) {
        super(field, value);
        this.defaultValue = defaultValue;
    }

    public BooleanProvider(String field, Boolean value) {
        super(field, value);
        this.defaultValue = value;
    }

    public BooleanProvider(String field) {
        super(field, null);
        this.defaultValue = null;
    }

    @Override
    public Provider<Boolean> fromJson(ResourceLocation recipeID, JsonObject json) {
        if(defaultValue != null && !json.has(field)) return new BooleanProvider(field, defaultValue);
        if(!json.has(field)) throw new RecipeSerializationException(recipeID, "Missing field '" + field + "'");
        return new BooleanProvider(field, json.get(field).getAsBoolean(), null);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeBoolean(value);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new BooleanProvider(field, buf.readBoolean(), null);
    }
}
