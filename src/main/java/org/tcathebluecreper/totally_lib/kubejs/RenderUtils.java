package org.tcathebluecreper.totally_lib.kubejs;

import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.EnumMap;
import java.util.Map;

public class RenderUtils {
    public static void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2) {
        renderQuad(matrixStack, color, texture, consumer, pPackedLight, x1, z1, x2, z2,
            texture.getU0(),
            texture.getV0(),
            texture.getU1(),
            texture.getV1()
        );
    }
    public static void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2, float minU, float minV, float maxU, float maxV) {
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

    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
        new EnumMap<>(Direction.class), m -> {
            for(Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX)
                m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180-facing.toYRot())));
        }
    );

    private static void rotateForFacingNoCentering(PoseStack stack, Direction facing) {
        stack.mulPose(ROTATE_FOR_FACING.get(facing));
    }

    public static void rotateForFacing(PoseStack stack, Direction facing) {
        stack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(stack, facing);
        stack.translate(-0.5, -0.5, -0.5);
    }
}
