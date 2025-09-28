package org.tcathebluecreper.totally_lib;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;
import org.tcathebluecreper.totally_lib.trait.ITrait;

public interface RenderFunction {
    void render(TraitMultiblockState state, MultiblockBlockEntityMaster<TraitMultiblockState> te, ITrait trait, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay);
}
