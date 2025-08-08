package org.tcathebluecreper.totally_immersive.api.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.tcathebluecreper.totally_immersive.api.lib.TIDynamicModel;

import java.util.List;
import java.util.function.Consumer;

public abstract class TIMultiblock extends IETemplateMultiblock {
    public final TIDynamicModel model;
    public  TIMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic, TIDynamicModel manualModel) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
        model = manualModel;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        /// copied from Immersive Industry
        consumer.accept(new BasicClientProperties(this) {
            @Override
            public void renderFormedStructure(PoseStack transform, MultiBufferSource bufferSource) {
                transform.pushPose();
                BlockPos offset = getMasterFromOriginOffset();
                transform.translate(offset.getX(), offset.getY(), offset.getZ());
                List<BakedQuad> nullQuads = model.getNullQuads();
                VertexConsumer buffer = bufferSource.getBuffer(IERenderTypes.TRANSLUCENT_FULLBRIGHT);
                nullQuads.forEach(quad -> buffer.putBulkData(
                    transform.last(), quad, 1, 1, 1, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY
                ));
                transform.popPose();
            }
        });
    }
}
