package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

public class ChemicalBathRenderer extends TIBlockEntityRenderer<MultiblockBlockEntityMaster<ChemicalBathState>> {
    public static final String craneTopId = "chemical_bath/top";
    public static TIDynamicModel craneTop;
    public static final String craneMiddleId = "chemical_bath/middle";
    public static TIDynamicModel craneMiddle;
    public static final String craneBottomId = "chemical_bath/bottom";
    public static TIDynamicModel craneBottom;

    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    @Override
    public void render(MultiblockBlockEntityMaster<ChemicalBathState> te, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        ChemicalBathState state = te.getHelper().getContext().getState();
//        int mirrored = te.getBlockState().getValue(IEProperties.MIRRORED) ? -1 : 1;
//        int progress = 5;
//        matrixStack.pushPose();
//        rotateForFacing(matrixStack, te.getBlockState().getValue(IEProperties.FACING_HORIZONTAL));
//        matrixStack.translate(0.5,0.5,0.5);
//
//
//        if(progress == -1) matrixStack.translate(AnimationUtils.lerp(0,3, AnimationUtils.amount(state.process.resetCooldown - pPartialTick, state.process.RESET_TIME)) * mirrored, 0, 0);
//        else if(progress >= 20 && progress <= 120) {
////            System.out.println((progress - 20 + (pPartialTick)) + ", " + AnimationUtils.lerp(0,3, AnimationUtils.amount(progress - 20 + (pPartialTick), state.process.PROCESS_TIME - 40)));
//            matrixStack.translate(AnimationUtils.lerp(0,3, AnimationUtils.amount(progress - 20 + (pPartialTick), state.process.PROCESS_TIME - 40)) * mirrored, 0, 0);
//        }
//        else if(progress > 120) matrixStack.translate(3 * mirrored, 0, 0);
//        renderPart(craneTop, matrixStack, pBuffer, pPackedLight, pPackedOverlay);
//
//        float lowerDist = 0;
//        {
//            if(progress >= 40 && progress <= 55) {
//                lowerDist = -AnimationUtils.lerp(0,0.8f, AnimationUtils.amount(progress - 40 + pPartialTick, 16));
//            }
//            else if(progress >= 80 && progress <= 95) {
//                lowerDist = -AnimationUtils.lerp(0,0.8f, 1 - AnimationUtils.amount(progress - 80 + pPartialTick, 16));
//            }
//            else if(progress >= 55 && progress <= 80) lowerDist = -0.8f;
//            else if(progress >= 0) {
//                lowerDist = 0;
//            }
////            else if(progress == -1) lowerDist = 0;
//            if(progress >= 120) {
//                if(progress <= 130) lowerDist = -AnimationUtils.lerp(0, 0.5f, AnimationUtils.amount(progress - 120, 10));
//                else lowerDist = -AnimationUtils.lerp(0, 0.5f, 1 - AnimationUtils.amount(progress - 130, 10));
//            }
//            if(progress <= 20 && progress >= 0) {
//                if(progress <= 10) lowerDist = -AnimationUtils.lerp(0, 0.5f, AnimationUtils.amount(progress, 10));
//                else lowerDist = -AnimationUtils.lerp(0, 0.5f, 1 - AnimationUtils.amount(progress - 10, 10));
//            }
//            if(progress == -2) lowerDist = 0;
//        }
//        matrixStack.translate(0, lowerDist, 0);
//        renderPart(craneBottom, matrixStack, pBuffer, pPackedLight, pPackedOverlay);
//
//        matrixStack.pushPose();
//        matrixStack.scale(0.5f, 0.5f ,0.5f);
//        matrixStack.translate(0,1.5,0);
//        if(progress >= 10 && progress <= 130) itemRenderer.renderStatic(state.processSlot.getValue().getStackInSlot(0), ItemDisplayContext.FIXED, getLightLevel(te.getLevel(), te.getBlockPos()), OverlayTexture.NO_OVERLAY, matrixStack, pBuffer, te.getLevel(), 1);
//        matrixStack.popPose();
//
//
//        renderPart(craneMiddle, matrixStack, pBuffer, pPackedLight, pPackedOverlay);
//        while(lowerDist <= 1/16f) {
//            renderPart(craneMiddle, matrixStack, pBuffer, pPackedLight, pPackedOverlay);
//            matrixStack.translate(0, 1/16f,0);
//            lowerDist += 1/16f;
//        }
//
//        matrixStack.popPose();
//
//        if(state.tank.getFluid().isEmpty()) return;
//
//        matrixStack.pushPose();
//        rotateForFacing(matrixStack, te.getBlockState().getValue(IEProperties.FACING_HORIZONTAL));
////        VertexConsumer consumer = pBuffer.getBuffer(RenderType.endPortal());
////
////        consumer.vertex(0, 5, 0);
////        consumer.vertex(0, 5, 1);
////        consumer.vertex(1, 5, 0);
////        consumer.vertex(1, 5, 1);
////
////        consumer.putBulkData(matrixStack, new BakedQuad());
//        float fillLevel = state.tank.getFluidAmount() / 8000f;
//        matrixStack.translate(1.0, 6/16f, -7/16f); // Translate to the top plane of the block (Y=1)
//        matrixStack.translate(0, fillLevel,0);
//        float width = AnimationUtils.lerp(1/16f, 6/16f, state.tank.getFluidAmount() / 4000f);
//
//        // --- 3. Get VertexConsumer ---
//        // Use RenderType.cutout() for textures with transparency but no blending issues
//        // Use RenderType.solid() for opaque textures
//        // Use RenderType.translucent() for alpha blending (can have sorting issues)
//        VertexConsumer consumer = pBuffer.getBuffer(Sheets.translucentCullBlockSheet()); // Use cutoutMipped for mipmapping
//
//        Fluid fluid = state.tank.getFluid().getFluid();
//        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid);
//        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture());
//
//        int color = IClientFluidTypeExtensions.of(fluid).getTintColor(fluid.defaultFluidState(), te.getLevel(), te.getBlockPos());
//
//        if(mirrored == -1) matrixStack.translate(-3,0,0);
//        renderQuad(matrixStack, color, texture, consumer, pPackedLight, 0, 1, 1, 0);
//        renderQuad(matrixStack, color, texture, consumer, pPackedLight, 1, 1, 2, 0);
//        matrixStack.translate(0,0,1);
//        renderQuad(matrixStack, color, texture, consumer, pPackedLight, 0, width, 1, 0, texture.getU0(), texture.getV0() - (texture.getV1() - texture.getV0()) * (width - 1), texture.getU1(), texture.getV1());
//        renderQuad(matrixStack, color, texture, consumer, pPackedLight, 1, width, 2, 0, texture.getU0(), texture.getV0() - (texture.getV1() - texture.getV0()) * (width - 1), texture.getU1(), texture.getV1());
//
//        matrixStack.popPose();
    }
    private void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2) {
        renderQuad(matrixStack, color, texture, consumer, pPackedLight, x1, z1, x2, z2,
            texture.getU0(),
            texture.getV0(),
            texture.getU1(),
            texture.getV1()
        );
    }
    private void renderQuad(PoseStack matrixStack, int color, TextureAtlasSprite texture, VertexConsumer consumer, int pPackedLight, float x1, float z1, float x2, float z2, float minU, float minV, float maxU, float maxV) {
        float y = 0.0f;

        // Normals for a face pointing upwards (Y-axis)
        float normalX = 0.0f;
        float normalY = 1.0f; // Points straight up
        float normalZ = 0.0f;

        // Get the current transformation matrices from the PoseStack
        Matrix4f modelViewMatrix = matrixStack.last().pose();
        Matrix3f normalMatrix = matrixStack.last().normal();

        // Vertex 1: Bottom-Left (relative to quad)
        consumer.vertex(modelViewMatrix, x1, y, z1)
                .color(color) // Full white, opaque
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight) // Light level
                .normal(normalMatrix, normalX, normalY, normalZ)
                .endVertex();

        // Vertex 2: Bottom-Right
        consumer.vertex(modelViewMatrix, x2, y, z1)
                .color(color)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight)
                .normal(normalMatrix, normalX, normalY, normalZ)
                .endVertex();

        // Vertex 3: Top-Right
        consumer.vertex(modelViewMatrix, x2, y, z2)
                .color(color)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight)
                .normal(normalMatrix, normalX, normalY, normalZ)
                .endVertex();

        // Vertex 4: Top-Left
        consumer.vertex(modelViewMatrix, x1, y, z2)
                .color(color)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight)
                .normal(normalMatrix, normalX, normalY, normalZ)
                .endVertex();
    }
}
