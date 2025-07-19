package org.tcathebluecreper.totally_immersive.api;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class TIBlockEntityRenderer<S extends BlockEntity> implements BlockEntityRenderer<S> {
    @Override
    public int getViewDistance() {
        return 512;
    }

    protected int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    public void renderPart(TIDynamicModel part, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        matrix.pushPose();
        matrix.translate(-.5, -.5, -.5);
        List<BakedQuad> quads = part.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.solid()), matrix, light, overlay);
        matrix.popPose();
    }

    public void renderPart(TIDynamicModel part, PoseStack matrix, MultiBufferSource buffer, int light, int overlay, RenderType rt) {
        matrix.pushPose();
        matrix.translate(-.5, -.5, -.5);
        List<BakedQuad> quads = part.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(rt), matrix, light, overlay);
        matrix.popPose();
    }

    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
        new EnumMap<>(Direction.class), m -> {
            for(Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX)
                m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180-facing.toYRot())));
        }
    );

    protected static void rotateForFacingNoCentering(PoseStack stack, Direction facing) {
        stack.mulPose(ROTATE_FOR_FACING.get(facing));
    }

    protected static void rotateForFacing(PoseStack stack, Direction facing) {
        stack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(stack, facing);
        stack.translate(-0.5, -0.5, -0.5);
    }
}
