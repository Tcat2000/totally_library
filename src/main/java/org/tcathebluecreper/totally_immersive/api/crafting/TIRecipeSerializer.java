package org.tcathebluecreper.totally_immersive.api.crafting;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TIRecipeSerializer<R extends TIRecipe> implements RecipeSerializer<R> {
    protected static final Map<Class<? extends TIRecipe>, BiFunction<IMultiblockState, Level, ? extends TIRecipe>> SERIALIZERS = new HashMap<>();
    private final ProviderList<Provider<?>> providers = getProviders();
    private final BiFunction<ResourceLocation, ProviderList<Provider<?>>, R> constructor;
    public ProviderList<Provider<?>> getProviders() {
        return new ProviderList<>();
    }

    public TIRecipeSerializer(BiFunction<ResourceLocation, ProviderList<Provider<?>>, R> constructor, Class<? extends TIRecipe> type) {
        this.constructor = constructor;
        SERIALIZERS.put(type, this::findRecipe);
    }

    @Override
    public final @NotNull R fromJson(@NotNull ResourceLocation recipeID, @NotNull JsonObject jsonObject) {
        ProviderList<Provider<?>> list = new ProviderList<>();
        providers.forEach(provider -> {
            list.add(provider.fromJson(recipeID, jsonObject));
        });
        return constructor.apply(recipeID, list);
    }

    @Override
    public final @Nullable R fromNetwork(@NotNull ResourceLocation recipeID, @NotNull FriendlyByteBuf friendlyByteBuf) {
        ProviderList<Provider<?>> list = new ProviderList<>();
        providers.forEach(provider -> {
            list.add(provider.fromNetwork(recipeID, friendlyByteBuf));
        });
        return constructor.apply(recipeID, list);
    }

    @Override
    public final void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, @NotNull R r) {
        providers.forEach(provider -> {
            provider.toNetwork(friendlyByteBuf);
        });
    }

    public abstract R findRecipe(IMultiblockState state, Level level);

    public abstract static class Provider<T> {
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
    public static class IntProvider extends Provider<Integer> {
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
    public static class BooleanProvider extends Provider<Boolean> {
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
    public static class FloatProvider extends Provider<Float> {
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
    public static class ItemProvider extends Provider<Item> {
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
    public static class ItemStackProvider extends Provider<ItemStack> {
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
            if(item == null) throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(data.get("item").getAsString()) + "'");
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
    public static class IngredientProvider extends Provider<Ingredient> {
        protected IngredientProvider(String field, Ingredient value) {
            super(field, value);
        }
        public IngredientProvider(String field) {
            super(field, null);
        }
        @Override
        public Provider<Ingredient> fromJson(ResourceLocation recipeID, JsonObject json) {
            JsonObject data = json.getAsJsonObject(field);
            if(data == null) throw new RecipeSerializationException(recipeID, "Missing '" + field + "'");
            int count = data.has("count") ? data.get("count").getAsInt() : 1;
            if(data.has("item")) {
                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(data.get("item").getAsString()));
                if(item == null) throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(data.get("item").getAsString()) + "'");
                return new IngredientProvider(field, Ingredient.of(new ItemStack(item, count)));
            }
            if(data.has("items")) {
                JsonArray itemObj = data.getAsJsonArray("items");
                return new IngredientProvider(field, Ingredient.of(itemObj.asList().stream().map(j -> {
                    Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(j.getAsString()));
                    if(item == null) throw new RecipeSerializationException(recipeID, "Cannot find item '" + ResourceLocation.parse(j.getAsString()) + "'");
                    return new ItemStack(item, count);
                })));
            }
            if(data.has("tag")) {
                return new IngredientProvider(field, Ingredient.fromValues(Stream.of(new Ingredient.TagValue(TagKey.create(Registries.ITEM, ResourceLocation.parse(data.get("tag").getAsString()))) {
                    @Override
                    public @NotNull Collection<ItemStack> getItems() {
                        return super.getItems().stream().peek(stack -> stack.setCount(count)).collect(Collectors.toSet());
                    }
                })));
            }
            if(data.has("tags")) {
                JsonArray tagObj = data.getAsJsonArray("tags");
                Ingredient.fromValues(tagObj.asList().stream().map(j -> new Ingredient.TagValue(TagKey.create(Registries.ITEM, ResourceLocation.parse(tagObj.getAsString()))) {
                    @Override
                    public @NotNull Collection<ItemStack> getItems() {
                        return super.getItems().stream().peek(stack -> stack.setCount(count)).collect(Collectors.toSet());
                    }
                }));
            }
            else throw new RecipeSerializationException(recipeID, "Ingredient '" + field + "' missing value, needs 'item', 'items', 'tag', or 'tags'");
            return null;
        }
        @Override
        public void toNetwork(FriendlyByteBuf buf) {
            assert value != null;
            value.toNetwork(buf);
        }
        @Override
        public Provider<?> fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
            return new IngredientProvider(field, Ingredient.fromNetwork(buf));
        }

        public boolean canExtractFrom(IItemHandler handler, int slot) {
            return canExtractFrom(handler.getStackInSlot(slot));
        }
        public boolean canExtractFrom(ItemStack stack) {
            assert value != null;
            for(ItemStack itemstack : value.getItems()) {
                if (itemstack.is(stack.getItem()) && itemstack.getCount() <= stack.getCount()) {
                    return true;
                }
            }
            return false;
        }
        public boolean canExtractFromAny(IItemHandler handler) {
            return canExtractFromAny(handler, 0, handler.getSlots());
        }
        public boolean canExtractFromAny(IItemHandler handler, int min, int max) {
            for(int i = min; i < max; i++) {
                if(canExtractFrom(handler, i)) return true;
            }
            return false;
        }
        public ItemStack extractFrom(IItemHandler handler, int slot) {
            return extractFrom(handler.getStackInSlot(slot));
        }
        public ItemStack extractFrom(ItemStack stack) {
            assert value != null;
            for(ItemStack itemstack : value.getItems()) {
                if (itemstack.is(stack.getItem()) && itemstack.getCount() <= stack.getCount()) {
                    stack.setCount(stack.getCount() - itemstack.getCount());
                    return itemstack.copy();
                }
            }
            return ItemStack.EMPTY;
        }
        public ItemStack extractFromAny(IItemHandler handler) {
            return extractFromAny(handler, 0, handler.getSlots());
        }
        public ItemStack extractFromAny(IItemHandler handler, int min, int max) {
            assert value != null;
            for(int i = min; i < max; i++) {
                if(canExtractFrom(handler, i)) return extractFrom(handler, i);
            }
            return ItemStack.EMPTY;
        }
    }
    public static class FluidProvider extends Provider<Fluid> {
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
    public static class FluidStackProvider extends Provider<FluidStack> {
        protected FluidStackProvider(String field, FluidStack value) {
            super(field, value);
        }
        public FluidStackProvider(String field) {
            super(field, null);
        }

        @Override
        public Provider<FluidStack> fromJson(ResourceLocation recipeID, JsonObject json) {
            JsonObject data = json.getAsJsonObject(field);
            if(!data.has("fluid")) throw new RecipeSerializationException(recipeID, "FluidStack field '" + field + "' missing field for 'fluid'");
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(data.get("fluid").getAsString()));
            if(fluid == null) throw new RecipeSerializationException(recipeID, "Cannot find fluid '" + ResourceLocation.parse(data.get("fluid").getAsString()) + "'");
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
}
