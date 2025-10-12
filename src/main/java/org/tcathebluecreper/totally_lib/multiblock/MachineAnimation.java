package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import org.tcathebluecreper.totally_lib.lib.AnimationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MachineAnimation {
    public List<AnimatedModelPart> parts = new ArrayList<>();

    public void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
        var lightTexture = Minecraft.getInstance().gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        RenderSystem.clearColor(1,1,1,1);
        if(parts != null) for(AnimatedModelPart part : parts) {
            part.render(frame, poseStack, bufferSource, level);
        }
    }

    public static class AnimatedModelPart {
        public final List<Vector4f> positionFrames;
        public final List<Vector4f> rotationFrames;
        public final List<Vector4f> scaleFrames;
        public final List<BakedQuad> quads;

        public AnimatedModelPart(List<Vector4f> positionFrames, List<Vector4f> rotationFrames, List<Vector4f> scaleFrames, List<BakedQuad> quads) {
            this.positionFrames = positionFrames;
            positionFrames.sort((a,b) -> (int) (a.w - b.w));
            this.rotationFrames = rotationFrames;
            rotationFrames.sort((a,b) -> (int) (a.w - b.w));
            this.scaleFrames = scaleFrames;
            scaleFrames.sort((a,b) -> (int) (a.w - b.w));
            this.quads = quads;
        }

        public void render(float frame, PoseStack poseStack, MultiBufferSource bufferSource, Level level) {
            positionFrames.sort(Comparator.comparingDouble(Vector4f::w));
            rotationFrames.sort(Comparator.comparingDouble(Vector4f::w));
            scaleFrames.sort(Comparator.comparingDouble(Vector4f::w));

            int posIndex = positionFrames.indexOf(positionFrames.stream().filter(vector4f -> vector4f.w <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startPos = posIndex != -1 ? positionFrames.get(posIndex) : null;
            Vector4f endPos = posIndex != -1 && positionFrames.size() > posIndex + 1 ? positionFrames.get(posIndex + 1) : null;

            int rotIndex = rotationFrames.indexOf(rotationFrames.stream().filter(vector4f -> vector4f.w <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startRot = rotIndex != -1 ? rotationFrames.get(rotIndex) : null;
            Vector4f endRot = rotIndex != -1 && rotationFrames.size() > rotIndex + 1 ? rotationFrames.get(rotIndex + 1) : null;

            int sizeIndex = scaleFrames.indexOf(scaleFrames.stream().filter(vector4f -> vector4f.w <= frame).reduce((first, second) -> second).orElse(null));
            Vector4f startSize = sizeIndex != -1 ? scaleFrames.get(sizeIndex) : null;
            Vector4f endSize = sizeIndex != -1 && scaleFrames.size() > sizeIndex + 1 ? scaleFrames.get(sizeIndex + 1) : null;

            poseStack.pushPose();
            try {
                if(startPos != null) {
                    Vector4f pos;
                    if(endPos != null) {
                        pos = ((Vector4f)startPos.clone()).lerp(endPos, AnimationUtils.amount(frame - startPos.w, endPos.w - startPos.w));
                    }
                    else pos = startPos;
                    poseStack.translate(pos.x, pos.y, pos.z);
                }
                if(startRot != null) {
                    Vector4f rot;
                    if(endRot != null) {
                        rot = ((Vector4f)startRot.clone()).lerp(endRot, AnimationUtils.amount(frame - startRot.w, endRot.w - startRot.w));
                    }
                    else rot = startRot;
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.x * Mth.DEG_TO_RAD, 1,0,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.y * Mth.DEG_TO_RAD, 0,1,0));
                    poseStack.mulPose(new Quaternionf().rotateAxis(rot.z * Mth.DEG_TO_RAD, 0,0,1));
                }
                if(startSize != null) {
                    Vector4f size;
                    if(endSize != null) {
                        size = ((Vector4f)startSize.clone()).lerp(endSize, AnimationUtils.amount(frame - startSize.w, endSize.w - startSize.w));
                    }
                    else size = startSize;
                    poseStack.scale(size.x, size.y, size.z);
                }
            } catch(CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            RenderUtils.renderModelTESRFancy(quads, bufferSource.getBuffer(RenderType.solid()), poseStack, level, BlockPos.ZERO, false,-1, 15);
            poseStack.popPose();
        }
    }
}
