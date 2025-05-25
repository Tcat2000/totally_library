package org.tcathebluecreper.totally_immersive;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockItem;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder;
import blusunrize.immersiveengineering.common.blocks.multiblocks.logic.NonMirrorableWithActiveBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.Multiblock.*;
import org.tcathebluecreper.totally_immersive.lib.ITMultiblockBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TIContent {
    public static class TIBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    }
    public static class TIItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    }
    public static class TIBET {
        public static final DeferredRegister<BlockEntityType<?>> BETs = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    }

    public static class TIMultiblocks {
        public static final List<MultiblockRegistration<?>> MULTIBLOCKS = new ArrayList<>();
        public static final MultiblockRegistration<ChemicalBathState> CHEMICAL_BATH = add(metal(new ChemicalBathLogic(),"chemical_bath")
                .structure(Multiblock.CHEMICAL_BATH)
                .build());



        public static <T extends IMultiblockState> MultiblockRegistration<T> add(MultiblockRegistration<T> res) {
            MULTIBLOCKS.add(res);
            return res;
        }
        private static <S extends IMultiblockState> IEMultiblockBuilder<S> stone(IMultiblockLogic<S> logic, String name, boolean solid) {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(2, 20).forceSolidOn();
            if (!solid)
                properties.noOcclusion();
            return new IEMultiblockBuilder<>(logic, name)
                    .notMirrored()
                    .customBlock(
                            TIBlocks.BLOCKS, TIItems.ITEMS,
                            r -> new NonMirrorableWithActiveBlock<>(properties, r),
                            MultiblockItem::new)
                    .defaultBEs(TIBET.BETs);
        }

        private static <S extends IMultiblockState> IEMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name) {
            return new IEMultiblockBuilder<>(logic, name)
                    .defaultBEs(TIBET.BETs)
                    .notMirrored()
                    .customBlock(
                            TIBlocks.BLOCKS, TIItems.ITEMS,
                            r -> new ITMultiblockBlock<>(IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get().forceSolidOn(), r),
                            MultiblockItem::new);
        }

        public static void init() {
            Multiblock.init();
        }
        public static class Multiblock{
            private static final List<Lazy<? extends MultiblockHandler.IMultiblock>> toRegister = new ArrayList<>();


            public static final Lazy<TemplateMultiblock> CHEMICAL_BATH = registerLazily(ChemicalBathMultiblock::new);


            public static void init() {
                toRegister.forEach(r->MultiblockHandler.registerMultiblock(r.get()));
            }
            public static <T extends MultiblockHandler.IMultiblock> Lazy<T> registerLazily(Supplier<T> mb) {
                Lazy<T> r = Lazy.of(mb);
                toRegister.add(r);
                return r;
            }
        }
    }
    public static class TIRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

        public static final IERecipeTypes.TypeWithClass<ChemicalBathRecipe> CHEMICAL_BATH = register("chemical_bath", ChemicalBathRecipe.class);

        static {
            ChemicalBathRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("chemical_bath", ChemicalBathRecipeSerializer::new);
        }

        public static <T extends Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> clazz){
            return new IERecipeTypes.TypeWithClass<>(register(name), clazz);
        }
        public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
            return RECIPE_TYPES.register(name, ()-> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID,name)));
        }
    }
}
