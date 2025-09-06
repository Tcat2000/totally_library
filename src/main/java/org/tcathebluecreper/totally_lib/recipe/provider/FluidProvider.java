package org.tcathebluecreper.totally_lib.recipe.provider;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidProvider extends Provider<Fluid> {
    protected FluidProvider(String field, Fluid value) {
        super(field, value);
    }

    @Override
    public Provider<Fluid> fromJson(ResourceLocation recipeID, JsonObject json) {
        return new FluidProvider(field, ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(json.get(field).getAsString())));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf) {
        assert value != null;
        buf.writeFluidStack(new FluidStack(value, 1));
    }

    @Override
    public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
        return new FluidProvider(field, buf.readFluidStack().getFluid());
    }
}
