package org.tcathebluecreper.totally_immersive.mod.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.tcathebluecreper.totally_immersive.mod.TIRenderTypes;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

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
