package org.tcathebluecreper.totally_immersive.Multiblock;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.render.tile.CrusherRenderer;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.lib.TIDynamicModel;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ChemicalBathRenderer implements BlockEntityRenderer<MultiblockBlockEntityMaster<ChemicalBathState>> {
    public static final String craneTopId = "chemical_bath_top";
    public static TIDynamicModel craneTop;
    public static final String craneMiddleId = "chemical_bath_middle";
    public static TIDynamicModel craneMiddle;
    public static final String craneBottomId = "chemical_bath_bottom";
    public static TIDynamicModel craneBottom;
    @Override
    public void render(MultiblockBlockEntityMaster<ChemicalBathState> te, float pPartialTick, PoseStack matrixStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        System.out.println(te);
//        Direction d=te.getHelper().getContext().getLevel().getOrientation().front().getOpposite();
//        BlockPos lightPos=te.getBlockPos().above(2);
//        matrixStack.pushPose();
//        List<BakedQuad> quads = craneTop.getNullQuads();
//        int calculatedLight = LevelRenderer.getLightColor(te.getLevel(), lightPos);
//        matrixStack.translate(3,2,1);
//        RenderUtils.renderModelTESRFast(quads, pBuffer.getBuffer(RenderType.solid()), matrixStack, calculatedLight, pPackedOverlay);
//        matrixStack.popPose();
        System.out.println(craneTop);
        matrixStack.pushPose();
        rotateForFacing(matrixStack, te.getBlockState().getValue(IEProperties.FACING_HORIZONTAL));
        matrixStack.translate(0,2,0);
        renderPart(craneTop, matrixStack, pBuffer, Direction.NORTH, pPackedLight, pPackedOverlay);
        matrixStack.popPose();
    }
    private void renderPart(TIDynamicModel barrel, PoseStack matrix, MultiBufferSource buffer, Direction facing, int light, int overlay) {
        matrix.pushPose();
        matrix.translate(-.5, -.5, -.5);
        List<BakedQuad> quads = barrel.get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, ModelData.EMPTY, null);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.solid()), matrix, light, overlay);
        matrix.popPose();
    }
    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
            new EnumMap<>(Direction.class), m -> {
                for(Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX)
                    m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180-facing.toYRot())));
            }
    );
    protected static void rotateForFacingNoCentering(PoseStack stack, Direction facing)
    {
        stack.mulPose(ROTATE_FOR_FACING.get(facing));
    }
    protected static void rotateForFacing(PoseStack stack, Direction facing)
    {
        stack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(stack, facing);
        stack.translate(-0.5, -0.5, -0.5);
    }
}
