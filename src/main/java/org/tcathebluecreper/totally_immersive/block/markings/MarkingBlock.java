package org.tcathebluecreper.totally_immersive.block.markings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_immersive.block.TIBlocks;

import java.util.*;

import static org.tcathebluecreper.totally_immersive.item.TIItems.SPRAY_CAN;

public class MarkingBlock extends Block {
    public static Property<Marking> MARKING_TOP;
    public static Property<Marking> MARKING_BOTTOM;
    public static Property<Marking> MARKING_NORTH;
    public static Property<Marking> MARKING_EAST;
    public static Property<Marking> MARKING_SOUTH;
    public static Property<Marking> MARKING_WEST;
    public static Map<@NotNull Direction, @NotNull Property<Marking>> MARKING_DIRECTIONS;
    public MarkingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        MARKING_TOP = side(Direction.UP);
        MARKING_BOTTOM = side(Direction.DOWN);
        MARKING_NORTH = side(Direction.NORTH);
        MARKING_EAST = side(Direction.EAST);
        MARKING_SOUTH = side(Direction.SOUTH);
        MARKING_WEST = side(Direction.WEST);
        MARKING_DIRECTIONS = Map.of(Direction.UP, MARKING_TOP, Direction.DOWN, MARKING_BOTTOM, Direction.NORTH, MARKING_NORTH, Direction.EAST, MARKING_EAST, Direction.SOUTH, MARKING_SOUTH, Direction.WEST, MARKING_WEST);
        builder.add(MARKING_BOTTOM, MARKING_TOP, MARKING_NORTH, MARKING_EAST, MARKING_SOUTH, MARKING_WEST);
    }

    public Property<Marking> side(Direction side) {
        return new Property<>("marking_" + side.getName(), Marking.class) {
            @Override
            public Collection<Marking> getPossibleValues() {
                return Marking.ALL_MARKINGS.stream().filter((marking) -> marking.allowOnSide(side)).toList();
            }

            @Override
            public String getName(Marking marking) {
                return marking.name();
            }

            @Override
            public Optional<Marking> getValue(String markingName) {
                return Marking.ALL_MARKINGS.stream().filter((marking -> marking.name().equals(markingName))).findFirst();
            }
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if(collisionContext.equals(CollisionContext.empty())) return Shapes.block();
        if(collisionContext.isHoldingItem(SPRAY_CAN.get())) {
            List<VoxelShape> shapes = new ArrayList<>();

            shapes.add(state.getValue(MARKING_BOTTOM).getShape());
            shapes.add(rotateShape(Direction.DOWN, Direction.WEST, state.getValue(MARKING_NORTH).getShape()));
            shapes.add(rotateShape(Direction.DOWN, Direction.EAST, state.getValue(MARKING_EAST).getShape()));
            shapes.add(rotateShape(Direction.DOWN, Direction.NORTH, state.getValue(MARKING_SOUTH).getShape()));
            shapes.add(rotateShape(Direction.DOWN, Direction.SOUTH, state.getValue(MARKING_WEST).getShape()));
            shapes.add(rotateShape(Direction.DOWN, Direction.UP, state.getValue(MARKING_TOP).getShape()));

            VoxelShape shape = shapes.get(0);
            shapes.remove(0);
            VoxelShape[] array = new VoxelShape[shapes.size()];
            for(int i = 0; i < shapes.size(); i++) {
                array[i] = shapes.get(i);
            }
            return Shapes.or(shape, array);
        }
        else if(collisionContext.isHoldingItem(Items.DEBUG_STICK)) {
            return Shapes.block();
        }
        return Shapes.box(0,0,0,0,0,0);
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.box(0,0,0,0,0,0)};

        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        int vert = 0;
        if(from == Direction.DOWN && to == Direction.UP) vert = 2;
        else if(from == Direction.UP && to == Direction.DOWN) vert = -2;
        else if(from == Direction.DOWN && to != Direction.DOWN && to != Direction.UP) vert = 1;
        else if(from == Direction.UP && to != Direction.DOWN && to != Direction.UP) vert = -1;
        if(vert == 1) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(minY, minX, minZ, maxY, maxX, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        if(vert == 2 || vert == -2) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(minX, 1-maxY, minZ, maxX, 1-minY, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        final BlockState[] block = {state};
        Direction.stream().forEach(dir -> {
            Property<Marking> prop = MARKING_DIRECTIONS.get(dir);
            Marking marking = state.getValue(prop);
            if(marking != TIBlocks.NONE) return;
            BlockState connectedBlock = level.getBlockState(pos.relative(dir));
            if(!marking.canBeSupported((Level) level, connectedBlock, pos.relative(dir), dir)) {
                block[0] = block[0].setValue(prop, TIBlocks.NONE);
            }
        });
        return InteractionResult.PASS;
    }
}
