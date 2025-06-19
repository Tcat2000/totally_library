package org.tcathebluecreper.totally_immersive.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.TIRenderTypes;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

public class BridgeBlockEntityRenderer extends TIBlockEntityRenderer<BridgeBlockEntity> {
    public static final String beamLocation = "bridge/wooden_beam";
    public static TIDynamicModel beam;
    @Override
    public void render(@NotNull BridgeBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        if(be.targetPos == null || be.targetVector == null || be.localVector == null) return;
        stack.pushPose();

        if(!be.constructed && be.renderBlocks != null) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            be.renderBlocks.forEach((pos, state) -> {
                if(state == null) return;
                stack.pushPose();
                BlockPos offset = pos.subtract(be.getBlockPos());
                stack.translate(offset.getX(), offset.getY(), offset.getZ());
                dispatcher.renderSingleBlock(state, stack, buf, 100, lightOverlay, ModelData.builder().with(new ModelProperty<>(), 2).build(), TIRenderTypes.blueprint());
                stack.popPose();
            });
        }

        stack.translate(0.5,0.5,0.5);

        if(!(be.renderBeams == null)) {
            be.renderBeams.forEach(data -> {
                data.render(this, beam, stack, buf, light, lightOverlay, TIRenderTypes.blueprint());
            });
        }
        stack.popPose();
    }
    public static BlockPos invertBlockPos(BlockPos pos) {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }
}
