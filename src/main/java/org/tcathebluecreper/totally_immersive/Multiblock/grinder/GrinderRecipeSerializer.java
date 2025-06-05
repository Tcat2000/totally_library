package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.TIContent;
import org.tcathebluecreper.totally_immersive.TIMultiblocks;

import java.util.ArrayList;
import java.util.List;

public class GrinderRecipeSerializer extends IERecipeSerializer<GrinderRecipe> {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(TIMultiblocks.CHEMICAL_BATH.blockItem().get());
    }

    @Override
    public GrinderRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
        System.out.println(recipeId);
        System.out.println(Ingredient.fromJson(json.get("itemInput")));
        System.out.println(readOutput(json.get("outputs")).get());
        return new GrinderRecipe(new ItemStack(Items.AIR), recipeId, Ingredient.fromJson(json.get("itemInput")), readOutputs(json.get("output").getAsJsonArray()), json.get("energyCost").getAsInt());
    }

    @Override
    public @Nullable GrinderRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return new GrinderRecipe(buffer.readItem(), id, Ingredient.fromNetwork(buffer), readOutputs(buffer), buffer.readInt());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, GrinderRecipe recipe) {
        buffer.writeItemStack(recipe.dummy, true);
        recipe.input.toNetwork(buffer);
        writeOutputs(buffer, recipe.outputs);
        buffer.writeInt(recipe.energyCost);
    }

    public List<StackWithChance> readOutputs(JsonArray json) {
        List<StackWithChance> list = new ArrayList<>();
        for(int i = 0; i < json.size(); i++) {
            list.add(readStackWithChance(json.get(i).getAsJsonObject()));
        }
        return list;
    }


    public StackWithChance readStackWithChance(JsonObject json) {
        return new StackWithChance(readOutput(json), json.get("chance").getAsFloat());
    }
    public List<StackWithChance> readOutputs(FriendlyByteBuf buf) {
        List<StackWithChance> list = new ArrayList<>();
        int len = buf.readInt();
        for(int i = 0; i < len; i++) {
            list.add(readStackWithChance(buf));
        }
        return list;
    }
    public StackWithChance readStackWithChance(FriendlyByteBuf buf) {
        return new StackWithChance(buf.readItem(), buf.readInt());
    }
    public void writeOutputs(FriendlyByteBuf buf, List<StackWithChance> outputs) {
        buf.writeInt(outputs.size());
        for(int i = 0; i < outputs.size(); i++) {
            writeStackWithChance(buf, outputs.get(i));
        }
    }
    public void writeStackWithChance(FriendlyByteBuf buf, StackWithChance stack) {
        buf.writeItemStack(stack.stack().get(), true);
        buf.writeFloat(stack.chance());
    }
}
