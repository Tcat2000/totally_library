package org.tcathebluecreper.totally_immersive.mod.block;

import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class ScreenBlockEntityRenderer implements BlockEntityRenderer<ScreenBlockEntity> {
    RenderTarget rt = new TextureTarget(64, 64, false, Minecraft.ON_OSX);
    @Override
    public void render(ScreenBlockEntity be, float partialTick, PoseStack stack, MultiBufferSource bufferSource, int light, int lightOverlay) {
        stack.pushPose();
        stack.translate(0,2,0);
        new WidgetGroup().addWidget(new ImageWidget()).drawInForeground(new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource), 0, 0, 0);
        stack.popPose();
    }
}
