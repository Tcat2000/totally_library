package org.tcathebluecreper.totally_immersive.mod.block.track;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.api.RenderablePart;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;
import org.tcathebluecreper.totally_immersive.api.shapes.CompoundVoxelShape;
import org.tcathebluecreper.totally_immersive.mod.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.api.lib.AnimationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return blockGetter.getBlockEntity(pos) != null ? ((BridgeBlockEntity) blockGetter.getBlockEntity(pos)).blockShape.shape : Shapes.block();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (level, pos, pos2, bet) -> {
            BridgeBlockEntity be = (BridgeBlockEntity) level.getBlockEntity(pos);
            if(be == null || be.localVector == null || be.targetVector == null || be.targetPos == null) return;

            if(!be.needUpdate) return;
            be.needUpdate = false;

            be.blockShape = new CompoundVoxelShape();
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

            int lastPillarHeight = 0;
            Vec3 lastVec = null;
            Vec3 lastPillarPos = null;
            boolean alternatingHeight = false;

            for(int i = 0; i < tiePositions.size(); i++) {

                Vec3 nextTiePos = tiePositions.size() > i + 1 ? tiePositions.get(i + 1) : tiePositions.get(i);
                Vec3 currentPillarPos = tiePositions.get(i);
                lastTiePos = 0 <= i - 1 ? tiePositions.get(i - 1) : tiePositions.get(i);

                if(currentPillarPos.equals(new Vec3(Float.NaN, Float.NaN, Float.NaN))) currentPillarPos = new Vec3(0, 0, 0);

                totalDistance -= tieDistance;
                Vec3 vec = nextTiePos.subtract(lastTiePos).normalize().multiply(tieDistance, tieDistance, tieDistance).normalize();

                Vec3 sideVec1 = vec.yRot(90 * Mth.DEG_TO_RAD);
                sideVec1 = sideVec1.add(0, -0.33, 0).normalize();

                Vec3 sideVec2 = vec.yRot(-90 * Mth.DEG_TO_RAD);
                sideVec2 = sideVec2.add(0, -0.33, 0).normalize();

                level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + currentPillarPos.x, pos.getY() + currentPillarPos.y, pos.getZ() + currentPillarPos.z, 0, 0, 0);
                level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + currentPillarPos.x + vec.x, pos.getY() + currentPillarPos.y + vec.y, pos.getZ() + currentPillarPos.z + vec.z, 0, 0, 0);

                float slop = (float) (vec.dot(new Vec3(0,1,0)) * 2.3);
                float angle = (float) ((vec.dot(new Vec3(1,0,0)) * Mth.PI) + 90) * Mth.DEG_TO_RAD;
                Vector3f axis = new Vector3f((float) vec.z, (float) 0, (float) vec.x);

                int pillarHeight = findHeight(level, pos.getCenter().add(currentPillarPos), vec);

                if(pillarHeight > 0) {
                    be.renderBeams.add( // vertical post center
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Vec3(0, 0, 0),
                            new Vec3(1, pillarHeight, 1)));
                    be.renderBeams.add( // vertical post left 1
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Quaternionf().rotateAxis(-4.5f * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)),
                            new Vec3(0, -0.125, 0.735),
                            new Vec3(1, pillarHeight, 1)));
                    be.renderBeams.add( // vertical post left 2
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Quaternionf().rotateAxis(-9f * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)),
                            new Vec3(0, -0.25, 1.45),
                            new Vec3(1, pillarHeight, 1)));
                    be.renderBeams.add( // vertical post right 1
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Quaternionf().rotateAxis(4.5f * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)),
                            new Vec3(0, -0.125, -0.735),
                            new Vec3(1, pillarHeight, 1)));
                    be.renderBeams.add( // vertical post right 2
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Quaternionf().rotateAxis(9f * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)),
                            new Vec3(0, -0.25, -1.45),
                            new Vec3(1, pillarHeight, 1)));


                    be.renderBeamsHorizontal.add( // horizontal top plate
                        new BridgeBeamRenderer(
                            currentPillarPos,
                            new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                            new Vec3(0, -3 / 16f, 1 / 16f),
                            new Vec3(1 + 1 / 7f, 6 / 8f, 4.5f)));

                    int l = 0;
                    for(int v = 3; v < pillarHeight - 1; v += 5) {
                        be.renderBeamsHorizontal.add( // horizontal crossbar A
                            new BridgeBeamRenderer(
                                currentPillarPos,
                                new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                                new Vec3(0.275, -v - 14 / 16f, 0),
                                new Vec3(4 / 7f, 4 / 8f, v / 3.15f + 4)));
                        be.renderBeamsHorizontal.add( // horizontal crossbar B
                            new BridgeBeamRenderer(
                                currentPillarPos,
                                new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                                new Vec3(-0.275, -v - 14 / 16f, 0),
                                new Vec3(4 / 7f, 4 / 8f, v / 3.15f + 4)));

                        float[] angles = new float[]{46,48,57};
                        float[] lengths = new float[]{5.f,6.7f, 8};
                        float[] sideOffsets = new float[]{0.55f,0.85f,0.8f};
                        float[] verticalOffsets = new float[]{0.125f,0.3f,0.35f};
                        float[] horizontalOffsets = new float[]{0.125f,0.3f,0.2f};
                        if(l < angles.length) {
                            Vec3 p = currentPillarPos.add(vec.xRot(90 * Mth.DEG_TO_RAD).multiply(v, v + 3.5, v)).subtract(currentPillarPos.add(vec.yRot(90 * Mth.DEG_TO_RAD).zRot(90 * Mth.DEG_TO_RAD).multiply(v, v, v)));

                            be.renderBeams.add( // brace A
                                new BridgeBeamRenderer(
                                    currentPillarPos.add(0,verticalOffsets[l] - v + 0.5f,0),
                                    new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                                    new Quaternionf().lookAlong(new Vector3f((float) 1, (float) 0, (float) 0), new Vector3f(0, 1, 0)).rotateAxis(angles[l] * Mth.DEG_TO_RAD, new Vector3f(0,0,1)),
                                    new Vec3(sideOffsets[l], lengths[l] / 2 + horizontalOffsets[l], 0.25),
                                    new Vec3(1, lengths[l], 0.5)));
                            be.renderBeams.add( // brace B .multiply(v, v + 3.5, v)
                                new BridgeBeamRenderer(
                                    currentPillarPos.add(0,verticalOffsets[l] - v + 0.5f,0),
                                    new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0, 1, 0)),
                                    new Quaternionf().lookAlong(new Vector3f((float) 1, (float) 0, (float) 0), new Vector3f(0, 1, 0)).rotateAxis(-angles[l] * Mth.DEG_TO_RAD, new Vector3f(0,0,1)),
                                    new Vec3(-sideOffsets[l], lengths[l] / 2 + horizontalOffsets[l], -0.25),
                                    new Vec3(1, lengths[l], 0.5)));

                            be.blockShape.add(new Vector3f(0,0,0), new Vector3f(2,0,0), new Vector3f(0,0,2), new Vector3f(2,0,2), new Vector3f(0,2,0), new Vector3f(2,2,0), new Vector3f(0,2,2), new Vector3f(2,2,2));
                        }
                        l++;
                    }

                    if(lastPillarHeight != 0) {
                        Vec3 midVec = vec.add(lastVec).normalize();
                        Vec3 midPillarPosA = TIMath.lerp3D(lastPillarPos.add(lastVec.yRot(-90 * Mth.DEG_TO_RAD)), currentPillarPos.add(vec.yRot(-90 * Mth.DEG_TO_RAD)), 0.5f);
                        float lengthA = (float) lastPillarPos.add(lastVec.yRot(-90 * Mth.DEG_TO_RAD)).distanceTo(currentPillarPos.add(vec.yRot(-90 * Mth.DEG_TO_RAD)));
                        Vec3 midPillarPosB = TIMath.lerp3D(lastPillarPos.add(lastVec.yRot(90 * Mth.DEG_TO_RAD)), currentPillarPos.add(vec.yRot(90 * Mth.DEG_TO_RAD)), 0.5f);
                        float lengthB = (float) lastPillarPos.add(lastVec.yRot(90 * Mth.DEG_TO_RAD)).distanceTo(currentPillarPos.add(vec.yRot(90 * Mth.DEG_TO_RAD)));

                        float height = alternatingHeight ? 0.245f : 0.24f;
                        alternatingHeight = !alternatingHeight;

                        be.renderBeamsHorizontal.add( // horizontal top bar A
                            new BridgeBeamRenderer(
                                midPillarPosA,
                                new Quaternionf().lookAlong(new Vector3f((float) midVec.z, (float) midVec.y, (float) midVec.x), new Vector3f(0, 1, 0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)),
                                new Vec3(0, height, 0),
                                new Vec3(1.3333, 1, lengthA)));

                        be.renderBeamsHorizontal.add( // horizontal top bar B
                            new BridgeBeamRenderer(
                                midPillarPosB,
                                new Quaternionf().lookAlong(new Vector3f((float) midVec.z, (float) midVec.y, (float) midVec.x), new Vector3f(0, 1, 0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)),
                                new Vec3(0, height, 0),
                                new Vec3(1, 1, lengthB)));


                        for(int c = 0; c < Math.min(lastPillarHeight, pillarHeight); c++) {
                            Vec3 braceStartPosA = lastPillarPos.add(lastVec.yRot(-90 * Mth.DEG_TO_RAD).multiply(2.5f,2.5f,2.5f)).add(0,-1,0);
                            Vec3 braceEndPosA = currentPillarPos.add(vec.yRot(-90 * Mth.DEG_TO_RAD).multiply(2,2,2)).add(0,-5,0);
                            Vec3 midPosA = TIMath.lerp3D(braceStartPosA, braceEndPosA, 0.5f);
                            Vec3 vectorA = braceEndPosA.subtract(midPosA).normalize();
                            float crossLengthA = (float) braceStartPosA.distanceTo(braceEndPosA);

                            be.renderBeamsHorizontal.add( // mid pillar brace A
                                new BridgeBeamRenderer(
                                    midPosA,
                                    new Quaternionf().lookAlong(new Vector3f((float) vectorA.z, (float) vectorA.y, (float) vectorA.x), new Vector3f(0, 1, 0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)),
                                    new Vec3(0, height, 0),
                                    new Vec3(1.3333, 1, crossLengthA)));
                        }
                    }
                }
                lastPillarHeight = pillarHeight;
                lastVec = vec;
                lastPillarPos = currentPillarPos;

//                be.renderBeamsHorizontal.add( // horizontal crossbar 1 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(0.275, -3 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 4.5)));
//                be.renderBeamsHorizontal.add( // horizontal crossbar 1 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(-0.275, -3 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 4.5)));
//                be.renderBeamsHorizontal.add( // horizontal crossbar 2 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(0.275, -8 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 6.5)));
//                be.renderBeamsHorizontal.add( // horizontal crossbar 2 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(-0.275, -8 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 6.5)));
//                be.renderBeamsHorizontal.add( // horizontal crossbar 3 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(0.275, -13 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 8)));
//                be.renderBeamsHorizontal.add( // horizontal crossbar 3 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)),
//                        new Quaternionf().rotateAxis(90f * Mth.DEG_TO_RAD,  new Vector3f(0,1,0)),
//                        new Vec3(-0.275, -13 - 14/16f, 0),
//                        new Vec3(4/7f, 4/8f, 8)));


//                be.renderBeams.add( // pillar brace 1 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(50f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(-0.275, 1.62, 22.4/16f),
//                        new Vec3(4/7f, 5.6, 4/8f)));
//
//                be.renderBeams.add( // pillar brace 1 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(-50f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(0.275, 1.62, -22.4/16f),
//                        new Vec3(4/7f, 5.6, 4/8f)));
//
//                be.renderBeams.add( // pillar brace 2 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(48f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(-0.275, 1.62 - 2.75, 4 + 7.75/16f),
//                        new Vec3(4/7f, 6 + 12/16f, 4/8f)));
//                be.renderBeams.add( // pillar brace 2 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(-48f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(0.275, 1.62 - 2.75, -4 - 7.75/16f),
//                        new Vec3(4/7f, 6 + 12/16f, 4/8f)));
//
//                be.renderBeams.add( // pillar brace 3 A
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(55.5f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(-0.275, 1.62 - 4.37, 9 + 2.5/16f),
//                        new Vec3(4/7f, 8, 4/8f)));
//                be.renderBeams.add( // pillar brace 3 B
//                    new RenderablePart(
//                        currentPillarPos,
//                        new Quaternionf().rotateAxis(angle, new Vector3f(0,1,0))
//                            .rotateAxis(-55.5f * Mth.DEG_TO_RAD, new Vector3f((float) vec.z, (float) vec.y, (float) vec.x)),
//                        new Vec3(0.275, 1.62 - 4.37, -9 - 2.5/16f),
//                        new Vec3(4/7f, 8, 4/8f)));
            }
        };
    }

    private int findHeight(Level level, Vec3 pos, Vec3 forward) {
        forward = forward.normalize().yRot(90 * Mth.DEG_TO_RAD);

        int height = 0;
        Vec3 startPos = pos.add(forward.multiply(2.5,2.5,2.5));
        Vec3 endPos = pos.add(forward.multiply(-2.5,-2.5,-2.5));

        while(true) {
            if(level.isInWorldBounds(BlockPos.containing(startPos)) && level.isInWorldBounds(BlockPos.containing(endPos))) {
                BlockState startState = level.getBlockState(BlockPos.containing(startPos));
                BlockState endState = level.getBlockState(BlockPos.containing(endPos));

                if(startState.getBlock() instanceof AirBlock && endState.getBlock() instanceof AirBlock) {
                    height++;
                    startPos = startPos.subtract(0, 1, 0);
                    endPos = endPos.subtract(0, 1, 0);
                }
                else return height;
            }
            else return height;
        }
    }


    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof BridgeBlockEntity) ((BridgeBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).needUpdate = true;
        return InteractionResult.PASS;
    }

    public static class BridgeBeamRenderer extends RenderablePart {

        public BridgeBeamRenderer(Vec3 pos, Quaternionf rot, Vec3 pos2, Vec3 scale) {
            super(pos, rot, pos2, scale);
        }

        public BridgeBeamRenderer(Vec3 pos, Quaternionf rot, Quaternionf rot2, Vec3 pos2, Vec3 scale) {
            super(pos, rot, rot2, pos2, scale);
        }

        public void render(TIBlockEntityRenderer<?> context, TIDynamicModel part, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay, RenderType renderType) {
            stack.pushPose();
            stack.translate(pos.x, pos.y, pos.z);
            stack.mulPose(rot);
            stack.mulPose(rot2);
            stack.translate(pos2.x, pos2.y, pos2.z);
            stack.scale((float) scale.x, (float) scale.y, (float) scale.z);

            VertexConsumer consumer = buf.getBuffer(Sheets.translucentCullBlockSheet());
            TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "block/wooden_decoration/treated_wood"));
            context.renderPart(part, stack, buf, light, lightOverlay, renderType);
            renderQuad(stack, 0, texture, consumer, light, 0, 0, 1, 1);

            stack.popPose();
        }
    }
    private static void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2) {
        renderQuad(matrixStack, color, texture, consumer, pPackedLight, x1, z1, x2, z2,
            texture.getU0(),
            texture.getV0(),
            texture.getU1(),
            texture.getV1()
        );
    }
    private static void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2, float minU, float minV, float maxU, float maxV) {
        float y = 0.0f;

        // Normals for a face pointing upwards (Y-axis)
        float normalX = 0.0f;
        float normalY = 1.0f; // Points straight up
        float normalZ = 0.0f;

        // Get the current transformation matrices from the PoseStack
        Matrix4f modelViewMatrix = matrixStack.last().pose();
        Matrix3f normalMatrix = matrixStack.last().normal();

        // Vertex 1: Bottom-Left (relative to quad)
        consumer.vertex(modelViewMatrix, x1, y, z1)
            .color(color) // Full white, opaque
            .uv(minU, minV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(pPackedLight) // Light level
            .normal(normalMatrix, normalX, normalY, normalZ)
            .endVertex();

        // Vertex 2: Bottom-Right
        consumer.vertex(modelViewMatrix, x2, y, z1)
            .color(color)
            .uv(maxU, minV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(pPackedLight)
            .normal(normalMatrix, normalX, normalY, normalZ)
            .endVertex();

        // Vertex 3: Top-Right
        consumer.vertex(modelViewMatrix, x2, y, z2)
            .color(color)
            .uv(maxU, maxV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(pPackedLight)
            .normal(normalMatrix, normalX, normalY, normalZ)
            .endVertex();

        // Vertex 4: Top-Left
        consumer.vertex(modelViewMatrix, x1, y, z2)
            .color(color)
            .uv(minU, maxV)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(pPackedLight)
            .normal(normalMatrix, normalX, normalY, normalZ)
            .endVertex();
    }
}
