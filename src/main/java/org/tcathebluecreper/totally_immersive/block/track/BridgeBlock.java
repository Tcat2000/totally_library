package org.tcathebluecreper.totally_immersive.block.track;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.api.RenderablePart;
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.lib.AnimationUtils;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

import static org.tcathebluecreper.totally_immersive.block.track.BridgeBlockEntityRenderer.invertBlockPos;

public class BridgeBlock extends Block implements EntityBlock {
    public BridgeBlock() {
        super(Properties.of());
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> p_152459_) {
        return ImmutableMap.of(null, Shapes.box(0f,0f,0f,1f,0.5f,1f));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return TIBlocks.BRIDGE_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
//        level.getBlockEntity(pos).setChanged();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, pos2, bet) -> {
            BridgeBlockEntity be = (BridgeBlockEntity) level.getBlockEntity(pos);
            if(be == null || be.localVector == null || be.targetVector == null || be.targetPos == null) return;

            if(!be.needUpdate) return;
            be.needUpdate = false;

            be.renderBlocks = new HashMap<>();


            BlockPos startPos = new BlockPos(0,0,0);
            BlockPos endPos = be.targetPos.offset(invertBlockPos(be.getBlockPos()));
            Vec3 startDirection = be.localVector;
            Vec3 endDirection = be.targetVector;

            if(startDirection != null && endDirection != null) {
                Vec3 lastPos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
                Vec3 lastPillarPos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
                float pillarDistance = 0.5f;
                float totalDistance = pillarDistance;
                float inc = 0.01f;

                for(float i = 0; i < 1.05; i += inc) {
                    Vec3 current = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
                    float currentDist = (float) TIMath.vectorDist(lastPos, current);

                    if(totalDistance >= pillarDistance) {
                        Vec3 next = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
                        Vec3 real = TIMath.lerp3D(lastPillarPos, next, AnimationUtils.amount(pillarDistance, (float) lastPillarPos.distanceTo(next)));

                        if(real.equals(new Vec3(Float.NaN, Float.NaN, Float.NaN))) real = new Vec3(0, 0, 0);

                        totalDistance -= pillarDistance;
                        Vec3 vec = real.subtract(lastPillarPos).normalize().multiply(pillarDistance, pillarDistance, pillarDistance);

                        Vec3 sideVec1 = vec.yRot(90 * Mth.DEG_TO_RAD);
                        sideVec1 = sideVec1.add(0, -0.33, 0).normalize();

                        Vec3 sideVec2 = vec.yRot(-90 * Mth.DEG_TO_RAD);
                        sideVec2 = sideVec2.add(0, -0.33, 0).normalize();

                        level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + lastPillarPos.x, pos.getY() + lastPillarPos.y - 0.499, pos.getZ() + lastPillarPos.z + 1.05, 0, 0, 0);

                        be.renderBeams.add(new RenderablePart(lastPillarPos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(1,0,0)), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));

                        lastPillarPos = real;
                    }
                    totalDistance += currentDist;
                    lastPos = current;
                }
            }
        };
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof BridgeBlockEntity) ((BridgeBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).needUpdate = true;
        return InteractionResult.PASS;
    }
}
