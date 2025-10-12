package org.tcathebluecreper.totally_lib.client;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_lib.multiblock.TLTraitMultiblockState;
import org.tcathebluecreper.totally_lib.trait.ITrait;

import java.util.EnumMap;
import java.util.Map;

public class TLRenderHelper {
    public final TLTraitMultiblockState state;
    public final MultiblockBlockEntityMaster<TLTraitMultiblockState> blockEntity;
    public final ITrait trait;
    public final float partialTick;
    public final PoseStack poseStack;
    public final MultiBufferSource bufferSource;
    public final int packedLight;
    public final int packedOverlay;

    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
        new EnumMap<>(Direction.class), m -> {
            for(Direction facing : DirectionUtils.BY_HORIZONTAL_INDEX)
                m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180-facing.toYRot())));
        }
    );

    public TLRenderHelper(TLTraitMultiblockState state, MultiblockBlockEntityMaster<TLTraitMultiblockState> blockEntity, ITrait trait, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.state = state;
        this.blockEntity = blockEntity;
        this.trait = trait;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    private void rotateForFacingNoCentering(PoseStack stack, Direction facing) {
        stack.mulPose(ROTATE_FOR_FACING.get(facing));
    }

    public void rotateForFacing() {
        poseStack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(poseStack, blockEntity.getBlockState().getValue(IEProperties.FACING_HORIZONTAL));
        poseStack.translate(-0.5, -0.5, -0.5);
    }

    public boolean isMirrored() {
        return blockEntity.getBlockState().getValue(IEProperties.MIRRORED);
    }

    public int getMirror() {
        return isMirrored() ? -1 : 1;
    }




    public VertexConsumer getSolidRenderer() {
        return bufferSource.getBuffer(Sheets.solidBlockSheet());
    }
    public VertexConsumer getTranslucentRenderer() {
        return bufferSource.getBuffer(Sheets.translucentCullBlockSheet());
    }
    public ResourceLocation getFluidStillTexture(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getStillTexture();
    }
    public ResourceLocation getFluidFlowingTexture(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getFlowingTexture();
    }
    public int getFluidTintTexture(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getTintColor(fluid.defaultFluidState(), blockEntity.getLevel(), blockEntity.getBlockPos());
    }
}
