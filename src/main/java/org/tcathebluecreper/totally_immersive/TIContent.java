package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath.*;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.*;
import org.tcathebluecreper.totally_immersive.item.SprayCan;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TIContent {

    public static class TIItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

        public static final RegistryObject<SprayCan> SPRAY_CAN = ITEMS.register("spray_can", () -> new SprayCan(new Item.Properties()));
    }
    public static class TIBET {
        public static final DeferredRegister<BlockEntityType<?>> BETs = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    }

    public static class TIRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

        public static final IERecipeTypes.TypeWithClass<ChemicalBathRecipe> CHEMICAL_BATH = register("chemical_bath", ChemicalBathRecipe.class);
        public static final IERecipeTypes.TypeWithClass<GrinderRecipe> GRINDER = register("grinder", GrinderRecipe.class);

        static {
            ChemicalBathRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("chemical_bath", () -> new ChemicalBathRecipeSerializer(ChemicalBathRecipe::new));
            GrinderRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("grinder", GrinderRecipeSerializer::new);
        }

        public static <T extends Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> clazz){
            return new IERecipeTypes.TypeWithClass<>(register(name), clazz);
        }
        public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
            return RECIPE_TYPES.register(name, ()-> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID,name)));
        }
    }
    public static class TIRegistries {
        static {
        }
    }
}
