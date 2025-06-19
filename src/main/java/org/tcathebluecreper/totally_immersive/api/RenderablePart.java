package org.tcathebluecreper.totally_immersive.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

public class RenderablePart {
    public final Vec3 pos;
    public final Vec3 pos2;
    public final Quaternionf rot;
    public final Vec3 scale;

    public RenderablePart(Vec3 pos, Quaternionf rot, Vec3 pos2, Vec3 scale) {
        this.pos = pos;
        this.rot = rot;
        this.pos2 = pos2;
        this.scale = scale;
    }

    public void render(TIBlockEntityRenderer<?> context, TIDynamicModel part, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay, RenderType renderType) {
        stack.pushPose();
        stack.translate(pos.x, pos.y, pos.z);
        stack.mulPose(rot);
        stack.translate(pos2.x, pos2.y, pos2.z);
        stack.scale((float) scale.x, (float) scale.y, (float) scale.z);
        context.renderPart(part, stack, buf, light, lightOverlay, renderType);
        stack.popPose();
    }
}