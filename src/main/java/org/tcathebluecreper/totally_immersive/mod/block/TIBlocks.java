package org.tcathebluecreper.totally_immersive.mod.block;

import blusunrize.immersiveengineering.common.blocks.generic.ConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.BasicConnectorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.EnergyConnectorBlockEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.tcathebluecreper.totally_immersive.mod.block.markings.Marking;
import org.tcathebluecreper.totally_immersive.mod.block.markings.MarkingBlock;
import org.tcathebluecreper.totally_immersive.mod.block.track.*;
import org.tcathebluecreper.totally_immersive.mod.item.TIItems;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.tcathebluecreper.totally_immersive.mod.TotallyImmersive.MODID;

public class TIBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BETs = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);


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


    //        public static final Marking DOUBLE_LINE_YELLOW = new Marking() {
//            @Override
//            public String name() {
//                return "double_line_yellow";
//            }
//        };
//        public static final Marking DOUBLE_LINE_ROTATED_YELLOW = new Marking() {
//            @Override
//            public String name() {
//                return "double_line_rotated_yellow";
//            }
//        };
    public static final Marking DOUBLE_LINE_CORNER_YELLOW_0 = new Marking() {
        @Override
        public String name() {
            return "double_line_corner_yellow0";
        }
    };
//        public static final Marking DOUBLE_LINE_CORNER_YELLOW_1 = new Marking() {
//            @Override
//            public String name() {
//                return "double_line_corner_yellow1";
//            }
//        };
//        public static final Marking DOUBLE_LINE_CORNER_YELLOW_2 = new Marking() {
//            @Override
//            public String name() {
//                return "double_line_corner_yellow2";
//            }
//        };
//        public static final Marking DOUBLE_LINE_CORNER_YELLOW_3 = new Marking() {
//            @Override
//            public String name() {
//                return "double_line_corner_yellow3";
//            }
//        };
//
//        public static final ColoredMarking STRIPES = new ColoredMarking("stripes_[color]", "totally_immersive", "block/marking/stripes_[color]");


    public static final RegistryObject<Block> MARKINGS_BLOCK = register("markings", () -> new MarkingBlock(BlockBehaviour.Properties.of().noCollission().instabreak().replaceable()));
    public static final RegistryObject<Block> REFINED_CONCRETE = register("refined_concrete", () -> new Block(BlockBehaviour.Properties.of()));

    public static final RegistryObject<Block> INDUSTRIAL_ENGINEERING = register("industrial_engineering", () -> new Block(BlockBehaviour.Properties.of()));

    public static final RegistryObject<Block> UHV_CONNECTOR = register("uhv_connector", () -> new BasicConnectorBlock<>(ConnectorBlock.PROPERTIES.get(), EnergyConnectorBlockEntity.SPEC_TO_TYPE.get(Pair.of("UHV", false))));
    public static final RegistryObject<Block> UHV_RELAY = register("uhv_relay", () -> new BasicConnectorBlock<>(ConnectorBlock.PROPERTIES.get(), EnergyConnectorBlockEntity.SPEC_TO_TYPE.get(Pair.of("UHV", true))));

    public static final RegistryObject<BallastBlock> GRAVEL_BALLAST = register("ballast_gravel", BallastBlock::new);

    public static final RegistryObject<TrackBlock> TRACK_BLOCK = register("track", TrackBlock::new);
    public static final RegistryObject<BlockEntityType<TrackBlockEntity>> TRACK_BLOCK_ENTITY = BETs.register("track", () -> new BlockEntityType<>(TrackBlockEntity::new, Set.of(TRACK_BLOCK.get()), null));

    public static final RegistryObject<BridgeBlock> BRIDGE_BLOCK = register("bridge", BridgeBlock::new);
    public static final RegistryObject<BlockEntityType<BridgeBlockEntity>> BRIDGE_BLOCK_ENTITY = BETs.register("bridge", () -> new BlockEntityType<>(BridgeBlockEntity::new, Set.of(BRIDGE_BLOCK.get()), null));

    public static final RegistryObject<BridgeSlaveBlock> BRIDGE_SLAVE_BLOCK = register("bridge_slave", BridgeSlaveBlock::new);
    public static final RegistryObject<BlockEntityType<BridgeSlaveBlockEntity>> BRIDGE_SLAVE_BLOCK_ENTITY = BETs.register("bridge_slave", () -> new BlockEntityType<>(BridgeSlaveBlockEntity::new, Set.of(BRIDGE_SLAVE_BLOCK.get()), null));

    public static final RegistryObject<ScreenBlock> SCREEN_BLOCK = register("screen_block", ScreenBlock::new);
    public static final RegistryObject<BlockEntityType<ScreenBlockEntity>> SCREEN_BLOCK_ENTITY = BETs.register("screen_block", () -> new BlockEntityType<>(ScreenBlockEntity::new, Set.of(SCREEN_BLOCK.get()), null));

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