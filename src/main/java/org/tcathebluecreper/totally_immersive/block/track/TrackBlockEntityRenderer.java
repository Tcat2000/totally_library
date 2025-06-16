package org.tcathebluecreper.totally_immersive.block.track;

import com.lowdragmc.lowdraglib.client.renderer.impl.BlockStateRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.lib.AnimationUtils;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

import static org.tcathebluecreper.totally_immersive.block.track.BallastBlock.NE_FILL;

public class TrackBlockEntityRenderer extends TIBlockEntityRenderer<TrackBlockEntity> {
    public static final String tieLocation = "track/tie";
    public static TIDynamicModel tie;
    public static final String railLocation = "track/rail";
    public static TIDynamicModel rail;
    @Override
    public void render(@NotNull TrackBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        if(be.target == null || be.targetVector == null || be.localVector == null) return;
        stack.pushPose();
        VertexConsumer consumer = buf.getBuffer(RenderType.lines());


        BlockPos pos0 = new BlockPos(0,0,0);
        BlockPos pos1 = be.target.offset(invertBlockPos(be.getBlockPos()));
        Vec3 vector0 = be.localVector;
        Vec3 vector1 = be.targetVector;

        if(!be.constructed && be.renderBlocks != null) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            be.renderBlocks.forEach((pos, state) -> {
                if(state == null) return;
                stack.pushPose();
                BlockPos offset = pos.subtract(be.getBlockPos());
                stack.translate(offset.getX(), offset.getY(), offset.getZ());
                dispatcher.renderSingleBlock(state, stack, buf, 100, lightOverlay, ModelData.builder().with(new ModelProperty<>(), 2).build(), RenderType.solid());
                stack.popPose();
            });
        }

        stack.translate(0.5,0.5,0.5);
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

                    stack.pushPose();
                    stack.translate(lastTarget.x, lastTarget.y, lastTarget.z);
                    stack.mulPose(new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)).rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)));

                    renderPart(tie, stack, buf, 100, lightOverlay);

                    stack.pushPose();
                    stack.scale(1, 1, 10 * targetDist);//(float) TIMath.vectorDist(lastTarget.add(lastTarget.z, 0, lastTarget.x), current.add(current.z, 0, current.x))
                    stack.translate(1, 3 / 16f, 0);
                    renderPart(rail, stack, buf, 100, lightOverlay);
                    stack.popPose();

                    stack.scale(1, 1, 10 * targetDist);//(float) TIMath.vectorDist(lastTarget.add(-lastTarget.z, 0, -lastTarget.x), current.add(-current.z, 0, -current.x))
                    stack.translate(-1, 3/16f, 0);
                    renderPart(rail, stack, buf, 100, lightOverlay);

                    stack.popPose();
                    lastTarget = current;
                }
                dist += currentDist;
                last = current;
            }
        }
        stack.popPose();
    }
    public static BlockPos invertBlockPos(BlockPos pos) {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public static class RenderableTrackPart {
        public final Vec3 pos;
        public final Quaternionf rot;
        public final Vec3 scale;

        public RenderableTrackPart(Vec3 pos, Quaternionf rot, Vec3 scale) {
            this.pos = pos;
            this.rot = rot;
            this.scale = scale;
        }

        public void render(TIDynamicModel part, PoseStack stack) {

        }
    }
}
