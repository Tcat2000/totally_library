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
import net.minecraft.world.level.block.AirBlock;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.tcathebluecreper.totally_immersive.block.track.TrackBlockEntityRenderer.invertBlockPos;

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
//        level.getBlockEntity(pos).setChanged();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, pos2, bet) -> {
            TrackBlockEntity be = (TrackBlockEntity) level.getBlockEntity(pos);
            if(be == null || be.localVector == null || be.targetVector == null || be.targetPos == null) return;

            if(!be.needUpdate) return;
            be.needUpdate = false;

            be.renderBlocks = new HashMap<>();
            be.renderTies = new ArrayList<>();
            be.renderRails = new ArrayList<>();


            BlockPos startPos = new BlockPos(0,0,0);
            BlockPos endPos = be.targetPos.offset(invertBlockPos(be.getBlockPos()));
            Vec3 startDirection = be.localVector;
            Vec3 endDirection = be.targetVector;

            if(startDirection != null && endDirection != null) {
                Vec3 lastPos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
                Vec3 lastTiePos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
                float tieDistance = 0.5f;
                float totalDistance = tieDistance;
                float inc = 0.01f;

                for(float i = 0; i < 1.05; i += inc) {
                    Vec3 current = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
                    float currentDist = (float) TIMath.vectorDist(lastPos, current);

                    if(totalDistance >= tieDistance) {
                        Vec3 next = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
                        Vec3 real = TIMath.lerp3D(lastTiePos, next, AnimationUtils.amount(tieDistance, (float) lastTiePos.distanceTo(next)));

                        if(real.equals(new Vec3(Float.NaN, Float.NaN, Float.NaN))) real = new Vec3(0, 0, 0);

                        totalDistance -= tieDistance;
                        Vec3 vec = real.subtract(lastTiePos).normalize().multiply(tieDistance, tieDistance, tieDistance);

                        Vec3 sideVec1 = vec.yRot(90 * Mth.DEG_TO_RAD);
                        sideVec1 = sideVec1.add(0, -0.33, 0).normalize();

                        Vec3 sideVec2 = vec.yRot(-90 * Mth.DEG_TO_RAD);
                        sideVec2 = sideVec2.add(0, -0.33, 0).normalize();

                        level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + lastTiePos.x, pos.getY() + lastTiePos.y - 0.499, pos.getZ() + lastTiePos.z + 1.05, 0, 0, 0);

                        if(!be.constructed) {
                            iterateVector(sideVec1, level, pos.getCenter().add(lastTiePos).add(sideVec1.x, -0.499, sideVec1.z)).forEach((POS, STATE) -> {
                                if(be.renderBlocks.containsKey(POS)) {
                                    be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                                } else be.renderBlocks.put(POS, STATE);
                            });
                            iterateVector(sideVec1, level, pos.getCenter().add(lastTiePos).add(sideVec1.x, -0.499, sideVec1.z)).forEach((POS, STATE) -> {
                                if(be.renderBlocks.containsKey(POS)) {
                                    be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                                } else be.renderBlocks.put(POS, STATE);
                            });

                            iterateVector(sideVec2, level, pos.getCenter().add(lastTiePos).add(sideVec2.x, -0.499, sideVec2.z)).forEach((POS, STATE) -> {
                                if(be.renderBlocks.containsKey(POS)) {
                                    be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                                } else be.renderBlocks.put(POS, STATE);
                            });
                            iterateVector(sideVec2, level, pos.getCenter().add(lastTiePos).add(sideVec2.x, -0.499, sideVec2.z)).forEach((POS, STATE) -> {
                                if(be.renderBlocks.containsKey(POS)) {
                                    be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                                } else be.renderBlocks.put(POS, STATE);
                            });
                        }


                        be.renderTies.add(new RenderablePart(lastTiePos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));
                        be.renderRails.add(new RenderablePart(lastTiePos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(1, 3 / 16f, 0), new Vec3(1, 1, 10 * tieDistance)));
                        be.renderRails.add(new RenderablePart(lastTiePos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(-1, 3 / 16f, 0), new Vec3(1, 1, 10 * tieDistance)));

                        lastTiePos = real;
                    }
                    totalDistance += currentDist;
                    lastPos = current;
                }
            }
        };
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof TrackBlockEntity) ((TrackBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).needUpdate = true;
        return InteractionResult.PASS;
    }

    public static Map<BlockPos, BlockState> iterateVector(Vec3 dir, Level level, Vec3 pos) {
        dir = dir.normalize();
        Vec3 inc = dir.multiply(1/16f, 1/16f, 1/16f);
        Vec3 acc = new Vec3(0,0,0);
        Map<BlockPos, BlockState> map = new HashMap<>();
        for(int i = 0; i < 16*8; i++) {
            if(!(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof AirBlock) && !(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof BallastBlock) && !(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof TrackBlock)) break;
            BallastBlock.addLayers(level, acc.add(pos), map);
            acc = acc.add(inc);
        }
        return map;
    }

}
