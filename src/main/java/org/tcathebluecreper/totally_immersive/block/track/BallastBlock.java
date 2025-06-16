package org.tcathebluecreper.totally_immersive.block.track;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_immersive.block.TIBlocks;

import java.util.HashMap;
import java.util.Map;

public class BallastBlock extends Block {
    public static IntegerProperty NE_FILL = IntegerProperty.create("fill_ne", 0, 8);
    public static IntegerProperty ES_FILL = IntegerProperty.create("fill_es", 0, 8);
    public static IntegerProperty SW_FILL = IntegerProperty.create("fill_sw", 0, 8);
    public static IntegerProperty WN_FILL = IntegerProperty.create("fill_wn", 0, 8);
    public BallastBlock() {
        super(BlockBehaviour.Properties.of().noOcclusion());
//        ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutout());
    }

    public static BlockState combine(BlockState state1, BlockState state2) {
        if(state1 == null || state2 == null) return null;
        if(!(state1.getBlock() instanceof BallastBlock) || !(state2.getBlock() instanceof BallastBlock)) return null;
        state1 = state1.setValue(NE_FILL, Math.max(state1.getValue(NE_FILL), state2.getValue(NE_FILL)));
        state1 = state1.setValue(ES_FILL, Math.max(state1.getValue(ES_FILL), state2.getValue(ES_FILL)));
        state1 = state1.setValue(SW_FILL, Math.max(state1.getValue(SW_FILL), state2.getValue(SW_FILL)));
        state1 = state1.setValue(WN_FILL, Math.max(state1.getValue(WN_FILL), state2.getValue(WN_FILL)));
        return state1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def) {
        def.add(NE_FILL, ES_FILL, SW_FILL, WN_FILL);
    }

    @Override
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0,0,0,0.5,2/16f * state.getValue(SW_FILL), 0.5));
        shape = Shapes.or(shape, Shapes.box(0.5,0,0.5,1,2/16f * state.getValue(NE_FILL), 1));
        shape = Shapes.or(shape, Shapes.box(0.5,0,0,1,2/16f * state.getValue(WN_FILL), 0.5));
        shape = Shapes.or(shape, Shapes.box(0,0,0.5,0.5,2/16f * state.getValue(ES_FILL), 1));
        return shape;
    }

    public static Map<BlockPos, BlockState> addLayers(Level level, Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);
        BlockState state = level.getBlockState(blockPos);
        if(state.getBlock() instanceof AirBlock) {
//            level.setBlock(blockPos, TIBlocks.GRAVEL_BALLAST.get().defaultBlockState(), 3);
//            state = level.getBlockState(blockPos);
            state = TIBlocks.GRAVEL_BALLAST.get().defaultBlockState();
        }
        if(!(state.getBlock() instanceof BallastBlock)) return new HashMap<>();
        pos = pos.subtract(blockPos.getCenter());
        IntegerProperty prop = null;
        if(pos.x > 0 && pos.z > 0) prop = NE_FILL;
        else if(pos.x < 0 && pos.z > 0) prop = ES_FILL;
        else if(pos.x > 0 && pos.z < 0) prop = WN_FILL;
        else if(pos.x < 0 && pos.z < 0) prop = SW_FILL;
        if(prop == null) return new HashMap<>();

        pos = pos.add(0.5,0.5,0.5);
        BlockState above = level.getBlockState(blockPos.above());

        if(above.getBlock() instanceof BallastBlock && above.getValue(NE_FILL) != 0) state = state.setValue(NE_FILL, 8);
        if(above.getBlock() instanceof BallastBlock && above.getValue(ES_FILL) != 0) state = state.setValue(ES_FILL, 8);
        if(above.getBlock() instanceof BallastBlock && above.getValue(SW_FILL) != 0) state = state.setValue(SW_FILL, 8);
        if(above.getBlock() instanceof BallastBlock && above.getValue(WN_FILL) != 0) state = state.setValue(WN_FILL, 8);

        if(state.getValue(NE_FILL) == 8 && state.getValue(ES_FILL) == 8 && state.getValue(SW_FILL) == 8 && state.getValue(WN_FILL) == 8) {
//            level.setBlock(blockPos, Blocks.GRAVEL.defaultBlockState(), Block.UPDATE_ALL);
            return Map.of(blockPos, Blocks.GRAVEL.defaultBlockState());
        }

        Map<BlockPos, BlockState> map = new HashMap<>();

        if(state.getValue(NE_FILL) != 0 && state.getValue(ES_FILL) != 0 && state.getValue(SW_FILL) != 0 && state.getValue(WN_FILL) != 0) {
            BlockPos dPos = blockPos.below();
            while(level.getBlockState(dPos).getBlock() instanceof AirBlock && level.isInWorldBounds(dPos)) {
//                level.setBlock(blockPos.below(), Blocks.GRAVEL.defaultBlockState(), Block.UPDATE_ALL);
                map.put(blockPos.below(), Blocks.GRAVEL.defaultBlockState());
                dPos = dPos.below();
            }
        }

        if(above.getBlock() instanceof BallastBlock && above.getValue(prop) != 0) state = state.setValue(prop, 8);
        else if(pos.y > 14/16f) state.setValue(prop, 8);
        else if(pos.y > 12/16f) state = state.setValue(prop, 7);
        else if(pos.y > 10/16f) state = state.setValue(prop, 6);
        else if(pos.y > 8/16f) state = state.setValue(prop, 5);
        else if(pos.y > 6/16f) state = state.setValue(prop, 4);
        else if(pos.y > 4/16f) state = state.setValue(prop, 3);
        else if(pos.y > 2/16f) state = state.setValue(prop, 2);
        else if(pos.y > 0/16f) state = state.setValue(prop, 1);
        map.put(blockPos, state);
//        level.setBlock(blockPos, state, Block.UPDATE_ALL);
        return map;
    }
}
