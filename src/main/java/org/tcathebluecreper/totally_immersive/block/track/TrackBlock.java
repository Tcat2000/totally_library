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

import java.util.*;
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
        return this::updateTrack;
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof TrackBlockEntity) ((TrackBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).needUpdate = true;
        return InteractionResult.PASS;
    }

    public static Map<BlockPos, BlockState> iterateVector(Vec3 dir, Level level, Vec3 pos) {
        return iterateVector(dir, level, pos, null);
    }

    public static Map<BlockPos, BlockState> iterateVector(Vec3 dir, Level level, Vec3 pos, Float minBallastHeight) {
        dir = dir.normalize();
        Vec3 inc = dir.multiply(1/16f, 1/16f, 1/16f);
        Vec3 acc = new Vec3(0,0,0);
        Map<BlockPos, BlockState> map = new HashMap<>();
        BallastBlock.addLayers(level, acc.add(pos).subtract(inc.x * 4, 0, inc.z * 4), map);
        BallastBlock.addLayers(level, acc.add(pos).subtract(inc.x * 8, 0, inc.z * 8), map);
        BallastBlock.addLayers(level, acc.add(pos).subtract(inc.x * 12, 0, inc.z * 12), map);
        BallastBlock.addLayers(level, acc.add(pos).subtract(inc.x * 16, 0, inc.z * 16), map);
        for(int i = 0; i < 16*8; i++) {
            if(minBallastHeight != null && acc.add(pos).y < minBallastHeight) break;
            if(!(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof AirBlock) && !(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof BallastBlock) && !(level.getBlockState(BlockPos.containing(pos.add((int) acc.x, (int) acc.y, (int) acc.z))).getBlock() instanceof TrackBlock)) break;
            BallastBlock.addLayers(level, acc.add(pos), map);
            acc = acc.add(inc);
        }
        return map;
    }

    public void updateTrack(Level level, BlockPos pos, BlockState state, Object bet) {
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
        if(startDirection == null || endDirection == null) return;

        Vec3 lastPos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
        Vec3 lastTiePos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
        float tieDistance = 0.5f;
        float totalDistance = tieDistance;
        float inc = 0.01f;

        List<Vec3> tiePositions = new ArrayList<>();
        for(float i = 0; i < 1; i += inc) {
            Vec3 current = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
            float currentDist = (float) TIMath.vectorDist(lastPos, current);
            if(totalDistance >= tieDistance) {
                totalDistance -= tieDistance;
                Vec3 next = TIMath.curve(startPos, startDirection, endPos, endDirection, i);
                Vec3 real = TIMath.lerp3D(lastTiePos, next, AnimationUtils.amount(tieDistance, (float) lastTiePos.distanceTo(next)));
                tiePositions.add(real);
                lastTiePos = real;
            }
            totalDistance += currentDist;
            lastPos = current;
        }

        for(int i = 0; i < tiePositions.size(); i++) {

            Vec3 nextTiePos = tiePositions.size() > i + 1 ? tiePositions.get(i + 1) : tiePositions.get(i);
            Vec3 currentTiePos = tiePositions.get(i);
            lastTiePos = 0 <= i - 1 ? tiePositions.get(i - 1) : tiePositions.get(i);

            if(currentTiePos.equals(new Vec3(Float.NaN, Float.NaN, Float.NaN))) currentTiePos = new Vec3(0, 0, 0);

            totalDistance -= tieDistance;
            Vec3 vec = nextTiePos.subtract(lastTiePos).normalize().multiply(tieDistance, tieDistance, tieDistance);

            Vec3 sideVec1 = vec.yRot(90 * Mth.DEG_TO_RAD);
            sideVec1 = sideVec1.add(0, -0.33, 0).normalize();

            Vec3 sideVec2 = vec.yRot(-90 * Mth.DEG_TO_RAD);
            sideVec2 = sideVec2.add(0, -0.33, 0).normalize();

//            level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + lastTiePos.x, pos.getY() + lastTiePos.y - 0.499, pos.getZ() + lastTiePos.z + 1.05, 0, 0, 0);

            if(!be.constructed || be.previewForceBallast) {
                iterateVector(sideVec1, level, pos.getCenter().add(lastTiePos).add(sideVec1.x, -0.499, sideVec1.z).add(sideVec1.x / 2, 0, sideVec1.z / 2), be.previewMinBallastHeight).forEach((POS, STATE) -> {
                    if(be.renderBlocks.containsKey(POS)) {
                        be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                    } else be.renderBlocks.put(POS, STATE);
                });
                iterateVector(sideVec1, level, pos.getCenter().add(lastTiePos).add(sideVec1.x, -0.499, sideVec1.z).add(sideVec1.z / 2, 0, sideVec1.x / 2).add(sideVec1.x / 2, 0, sideVec1.z / 2), be.previewMinBallastHeight).forEach((POS, STATE) -> {
                    if(be.renderBlocks.containsKey(POS)) {
                        be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                    } else be.renderBlocks.put(POS, STATE);
                });

                iterateVector(sideVec2, level, pos.getCenter().add(lastTiePos).add(sideVec2.x, -0.499, sideVec2.z).add(sideVec1.z / 2, 0, sideVec1.x / 2).subtract(sideVec1.x / 2, 0, sideVec1.z / 2), be.previewMinBallastHeight).forEach((POS, STATE) -> {
                    if(be.renderBlocks.containsKey(POS)) {
                        be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                    } else be.renderBlocks.put(POS, STATE);
                });
                iterateVector(sideVec2, level, pos.getCenter().add(lastTiePos).add(sideVec2.x, -0.499, sideVec2.z).subtract(sideVec1.x / 2, 0, sideVec1.z / 2), be.previewMinBallastHeight).forEach((POS, STATE) -> {
                    if(be.renderBlocks.containsKey(POS)) {
                        be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                    } else be.renderBlocks.put(POS, STATE);
                });
            }

            double slop = vec.dot(new Vec3(0,1,0)) * 2.3;
            double angle = vec.dot(new Vec3(1,0,0)) * Mth.PI;

            be.renderTies.add(new RenderablePart(currentTiePos, new Quaternionf().rotateAxis((float) angle, new Vector3f(0,1,0), new Quaternionf().rotateAxis((float) slop, new Vector3f(-1,0,0))), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));
            be.renderRails.add(new RenderablePart(currentTiePos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(1, 3 / 16f, 0), new Vec3(1, 1, 10 * tieDistance)));
            be.renderRails.add(new RenderablePart(currentTiePos, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(-1, 3 / 16f, 0), new Vec3(1, 1, 10 * tieDistance)));
        }
    }
}
