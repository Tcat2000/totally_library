package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IntProvider extends Provider<Integer> {
    protected final Integer defaultValue;

    protected IntProvider(String field, Integer value, Integer defaultValue) {
        super(field, value);
        this.defaultValue = defaultValue;
    }

    public IntProvider(String field, Integer value) {
        super(field, value);
        this.defaultValue = null;
    }

    public IntProvider(String field) {
        super(field, null);
        this.defaultValue = null;
    }

    @Override
    public Provider<Integer> fromJson(ResourceLocation recipeID, JsonObject json) {
        if(defaultValue != null && !json.has(field)) return new IntProvider(field, defaultValue);
        return new IntProvider(field, json.get(field).getAsInt(), null);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeInt(value);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new IntProvider(field, buf.readInt());
    }

    public boolean canExtract(FluidStack stack) {
        assert value != null;
        return stack.getAmount() >= value;
    }

    public boolean canExtract(ItemStack stack) {
        assert value != null;
        return stack.getCount() >= value;
    }
}
