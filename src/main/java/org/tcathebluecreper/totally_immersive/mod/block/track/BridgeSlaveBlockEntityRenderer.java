package org.tcathebluecreper.totally_immersive.mod.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BridgeSlaveBlockEntityRenderer implements BlockEntityRenderer<BridgeSlaveBlockEntity> {
    @Override
    public void render(BridgeSlaveBlockEntity be, float partialTick, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        BlockPos corePos = new BlockPos(130, 78, -652);
        BlockEntity core = be.getLevel().getBlockEntity(corePos);
        if(!(core instanceof BridgeBlockEntity)) return;
        stack.pushPose();
        BlockPos offset = corePos.subtract(be.getBlockPos());
        stack.translate(offset.getX(), offset.getY(), offset.getZ());
        ((BridgeBlockEntity) core).render((BridgeBlockEntity) core, partialTick, stack, buf, light, lightOverlay);
        stack.popPose();
    }
}
