package org.tcathebluecreper.totally_immersive.mod.Multiblock.rotay_kiln;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

public class RotaryKilnRenderer extends TIBlockEntityRenderer<MultiblockBlockEntityMaster<RotaryKilnState>> {
    public static final String barrelId = "rotary_kiln/barrel";
    public static TIDynamicModel barrel;

    @Override
    public void render(MultiblockBlockEntityMaster<RotaryKilnState> be, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int lightOverlay) {
        rotateForFacing(stack, be.getBlockState().getValue(IEProperties.FACING_HORIZONTAL));
        stack.translate(0.5, 1.5, 0.8f);
        switch(be.getBlockState().getValue(IEProperties.FACING_HORIZONTAL)) {
            case NORTH -> {
                stack.rotateAround(new Quaternionf().rotateAxis(-3.54f * Mth.DEG_TO_RAD, 1,0,0),0,0, 0);
                stack.rotateAround(new Quaternionf().rotateAxis((be.getLevel().getGameTime() + partialTick) * Mth.DEG_TO_RAD * 3, 0,0,1),0,3, 0);
            }
            case SOUTH -> stack.rotateAround(new Quaternionf().rotateAxis(3.54f * Mth.DEG_TO_RAD, 1,0,0),0,0, 0);
            case EAST -> stack.rotateAround(new Quaternionf().rotateAxis(-3.54f * Mth.DEG_TO_RAD, 0,0,1),0,0, 0);
            case WEST -> stack.rotateAround(new Quaternionf().rotateAxis(3.54f * Mth.DEG_TO_RAD, 0,0,1),0,0, 0);
        }

        renderPart(barrel, stack, bufferSource, light, lightOverlay);
    }
}
