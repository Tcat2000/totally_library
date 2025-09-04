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
import org.tcathebluecreper.totally_lib.multiblock.RecipeTraitMultiblockState;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TLRecipeBuilder {
    private final ResourceLocation id;
    private final RegistrationManager manager;
    private final Consumer<TLBuiltRecipeInfo> consumer;
    public Function<ProviderList<TLRecipeSerializer.Provider<?>>, Integer> getLength = (list) -> 20;
    public BiFunction<TLRecipe, IMultiblockState, Boolean> checkCanExecute;
    public TriFunction<TLRecipe, IMultiblockState, Integer, Boolean> checkCanResume;
    public ProviderList<TLRecipeSerializer.Provider<?>> recipeProviders = new ProviderList<>();
    public List<TLRecipeProcess.Action<TLRecipe, TraitMultiblockState>> process = new ArrayList<>();
    public BiFunction<TLRecipeProcess<TLRecipe, TraitMultiblockState>, Integer, Boolean> tickLogic;

    public static final RegistryObject<RecipeType<?>> TLRecipeRegistry = TotallyLibrary.regManager.register(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), TotallyLibrary.MODID, "recipe", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID,"recipe")));

    public TLRecipeBuilder(ResourceLocation id, RegistrationManager manager, Consumer<TLBuiltRecipeInfo> consumer) {
        this.id = id;
        this.manager = manager;
        this.consumer = consumer;
    }
    @RemapForJS("lengthInt")
    public TLRecipeBuilder length(int length) {
        getLength = (providers) -> length;
        return this;
    }
    public TLRecipeBuilder length(Function<ProviderList<TLRecipeSerializer.Provider<?>>, Integer> length) {
        getLength = length;
        return this;
    }
    public TLRecipeBuilder executeCondition(BiFunction<TLRecipe, IMultiblockState, Boolean> condition) {
        this.checkCanExecute = condition;
        return this;
    }
    public TLRecipeBuilder resumeCondition(TriFunction<TLRecipe, IMultiblockState, Integer, Boolean> condition) {
        this.checkCanResume = condition;
        return this;
    }
    public TLRecipeBuilder addProvider(TLRecipeSerializer.Provider<?> provider) {
        recipeProviders.add(provider);
        return this;
    }
    public TLRecipeBuilder tickLogic(BiFunction<TLRecipeProcess<TLRecipe, TraitMultiblockState>, Integer, Boolean> tickLogic) {
        this.tickLogic = tickLogic;
        return this;
    }
    public TLRecipeBuilder processTick(int tick, BiConsumer<TLRecipeProcess<TLRecipe, TraitMultiblockState>, Integer> logic) {
        this.process.add(new TLRecipeProcess.TickAction<>(tick, logic));
        return this;
    }

    public TLBuiltRecipeInfo build() {
        AtomicReference<Supplier<TLRegistrableRecipeSerializer>> getSerializer = new AtomicReference<>();

        BiFunction<ResourceLocation, ProviderList<TLRecipeSerializer.Provider<?>>, TLRegistrableRecipe> recipe = (id, providers) -> new TLRegistrableRecipe(id, providers, getSerializer.get().get(), TLRecipeRegistry, getLength.apply(providers), checkCanExecute, checkCanResume, getSerializer.get().get());

        AtomicReference<Supplier<RegistryObject<RecipeSerializer<?>>>> ro = new AtomicReference<>();
        RegistryObject<RecipeSerializer<?>> reg = manager.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), id.getNamespace(), id.getPath(), () -> new TLRegistrableRecipeSerializer(recipe, TLRecipe.class, recipeProviders, new IERecipeTypes.TypeWithClass<>((RegistryObject<RecipeType<TLRegistrableRecipe>>) (Object) TLRecipeRegistry, TLRegistrableRecipe.class)));
        ro.set(() -> reg);

        getSerializer.set(() -> (TLRegistrableRecipeSerializer) reg.get());

        Function<TraitMultiblockState, TLRecipeProcess<TLRecipe, TraitMultiblockState>> createProcess = state -> new TLRecipeProcess<>(TLRecipe.class, process, state, tickLogic, 0);

        TLBuiltRecipeInfo info = new TLBuiltRecipeInfo(() -> (TLRegistrableRecipeSerializer) getSerializer.get(), recipe, recipeProviders, createProcess);
        consumer.accept(info);
        return info;
    }
}
