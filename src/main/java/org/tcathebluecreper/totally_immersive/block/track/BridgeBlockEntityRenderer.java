package org.tcathebluecreper.totally_immersive.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_lib.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;

public class BridgeBlockEntityRenderer extends TIBlockEntityRenderer<BridgeBlockEntity> {
    public static final String beamLocation = "bridge/wooden_beam";
    public static TIDynamicModel beam;
    public static final String beamHorizontalLocation = "bridge/wooden_beam_horizontal";
    public static TIDynamicModel beamHorizontal;
    @Override
    public void render(@NotNull BridgeBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        be.render(be, v, stack, buf, light, lightOverlay);
    }
    public static BlockPos invertBlockPos(BlockPos pos) {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    @Override
    public boolean shouldRenderOffScreen(BridgeBlockEntity p_112306_) {
        return true;
    }
}
