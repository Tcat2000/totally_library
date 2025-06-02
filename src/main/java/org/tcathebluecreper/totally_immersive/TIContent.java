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
import java.util.function.Function;
import java.util.function.Supplier;

import static org.tcathebluecreper.totally_immersive.TotallyImmersive.MODID;

public class TIContent {
    public static class TIBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
        public static final Marking NONE = new Marking() {
            @Override
            public String name() {
                return "none";
            }

            @Override
            public VoxelShape getShape() {
                return Shapes.empty();
            }
        };
//        public static final Marking STRIPES_YELLOW = new Marking() {
//            @Override
//            public String name() {
//                return "stripes_yellow";
//            }
//        };
        public static final Marking DOUBLE_LINE_YELLOW = new Marking() {
            @Override
            public String name() {
                return "double_line_yellow";
            }
        };

        public static final ColoredMarking STRIPES = new ColoredMarking("stripes_[color]", ResourceLocation.fromNamespaceAndPath("totally_immersive", "block/marking/stripes_[color]"));


        public static final RegistryObject<Block> MARKINGS_BLOCK = register("markings", () -> new MarkingBlock(BlockBehaviour.Properties.of().noCollission().instabreak().replaceable()));
        public static final RegistryObject<Block> REFINED_CONCRETE = register("refined_concrete", () -> new Block(BlockBehaviour.Properties.of()));

        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, String itemName, Function<T, Item> item) {
            RegistryObject<T> blk = BLOCKS.register(name, block);
            TIItems.ITEMS.register(itemName, () -> item.apply(blk.get()));
            return blk;
        }
        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
            return register(name, block, name, (b) -> new BlockItem(b, new Item.Properties()));
        }
        protected static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Function<T, Item> item) {
            return register(name, block, name, item);
        }
    }
    public static class TIItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

        public static final RegistryObject<SprayCan> SPRAY_CAN = ITEMS.register("spray_can", () -> new SprayCan(new Item.Properties()));
    }
    public static class TIBET {
        public static final DeferredRegister<BlockEntityType<?>> BETs = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    }

    public static class TIMultiblocks {
        public static final List<MultiblockRegistration<?>> MULTIBLOCKS = new ArrayList<>();
        public static final MultiblockRegistration<ChemicalBathState> CHEMICAL_BATH = add(metal(new ChemicalBathLogic(),"chemical_bath")
                .structure(Multiblock.CHEMICAL_BATH)
                .build());

//        public static final MultiblockRegistration<GrinderState> GRINDER = add(metal(new GrinderLogic(),"grinder")
//                .structure(Multiblock.GRINDER)
//                .build());


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
//            public static final Lazy<TemplateMultiblock> GRINDER = registerLazily(GrinderMultiblock::new);


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
        public static final IERecipeTypes.TypeWithClass<GrinderRecipe> GRINDER = register("grinder", GrinderRecipe.class);

        static {
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
