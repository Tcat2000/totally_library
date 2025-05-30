package org.tcathebluecreper.totally_immersive.block.markings;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.Optional;

public class MarkingBlock extends Block {
    public static Property<Marking> MARKING_TOP;
    public static Property<Marking> MARKING_BOTTOM;
    public static Property<Marking> MARKING_NORTH;
    public static Property<Marking> MARKING_EAST;
    public static Property<Marking> MARKING_SOUTH;
    public static Property<Marking> MARKING_WEST;
    public MarkingBlock(Properties properties) {
        super(properties);
        MARKING_TOP = side("top");
        MARKING_BOTTOM = side("bottom");
        MARKING_NORTH = side("north");
        MARKING_EAST = side("east");
        MARKING_SOUTH = side("south");
        MARKING_WEST = side("west");
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
    }

    public Property<Marking> side(String side) {
        return new Property<>("marking_" + side, Marking.class) {
            @Override
            public Collection<Marking> getPossibleValues() {
                return Marking.ALL_MARKINGS;
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
}
