package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.tcathebluecreper.totally_lib.crafting.RecipeSerializationException;

public class FluidStackProvider extends Provider<FluidStack> {
    protected FluidStackProvider(String field, FluidStack value) {
        super(field, value);
    }

    public FluidStackProvider(String field) {
        super(field, null);
    }

    @Override
    public Provider<FluidStack> fromJson(ResourceLocation recipeID, JsonObject json) {
        JsonObject data = json.getAsJsonObject(field);
        if(!data.has("fluid"))
            throw new RecipeSerializationException(recipeID, "FluidStack field '" + field + "' missing field for 'fluid'");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(data.get("fluid").getAsString()));
        if(fluid == null)
            throw new RecipeSerializationException(recipeID, "Cannot find fluid '" + ResourceLocation.parse(data.get("fluid").getAsString()) + "'");
        return new FluidStackProvider(field, new FluidStack(fluid, data.has("amount") ? data.get("amount").getAsInt() : 1));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeFluidStack(value);
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new FluidStackProvider(field, buf.readFluidStack());
    }

    public boolean canExtract(FluidStack stack) {
        assert value != null;
        if(!value.getFluid().isSame(stack.getFluid())) return false;
        return stack.getAmount() >= value.getAmount();
    }

    public FluidStack extract(FluidStack stack) {
        assert value != null;
        if(!canExtract(stack)) return FluidStack.EMPTY;
        stack.setAmount(stack.getAmount() - value.getAmount());
        return new FluidStack(stack, value.getAmount());
    }
}
