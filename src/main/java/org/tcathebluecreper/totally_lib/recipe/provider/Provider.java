package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.recipe.TLRecipeSerializer;

public abstract class Provider<T> {
    public final String field;
    public final T value;

    protected Provider(String field, T value) {
        this.field = field;
        this.value = value;
    }

    public Provider(String field) {
        this.field = field;
        this.value = null;
    }

    public abstract Provider<T> fromJson(ResourceLocation recipeID, JsonObject json);

    public abstract void toNetwork(FriendlyByteBuf buf);

    public abstract Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf);

    public T get() {
        return value;
    }
}
