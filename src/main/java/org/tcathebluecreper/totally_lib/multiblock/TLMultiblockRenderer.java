package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.tcathebluecreper.totally_lib.TIBlockEntityRenderer;
import org.tcathebluecreper.totally_lib.trait.ITrait;

public class TLMultiblockRenderer extends TIBlockEntityRenderer<MultiblockBlockEntityMaster<TLTraitMultiblockState>> {
    @Override
    public void render(MultiblockBlockEntityMaster<TLTraitMultiblockState> pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        TLTraitMultiblockState state = pBlockEntity.getHelper().getContext().getState();

        for(ITrait trait : state.traits) {
            if(trait.needsBER()) trait.render(state, pBlockEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }
}
