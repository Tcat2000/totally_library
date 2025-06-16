package org.tcathebluecreper.totally_immersive.block.track;

import com.lowdragmc.lowdraglib.client.renderer.impl.BlockStateRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.tcathebluecreper.totally_immersive.api.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_immersive.api.TIMath;
import org.tcathebluecreper.totally_immersive.lib.AnimationUtils;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

import static org.tcathebluecreper.totally_immersive.block.track.BallastBlock.NE_FILL;

public class TrackBlockEntityRenderer extends TIBlockEntityRenderer<TrackBlockEntity> {
    public static final String tieLocation = "track/tie";
    public static TIDynamicModel tie;
    public static final String railLocation = "track/rail";
    public static TIDynamicModel rail;
    @Override
    public void render(@NotNull TrackBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        if(be.target == null || be.targetVector == null || be.localVector == null) return;
        stack.pushPose();
        VertexConsumer consumer = buf.getBuffer(RenderType.lines());


        BlockPos pos0 = new BlockPos(0,0,0);
        BlockPos pos1 = be.target.offset(invertBlockPos(be.getBlockPos()));
        Vec3 vector0 = be.localVector;
        Vec3 vector1 = be.targetVector;

        if(!be.constructed && be.renderBlocks != null) {
            BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            be.renderBlocks.forEach((pos, state) -> {
                if(state == null) return;
                stack.pushPose();
                BlockPos offset = pos.subtract(be.getBlockPos());
                stack.translate(offset.getX(), offset.getY(), offset.getZ());
                dispatcher.renderSingleBlock(state, stack, buf, 100, lightOverlay, ModelData.builder().with(new ModelProperty<>(), 2).build(), RenderType.solid());
                stack.popPose();
            });
        }

        stack.translate(0.5,0.5,0.5);

        if(!(be.renderRails == null || be.renderTies == null)) {
            be.renderTies.forEach(data -> {
                data.render(this, tie, stack, buf, light, lightOverlay);
            });
            be.renderRails.forEach(data -> {
                data.render(this, rail, stack, buf, light, lightOverlay);
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

        public void render(TIBlockEntityRenderer<?> context, TIDynamicModel part, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
            stack.pushPose();
            stack.translate(pos.x, pos.y, pos.z);
            stack.mulPose(rot);
            stack.translate(pos2.x, pos2.y, pos2.z);
            stack.scale((float) scale.x, (float) scale.y, (float) scale.z);
            context.renderPart(part, stack, buf, light, lightOverlay);
            stack.popPose();
        }
    }
}
