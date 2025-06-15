package org.tcathebluecreper.totally_immersive.block.track;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.TIBlocks;

import java.util.function.Function;

public class TrackBlock extends Block implements EntityBlock {
    public TrackBlock() {
        super(Properties.of());
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> p_152459_) {
        return ImmutableMap.of(null, Shapes.box(0f,0f,0f,1f,0.5f,1f));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return TIBlocks.TRACK_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        level.getBlockEntity(pos).setChanged();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, pos2, bet) -> {
            TrackBlockEntity be = (TrackBlockEntity) level.getBlockEntity(pos);
            assert be != null;
            if(be.localVector == null || be.targetVector == null || be.target == null) return;
            level.addParticle(ParticleTypes.FLAME,
                pos.getX() + be.localVector.x + 0.5,
                pos.getY() + be.localVector.y + 0.5,
                pos.getZ() + be.localVector.z + 0.5,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
            );
            level.addParticle(ParticleTypes.FLAME,
                be.target.getX() + be.targetVector.x + 0.5,
                be.target.getY() + be.targetVector.y + 0.5,
                be.target.getZ() + be.targetVector.z + 0.5,
                be.target.getX() + 0.5,
                be.target.getY() + 0.5,
                be.target.getZ() + 0.5
            );
        };
    }
}
