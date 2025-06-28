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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            be.renderBeams = new ArrayList<>();
            be.renderBeamsHorizontal = new ArrayList<>();


            BlockPos startPos = new BlockPos(0,0,0);
            BlockPos endPos = be.targetPos.offset(TrackBlockEntityRenderer.invertBlockPos(be.getBlockPos()));
            Vec3 startDirection = be.localVector;
            Vec3 endDirection = be.targetVector;
            if(startDirection == null || endDirection == null) return;

            Vec3 lastPos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
            Vec3 lastTiePos = TIMath.curve(startPos, startDirection, endPos, endDirection, 0);
            float tieDistance = 4;
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

                level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + currentTiePos.x, pos.getY() + currentTiePos.y, pos.getZ() + currentTiePos.z, 0, 0, 0);
                level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + currentTiePos.x + vec.x, pos.getY() + currentTiePos.y + vec.y, pos.getZ() + currentTiePos.z + vec.z, 0, 0, 0);

                float slop = (float) (vec.dot(new Vec3(0,1,0)) * 2.3);
                float angle = (float) ((vec.dot(new Vec3(1,0,0)) * Mth.PI) + 90) * Mth.DEG_TO_RAD;
                Vector3f axis = new Vector3f((float) vec.z, (float) 0, (float) vec.x);

                be.renderBeams.add( // vertical post center
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().lookAlong(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z), new Vector3f(0,1,0)),
                        new Vec3(0, 0, 0),
                        new Vec3(1, 16, 1)));
                be.renderBeams.add( // vertical post left 1
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().lookAlong(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z), new Vector3f(0,1,0)),
                        new Quaternionf().rotateAxis(-4.5f * Mth.DEG_TO_RAD, axis),
                        new Vec3(0, -0.125, 0.735),
                        new Vec3(1, 16, 1)));
                be.renderBeams.add( // vertical post left 2
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().lookAlong(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z), new Vector3f(0,1,0)),
                        new Quaternionf().rotateAxis(-9f * Mth.DEG_TO_RAD, axis),
                        new Vec3(0, -0.25, 1.45),
                        new Vec3(1, 16, 1)));
                be.renderBeams.add( // vertical post right 1
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().lookAlong(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z), new Vector3f(0,1,0)),
                        new Quaternionf().rotateAxis(4.5f * Mth.DEG_TO_RAD, axis),
                        new Vec3(0, -0.125, -0.735),
                        new Vec3(1, 16, 1)));
                be.renderBeams.add( // vertical post right 2
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().lookAlong(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z), new Vector3f(0,1,0)),
                        new Quaternionf().rotateAxis(9f * Mth.DEG_TO_RAD, axis),
                        new Vec3(0, -0.25, -1.45),
                        new Vec3(1, 16, 1)));

                be.renderBeamsHorizontal.add( // horizontal top plate
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0)),
                        new Vec3(0, -3/16f, 1/16f),
                        new Vec3(1 + 1/7f, 6/8f, 4.5f)));
                be.renderBeams.add( // horizontal crossbar 1 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 2.25, 4 - 2/16f),
                        new Vec3(4/7f, 4.5, 4/8f)));
                be.renderBeams.add( // horizontal crossbar 1 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 2.25, 4 - 2/16f),
                        new Vec3(4/7f, 4.5, 4/8f)));
                be.renderBeams.add( // horizontal crossbar 2 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 3.25, 9 - 2/16f),
                        new Vec3(4/7f, 6.5, 4/8f)));
                be.renderBeams.add( // horizontal crossbar 2 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 3.25, 9 - 2/16f),
                        new Vec3(4/7f, 6.5, 4/8f)));
                be.renderBeams.add( // horizontal crossbar 3 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 4, 14 - 2/16f),
                        new Vec3(4/7f, 8, 4/8f)));
                be.renderBeams.add( // horizontal crossbar 3 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(90f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 4, 14 - 2/16f),
                        new Vec3(4/7f, 8, 4/8f)));

                be.renderBeams.add( // pillar brace 1 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(50f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 1.62, 22.4/16f),
                        new Vec3(4/7f, 5.6, 4/8f)));

                be.renderBeams.add( // pillar brace 1 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(-50f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 1.62, -22.4/16f),
                        new Vec3(4/7f, 5.6, 4/8f)));

                be.renderBeams.add( // pillar brace 2 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(48f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 1.62 - 2.75, 4 + 7.75/16f),
                        new Vec3(4/7f, 6 + 12/16f, 4/8f)));
                be.renderBeams.add( // pillar brace 2 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(-48f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 1.62 - 2.75, -4 - 7.75/16f),
                        new Vec3(4/7f, 6 + 12/16f, 4/8f)));

                be.renderBeams.add( // pillar brace 3 A
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(55.5f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(-0.275, 1.62 - 4.37, 9 + 2.5/16f),
                        new Vec3(4/7f, 8, 4/8f)));
                be.renderBeams.add( // pillar brace 3 B
                    new RenderablePart(
                        currentTiePos,
                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
                            .rotateAxis(-55.5f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
                        new Vec3(0.275, 1.62 - 4.37, -9 - 2.5/16f),
                        new Vec3(4/7f, 8, 4/8f)));
            }
        };
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof BridgeBlockEntity) ((BridgeBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).needUpdate = true;
        return InteractionResult.PASS;
    }
}
