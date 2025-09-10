package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.tcathebluecreper.totally_lib.lib.TIDynamicModel;

import java.util.List;
import java.util.function.Consumer;

public abstract class TIMultiblock extends IETemplateMultiblock {
    public final TIDynamicModel model;
    public final BlockPos masterFromOrigin;
    public final BlockPos triggerFromOrigin;
    public final BlockPos size;
    public TIMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic, TIDynamicModel manualModel) {
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
        model = manualModel;
        this.masterFromOrigin = masterFromOrigin;
        this.triggerFromOrigin = triggerFromOrigin;
        this.size = size;
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

    @Override
    public boolean createStructure(Level world, BlockPos pos, Direction side, Player player) {
        Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
        if (rot == null) {
            return false;
        } else {
            List<StructureTemplate.StructureBlockInfo> structure = this.getStructure(world);

            label29:
            for(Mirror mirror : this.getPossibleMirrorStates()) {
                StructurePlaceSettings placeSet = (new StructurePlaceSettings()).setMirror(mirror).setRotation(rot);
                BlockPos origin = pos.subtract(StructureTemplate.calculateRelativePosition(placeSet, this.triggerFromOrigin));

                for(StructureTemplate.StructureBlockInfo info : structure) {
                    BlockPos realRelPos = StructureTemplate.calculateRelativePosition(placeSet, info.pos());
                    BlockPos here = origin.offset(realRelPos);
                    BlockState expected = this.applyToState(info.state(), mirror, rot);
                    BlockState inWorld = world.getBlockState(here);
                    if (!BlockMatcher.matches(expected, inWorld, world, here, this.additionalPredicates).isAllow()) {
                        continue label29;
                    }
                }

                this.form(world, origin, rot, mirror, side);

                return true;
            }

            return false;
        }
    }
    private BlockState applyToState(BlockState in, Mirror m, Rotation r) {
        return in.mirror(m).rotate(r);
    }
    private List<Mirror> getPossibleMirrorStates() {
        return this.canBeMirrored() ? ImmutableList.of(Mirror.NONE, Mirror.FRONT_BACK) : ImmutableList.of(Mirror.NONE);
    }
}
