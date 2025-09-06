package org.tcathebluecreper.totally_lib.recipe;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.function.TriFunction;
import org.tcathebluecreper.totally_lib.RegistrationManager;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.crafting.ProviderList;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;
import org.tcathebluecreper.totally_lib.recipe.action.Action;
import org.tcathebluecreper.totally_lib.recipe.action.TickAction;
import org.tcathebluecreper.totally_lib.recipe.provider.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

import static org.tcathebluecreper.totally_lib.TotallyLibrary.MODID;

public class RecipeBuilder {
    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<TLBuiltRecipeInfo> consumer;
    public Function<ProviderList<Provider<?>>, Integer> getLength = (list) -> 20;
    public BiFunction<ModularRecipe, IMultiblockState, Boolean> checkCanExecute;
    public TriFunction<ModularRecipe, IMultiblockState, Integer, Boolean> checkCanResume;
    public ProviderList<Provider<?>> recipeProviders = new ProviderList<>();
    public List<Action<ModularRecipe, TraitMultiblockState>> process = new ArrayList<>();
    public BiFunction<TLRecipeProcess<ModularRecipe, TraitMultiblockState>, Integer, Boolean> tickLogic;

    public static final RegistryObject<RecipeType<?>> TLRegistrableRecipeRegistry = TotallyLibrary.regManager.register(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), TotallyLibrary.MODID, "recipe", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID,"recipe")));

    public RecipeBuilder(ResourceLocation id, RegistrationManager manager, Consumer<TLBuiltRecipeInfo> consumer) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
    }
    @RemapForJS("lengthInt")
    public RecipeBuilder length(int length) {
        getLength = (providers) -> length;
        return this;
    }
    public RecipeBuilder length(Function<ProviderList<Provider<?>>, Integer> length) {
        getLength = length;
        return this;
    }
    public RecipeBuilder executeCondition(BiFunction<ModularRecipe, IMultiblockState, Boolean> condition) {
        this.checkCanExecute = condition;
        return this;
    }
    public RecipeBuilder resumeCondition(TriFunction<ModularRecipe, IMultiblockState, Integer, Boolean> condition) {
        this.checkCanResume = condition;
        return this;
    }
    public RecipeBuilder addProvider(Provider<?> provider) {
        recipeProviders.add(provider);
        return this;
    }
    public RecipeBuilder tickLogic(BiFunction<TLRecipeProcess<ModularRecipe, TraitMultiblockState>, Integer, Boolean> tickLogic) {
        this.tickLogic = tickLogic;
        return this;
    }
    public RecipeBuilder processTick(int tick, BiFunction<TLRecipeProcess<ModularRecipe, TraitMultiblockState>, Integer, Boolean> logic) {
        this.process.add(new TickAction<>(tick, logic));
        return this;
    }

    public TLBuiltRecipeInfo build() {
        AtomicReference<Supplier<ModularRecipeSerializer>> getSerializer = new AtomicReference<>();

        BiFunction<ResourceLocation, ProviderList<Provider<?>>, ModularRecipe> recipe = (id, providers) -> new ModularRecipe(id, providers, getSerializer.get().get(), TLRegistrableRecipeRegistry, getLength.apply(providers), checkCanExecute, checkCanResume, getSerializer.get().get());

        AtomicReference<Supplier<RegistryObject<RecipeSerializer<?>>>> ro = new AtomicReference<>();
        RegistryObject<RecipeSerializer<?>> reg = manager.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), id.getNamespace(), id.getPath(), () -> new ModularRecipeSerializer(recipe, ModularRecipe.class, recipeProviders, new IERecipeTypes.TypeWithClass<>((RegistryObject<RecipeType<ModularRecipe>>) (Object) TLRegistrableRecipeRegistry, ModularRecipe.class)));
        ro.set(() -> reg);

        getSerializer.set(() -> (ModularRecipeSerializer) reg.get());

        Function<TraitMultiblockState, CraftingRecipeProcess<ModularRecipe, TraitMultiblockState>> createProcess = state -> new CraftingRecipeProcess<>(ModularRecipe.class, process, state, tickLogic, 0, getSerializer.get().get());

        TLBuiltRecipeInfo info = new TLBuiltRecipeInfo(() -> (ModularRecipeSerializer) getSerializer.get(), recipe, recipeProviders, createProcess);
        consumer.accept(info);
        return info;
    }

    public static class TLBuiltRecipeInfo {
        public final Supplier<ModularRecipeSerializer> getSerializer;
        public final BiFunction<ResourceLocation, ProviderList<Provider<?>>, ModularRecipe> recipeConstructor;
        public final ProviderList<Provider<?>> recipeProviders;
        public final Function<TraitMultiblockState, CraftingRecipeProcess<ModularRecipe, TraitMultiblockState>> createProcess;

        public TLBuiltRecipeInfo(Supplier<ModularRecipeSerializer> getSerializer, BiFunction<ResourceLocation, ProviderList<Provider<?>>, ModularRecipe> recipeConstructor, ProviderList<Provider<?>> recipeProviders, Function<TraitMultiblockState, CraftingRecipeProcess<ModularRecipe, TraitMultiblockState>> createProcess) {
            this.getSerializer = getSerializer;
            this.recipeConstructor = recipeConstructor;
            this.recipeProviders = recipeProviders;
            this.createProcess = createProcess;
        }
    }
}
