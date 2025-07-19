package org.tcathebluecreper.totally_immersive.mod.block.track;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.tcathebluecreper.totally_immersive.api.RenderablePart;
import org.tcathebluecreper.totally_immersive.mod.block.TIBlocks;
import org.tcathebluecreper.totally_immersive.api.NBT.TINBTUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TrackBlockEntity extends BlockEntity {
    public TrackBlockEntity(BlockPos pos, BlockState state) {
        super(TIBlocks.TRACK_BLOCK_ENTITY.get(), pos, state);
    }

    public Vec3 localVector;
    public BlockPos targetPos;
    public Vec3 targetVector;

    public boolean constructed = false;
    public boolean needUpdate = true;

    public boolean previewForceBallast = false;
    public Float previewMinBallastHeight = null;

    public Map<BlockPos, BlockState> renderBlocks;
    public List<RenderablePart> renderTies;
    public List<RenderablePart> renderRails;

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
}
