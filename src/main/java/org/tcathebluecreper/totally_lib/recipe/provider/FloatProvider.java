package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class FloatProvider extends Provider<Float> {
    protected FloatProvider(String field, Float value) {
        super(field, value);
    }

    @Override
    public Provider<Float> fromJson(ResourceLocation recipeID, JsonObject json) {
        return new FloatProvider(field, json.get(field).getAsFloat());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeFloat(value);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new IntProvider(field, buf.readInt());
    }
}
