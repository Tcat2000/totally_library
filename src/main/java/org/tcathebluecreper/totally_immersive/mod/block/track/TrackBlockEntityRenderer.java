package org.tcathebluecreper.totally_immersive.mod.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.mod.TIRenderTypes;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

import static org.tcathebluecreper.totally_immersive.mod.TotallyImmersive.MODID;

public class TrackBlockEntityRenderer extends TIBlockEntityRenderer<TrackBlockEntity> {
    public static final String tieLocation = "track/tie";
    public static TIDynamicModel tie;
    public static final String railLocation = "track/rail";
    public static TIDynamicModel rail;
    @Override
    public void render(@NotNull TrackBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        if(be.targetPos == null || be.targetVector == null || be.localVector == null) return;
        stack.pushPose();

        if((!be.constructed || be.previewForceBallast) && be.renderBlocks != null) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            be.renderBlocks.forEach((pos, state) -> {
                if(state == null) return;
                stack.pushPose();
                BlockPos offset = pos.subtract(be.getBlockPos());
                stack.translate(offset.getX(), offset.getY(), offset.getZ());
                dispatcher.renderSingleBlock(state, stack, buf, 100, 100, ModelData.builder().with(new ModelProperty<>(), 2).build(), be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
                stack.popPose();
            });
        }

        stack.translate(0.5,0.5,0.5);

        if(!(be.renderRails == null || be.renderTies == null)) {
            be.renderTies.forEach(data -> {
                data.render(tie, stack, buf, 100, 100, be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
            });
            be.renderRails.forEach(data -> {
                data.render(rail, stack, buf, 100, 100, be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
            });
        }
        stack.popPose();
    }
    public static BlockPos invertBlockPos(BlockPos pos) {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public static class RenderableTrackPart {
        public final Vec3 pos;
        public final Vec3 pos2;
        public final Quaternionf rot;
        public final Vec3 scale;

        public RenderableTrackPart(Vec3 pos, Quaternionf rot, Vec3 pos2, Vec3 scale) {
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
//            stack.scale((float) scale.x, (float) scale.y, (float) scale.z);

            VertexConsumer consumer = buf.getBuffer(Sheets.translucentCullBlockSheet());
            TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "block/wooden_decoration/treated_wood"));

            renderQuad(stack, 0, texture, consumer, light, 0, 0, 1, 1, texture.getU0(), texture.getV0() - (texture.getV1() - texture.getV0()), texture.getU1(), texture.getV1());

            stack.popPose();
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
}
