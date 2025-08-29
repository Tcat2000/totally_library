package org.tcathebluecreper.totally_lib.dev_utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SelectionManager {
    public static BlockPos firstPos;
    public static BlockPos secondPos;

    public static void render(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if(firstPos == null || secondPos == null) return;

        PoseStack ms = event.getPoseStack();
        ms.pushPose();

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        PoseStack stack = event.getPoseStack();
        VertexConsumer linesBuffer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES);
//        LevelRenderer.renderLineBox(stack, linesBuffer, new AABB(0,0,0, 1,0,0), 1,1,1,1);

        VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());

        int width = 1;

        Matrix4f matrix = stack.last().pose();
        Matrix3f normal = stack.last().normal();

        builder.vertex(matrix, -width / 2, 0, -width / 2)
            .color(1.0f, 0.0f, 0.0f, 0.5f) // Red with 50% opacity
            .uv(0, 0)
            .uv2(0,0)
            .normal(normal, 0, 0, 1)
            .endVertex();

        builder.vertex(matrix, -width / 2, 0, width / 2)
            .color(1.0f, 0.0f, 0.0f, 0.5f)
            .uv(0, 1)
            .uv2(0, 1)
            .normal(normal, 0, 0, 1)
            .endVertex();

        builder.vertex(matrix, width / 2, 0, width / 2)
            .color(1.0f, 0.0f, 0.0f, 0.5f)
            .uv(1, 1)
            .uv2(1, 1)
            .normal(normal, 0, 0, 1)
            .endVertex();

        builder.vertex(matrix, width / 2, 0, -width / 2)
            .color(1.0f, 0.0f, 0.0f, 0.5f)
            .uv(1, 0)
            .uv2(1, 0)
            .normal(normal, 0, 0, 1)
            .endVertex();

        ms.popPose();
    }
}
