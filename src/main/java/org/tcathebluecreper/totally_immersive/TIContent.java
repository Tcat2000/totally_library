package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.register.IEBlockEntities;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.Multiblock.ChemicalBath.*;
import org.tcathebluecreper.totally_immersive.Multiblock.grinder.*;
import org.tcathebluecreper.totally_immersive.block.markings.ColoredMarking;
import org.tcathebluecreper.totally_immersive.block.markings.Marking;
import org.tcathebluecreper.totally_immersive.block.markings.MarkingBlock;
import org.tcathebluecreper.totally_immersive.item.SprayCan;
import org.tcathebluecreper.totally_immersive.lib.ITMultiblockBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

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
            ChemicalBathRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("chemical_bath", ChemicalBathRecipeSerializer::new);
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
