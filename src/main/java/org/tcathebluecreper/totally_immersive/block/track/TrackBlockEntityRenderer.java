package org.tcathebluecreper.totally_immersive.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

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

        stack.translate(0.5,0.5,0.5);
        if(!(vector0 == null || vector1 == null)) {
            Vec3 last = TIMath.curve(pos0, vector0, pos1, vector1, 0);
            Vec3 lastTarget = TIMath.curve(pos0, vector0, pos1, vector1, 0);
            Vec3 first = last;
            Vec3 lastVec = be.localVector.normalize();
            final Vec3 normal = new Vec3(0,0,1);
            float dist = 0;
            float targetDist = 0.75f;

            Matrix4f matrix4f = stack.last().pose();
            Matrix3f matrix3f = stack.last().normal();

//            consumer.vertex(matrix4f, 0, 0, 0).color(1f, 0f, 0f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
//            consumer.vertex(matrix4f, (float) normal.x, (float) normal.y, (float) normal.z).color(1f, 0f, 0f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();

            for(float i = 0; i < 1; i += 0.001f) {
                Vec3 current = TIMath.curve(pos0, vector0, pos1, vector1, i);
                dist += (float) TIMath.vectorDist(last, current);
//                stack.translate(current.x - last.x, current.y - last.y, current.z - last.z);
                if(dist >= targetDist) {
                    dist -= targetDist;
                    Vec3 vec = current.subtract(lastTarget).normalize().multiply(targetDist, targetDist, targetDist);

                    stack.pushPose();
                    matrix4f = stack.last().pose();
                    matrix3f = stack.last().normal();
                    stack.translate(lastTarget.x, lastTarget.y, lastTarget.z);

//                    consumer.vertex(matrix4f, 0, 0, 0).color(0f, 1f, 0f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
//                    consumer.vertex(matrix4f, (float) vec.x, (float) vec.y, (float) vec.z).color(0f, 1f, 0f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();

//                    stack.mulPose(TIMath.getRotationQuaternion(normal, vec));
                    stack.mulPose(new Quaternionf().lookAlong(new Vector3f((float) vec.z, (float) vec.y, (float) vec.x), new Vector3f(0,1,0)));

//                    consumer.vertex(matrix4f, 0, 0, 0).color(0f, 0f, 1f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
//                    consumer.vertex(matrix4f, (float) 1, (float) 0, (float) 0).color(0f, 0f, 1f, 1f).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();


                    stack.mulPose(new Quaternionf().rotateAxis(90 * Mth.DEG_TO_RAD, new Vector3f(0,1,0)));
//
                    renderPart(tie, stack, buf, 100, lightOverlay);
                    stack.scale(1, 1, 6);
                    stack.translate(1, 3 / 16f, 0);
                    renderPart(rail, stack, buf, 100, lightOverlay);

                    stack.translate(-2, 0, 0);
                    renderPart(rail, stack, buf, 100, lightOverlay);

                    stack.popPose();
                    lastTarget = current;
                }
                last = current;
            }
        }
        stack.popPose();
    }
    private static BlockPos invertBlockPos(BlockPos pos) {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }
}
