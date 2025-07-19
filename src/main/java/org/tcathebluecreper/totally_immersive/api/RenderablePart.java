package org.tcathebluecreper.totally_immersive.api;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

import java.util.List;

public class RenderablePart {
    public final Vec3 pos;
    public final Vec3 pos2;
    public final Quaternionf rot;
    public final Quaternionf rot2;
    public final Vec3 scale;

    public RenderablePart(Vec3 pos, Quaternionf rot, Vec3 pos2, Vec3 scale) {
        this.pos = pos;
        this.rot = rot;
        this.rot2 = new Quaternionf();
        this.pos2 = pos2;
        this.scale = scale;
    }
    public RenderablePart(Vec3 pos, Quaternionf rot, Quaternionf rot2, Vec3 pos2, Vec3 scale) {
        this.pos = pos;
        this.rot = rot;
        this.rot2 = rot2;
        this.pos2 = pos2;
        this.scale = scale;
    }

    public void render(TIDynamicModel part, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay, RenderType renderType) {
        stack.pushPose();
        stack.translate(pos.x, pos.y, pos.z);
        stack.mulPose(rot);
        stack.mulPose(rot2);
        stack.translate(pos2.x, pos2.y, pos2.z);
        stack.scale((float) scale.x, (float) scale.y, (float) scale.z);
        renderPart(part, stack, buf, light, lightOverlay, renderType);
        stack.popPose();
    }
    public void renderPart(TIDynamicModel part, PoseStack matrix, MultiBufferSource buffer, int light, int overlay, RenderType rt) {
        matrix.pushPose();
        matrix.translate(-.5, -.5, -.5);
        List<BakedQuad> quads = part.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(rt), matrix, light, overlay);
        matrix.popPose();
    }
}