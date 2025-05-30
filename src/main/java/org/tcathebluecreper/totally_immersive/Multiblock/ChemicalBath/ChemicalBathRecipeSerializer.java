package org.tcathebluecreper.totally_immersive.Multiblock.ChemicalBath;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.TIContent;

public class ChemicalBathRecipeSerializer extends IERecipeSerializer<ChemicalBathRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(TIContent.TIMultiblocks.CHEMICAL_BATH.blockItem().get());
    }

    @Override
    public ChemicalBathRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        return new ChemicalBathRecipe(new ItemStack(Items.AIR), recipeId, Ingredient.fromJson(json.get("itemInput")), ApiUtils.jsonDeserializeFluidStack(json.get("fluidInput").getAsJsonObject()), readOutput(json.get("output")).get(), json.get("energyCost").getAsInt(), json.get("fluidCost").getAsInt(), json.get("fluidRequirement").getAsInt());
    }

    @Override
    public @Nullable ChemicalBathRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return new ChemicalBathRecipe(buffer.readItem(), id, Ingredient.fromNetwork(buffer), buffer.readRegistryId(), buffer.readItem(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ChemicalBathRecipe recipe) {
        buffer.writeItemStack(recipe.dummy, true);
        recipe.input.toNetwork(buffer);
        buffer.writeRegistryId(ForgeRegistries.FLUIDS, recipe.fluidInput);
        buffer.writeItemStack(recipe.output, true);
        buffer.writeInt(recipe.energyCost);
        buffer.writeInt(recipe.fluidAmount);
        buffer.writeInt(recipe.fluidMinAmount);
    }
}
