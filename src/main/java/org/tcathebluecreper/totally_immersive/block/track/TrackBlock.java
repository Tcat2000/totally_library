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
import net.minecraft.world.level.block.Blocks;
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
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.lib.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
            assert be != null;


            if(be.localVector == null || be.targetVector == null || be.target == null) return;
//            level.addParticle(ParticleTypes.FLAME,
//                pos.getX() + be.localVector.x + 0.5,
//                pos.getY() + be.localVector.y + 0.5,
//                pos.getZ() + be.localVector.z + 0.5,
//                pos.getX() + 0.5,
//                pos.getY() + 0.5,
//                pos.getZ() + 0.5
//            );
//            level.addParticle(ParticleTypes.FLAME,
//                be.target.getX() + be.targetVector.x + 0.5,
//                be.target.getY() + be.targetVector.y + 0.5,
//                be.target.getZ() + be.targetVector.z + 0.5,
//                be.target.getX() + 0.5,
//                be.target.getY() + 0.5,
//                be.target.getZ() + 0.5
//            );

            be.renderBlocks = new HashMap<>();
            for(int i = 0; i < 365; i++) {
                iterateVector(new Vec3(Math.sin(i * Mth.DEG_TO_RAD) * 3,-2,Math.cos(i * Mth.DEG_TO_RAD) * 3), level, pos).forEach((POS, STATE) -> {
                    if(be.renderBlocks.containsKey(POS)) {
                        be.renderBlocks.put(POS, BallastBlock.combine(STATE, be.renderBlocks.get(POS)));
                    }
                    else be.renderBlocks.put(POS, STATE);
                });
            }


            BlockPos pos0 = new BlockPos(0,0,0);
            BlockPos pos1 = be.target.offset(invertBlockPos(be.getBlockPos()));
            Vec3 vector0 = be.localVector;
            Vec3 vector1 = be.targetVector;

            be.renderTies = new ArrayList<>();
            be.renderRails = new ArrayList<>();

            if(!(vector0 == null || vector1 == null)) {
                Vec3 last = TIMath.curve(pos0, vector0, pos1, vector1, 0);
                Vec3 lastTarget = TIMath.curve(pos0, vector0, pos1, vector1, 0);
                Vec3 first = last;
                Vec3 lastVec = be.localVector.normalize();
                final Vec3 normal = new Vec3(0,0,1);
                float dist = 0;
                float targetDist = 0.5f;

                float inc = 0.001f;
                for(float i = 0; i < 1; i += inc) {
                    Vec3 current = TIMath.curve(pos0, vector0, pos1, vector1, i);
                    float currentDist = (float) TIMath.vectorDist(last, current);

                    if(dist >= targetDist) {
                        Vec3 next = TIMath.curve(pos0, vector0, pos1, vector1, i + inc);
                        Vec3 real = TIMath.lerp3D(lastTarget, next, AnimationUtils.amount(dist - currentDist, (float) (TIMath.vectorDist(current, next))));//dist - currentDist | TIMath.vectorDist(current, next) - currentDist

                        dist -= targetDist;
                        Vec3 vec = real.subtract(lastTarget).normalize().multiply(targetDist, targetDist, targetDist);


                        be.renderTies.add(new TrackBlockEntityRenderer.RenderableTrackPart(lastTarget, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));
                        be.renderRails.add(new TrackBlockEntityRenderer.RenderableTrackPart(lastTarget, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(1, 3 / 16f, 0), new Vec3(1, 1, 10 * targetDist)));
                        be.renderRails.add(new TrackBlockEntityRenderer.RenderableTrackPart(lastTarget, new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)), new Vec3(-1, 3 / 16f, 0), new Vec3(1, 1, 10 * targetDist)));

                        lastTarget = current;
                    }
                    dist += currentDist;
                    last = current;
                }
            }
        };
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {

        for(int i = 0; i < 365; i++) {
            iterateVector(new Vec3(Math.sin(i * Mth.DEG_TO_RAD) * 3,-2,Math.cos(i * Mth.DEG_TO_RAD) * 3), level, pos);
        }
        if(state != null) return InteractionResult.PASS;

        TrackBlockEntity be = (TrackBlockEntity) level.getBlockEntity(pos);
        if(be.target == null || be.targetVector == null || be.localVector == null) return InteractionResult.FAIL;


        BlockPos pos0 = new BlockPos(0,0,0);
        BlockPos pos1 = be.target.offset(invertBlockPos(be.getBlockPos()));
        Vec3 vector0 = be.localVector;
        Vec3 vector1 = be.targetVector;

        if(!(vector0 == null || vector1 == null)) {
            Vec3 last = TIMath.curve(pos0, vector0, pos1, vector1, 0);
            Vec3 lastTarget = TIMath.curve(pos0, vector0, pos1, vector1, 0);
            Vec3 first = last;
            Vec3 lastVec = be.localVector.normalize();
            final Vec3 normal = new Vec3(0,0,1);
            float dist = 0;
            float targetDist = 0.5f;

            float inc = 0.001f;
            for(float i = 0; i < 1; i += inc) {
                Vec3 current = TIMath.curve(pos0, vector0, pos1, vector1, i);
                float currentDist = (float) TIMath.vectorDist(last, current);

                if(dist >= targetDist) {
                    Vec3 next = TIMath.curve(pos0, vector0, pos1, vector1, i + inc);
                    Vec3 real = TIMath.lerp3D(lastTarget, next, AnimationUtils.amount(dist - currentDist, (float) (TIMath.vectorDist(current, next) - currentDist)));//dist - currentDist | TIMath.vectorDist(current, next) - currentDist

                    dist -= targetDist;
                    Vec3 vec = real.subtract(lastTarget).normalize().multiply(targetDist, targetDist, targetDist);



//                    stack.pushPose();
//                    stack.scale(1, 1, 10 * targetDist);//(float) TIMath.vectorDist(lastTarget.add(lastTarget.z, 0, lastTarget.x), current.add(current.z, 0, current.x))
//                    stack.translate(1, 3 / 16f, 0);
//                    renderPart(rail, stack, buf, 100, lightOverlay);
//                    stack.popPose();
//
//                    stack.scale(1, 1, 10 * targetDist);//(float) TIMath.vectorDist(lastTarget.add(-lastTarget.z, 0, -lastTarget.x), current.add(-current.z, 0, -current.x))
//                    stack.translate(-1, 3/16f, 0);
//                    renderPart(rail, stack, buf, 100, lightOverlay);

                    lastTarget = current;
                }
                dist += currentDist;
                last = current;
            }
        }
        return InteractionResult.PASS;
    }

    public static Map<BlockPos, BlockState> iterateVector(Vec3 vec, Level level, BlockPos pos) {
        vec.normalize();
        Vec3 inc = vec.multiply(1/16f, 1/16f, 1/16f);
        Vec3 acc = new Vec3(0,0,0);
        Map<BlockPos, BlockState> map = new HashMap<>();
        for(int i = 0; i < 16*8; i++) {
            System.out.println("state: " + (level.getBlockState(pos).getBlock()));
            if(!(level.getBlockState(pos).getBlock() instanceof AirBlock) && !(level.getBlockState(pos).getBlock() instanceof BallastBlock) && !(level.getBlockState(pos).getBlock() instanceof TrackBlock)) {
                break;
            }
            BallastBlock.addLayers(level, acc.add(pos.getCenter())).forEach((POS, STATE) -> {
                if(map.containsKey(POS)) {
                    map.put(POS, BallastBlock.combine(STATE, map.get(POS)));
                }
                else map.put(POS, STATE);
            });
            acc = acc.add(inc);
        }
        return map;
    }

}
