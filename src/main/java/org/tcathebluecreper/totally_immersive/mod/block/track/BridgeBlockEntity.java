package org.tcathebluecreper.totally_immersive.mod.block.track;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.api.NBT.TINBTUtils;
import org.tcathebluecreper.totally_immersive.api.RenderablePart;
import org.tcathebluecreper.totally_immersive.api.shapes.CompoundVoxelShape;
import org.tcathebluecreper.totally_immersive.mod.TIRenderTypes;
import org.tcathebluecreper.totally_immersive.mod.TIBlocks;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.tcathebluecreper.totally_immersive.mod.block.track.BridgeBlockEntityRenderer.beam;
import static org.tcathebluecreper.totally_immersive.mod.block.track.BridgeBlockEntityRenderer.beamHorizontal;

public class BridgeBlockEntity extends BlockEntity {
    CompoundVoxelShape blockShape = new CompoundVoxelShape();
    public BridgeBlockEntity(BlockPos pos, BlockState state) {
        super(TIBlocks.BRIDGE_BLOCK_ENTITY.get(), pos, state);
    }

    public Vec3 localVector;
    public BlockPos targetPos;
    public Vec3 targetVector;

    public boolean constructed = false;
    public boolean needUpdate = true;
    public Map<BlockPos, BlockState> renderBlocks;
    public List<RenderablePart> renderBeams;
    public List<RenderablePart> renderBeamsHorizontal;

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("LocalVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(localVector, new Vec3(0,0,0))));
        tag.put("TargetVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(targetVector, new Vec3(0,0,0))));
        tag.put("TargetPos", TINBTUtils.blockPosToTag(Objects.requireNonNullElse(targetPos, getBlockPos())));
        tag.putBoolean("Constructed", constructed);
    }

    @Override
    public void load(CompoundTag nbt) {
        localVector = TINBTUtils.tagToVec(nbt.getCompound("LocalVector"));
        targetPos = TINBTUtils.tagToBlockPos((IntArrayTag) nbt.get("TargetPos"));
        targetVector = TINBTUtils.tagToVec(nbt.getCompound("TargetVector"));
        constructed = nbt.getBoolean("Constructed");
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("LocalVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(localVector, new Vec3(0,0,0))));
        tag.put("TargetVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(targetVector, new Vec3(0,0,0))));
        tag.put("TargetPos", TINBTUtils.blockPosToTag(Objects.requireNonNullElse(targetPos, getBlockPos())));
        tag.putBoolean("Constructed", constructed);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private float lastRenderedFrameTime = 0;
    @OnlyIn(Dist.CLIENT)
    public void render(@NotNull BridgeBlockEntity be, float v, PoseStack stack, MultiBufferSource buf, int light, int lightOverlay) {
        if(lastRenderedFrameTime != Minecraft.getInstance().getFrameTime()) {
            lastRenderedFrameTime = Minecraft.getInstance().getFrameTime();

            if(be.targetPos == null || be.targetVector == null || be.localVector == null) return;
            stack.pushPose();

            if(!be.constructed && be.renderBlocks != null) {
                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                be.renderBlocks.forEach((pos, state) -> {
                    if(state == null) return;
                    stack.pushPose();
                    BlockPos offset = pos.subtract(be.getBlockPos());
                    stack.translate(offset.getX(), offset.getY(), offset.getZ());
                    dispatcher.renderSingleBlock(state, stack, buf, 100, lightOverlay, ModelData.builder().with(new ModelProperty<>(), 2).build(), be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
                    stack.popPose();
                });
            }

            stack.translate(0.5,0.5,0.5);

            if(!(be.renderBeams == null)) {
                be.renderBeams.forEach(data -> {
                    data.render(beam, stack, buf, 100, 100, be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
                });
            }
            if(!(be.renderBeamsHorizontal == null)) {
                be.renderBeamsHorizontal.forEach(data -> {
                    data.render(beamHorizontal, stack, buf, 100, 100, be.constructed ? RenderType.solid() : TIRenderTypes.blueprint());
                });
            }
            stack.popPose();
        }
    }
}
