package org.tcathebluecreper.totally_immersive.block.track;

import net.minecraft.client.renderer.texture.Tickable;
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
import org.tcathebluecreper.totally_immersive.TIBlocks;
import org.tcathebluecreper.totally_immersive.api.NBT.TINBTUtils;

import java.util.Objects;

public class TrackBlockEntity extends BlockEntity {
    public TrackBlockEntity(BlockPos pos, BlockState state) {
        super(TIBlocks.TRACK_BLOCK_ENTITY.get(), pos, state);
    }

    public Vec3 localVector;
    public BlockPos target;
    public Vec3 targetVector;

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("LocalVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(localVector, new Vec3(0,0,0))));
        tag.put("TargetVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(targetVector, new Vec3(0,0,0))));
        tag.put("TargetPos", TINBTUtils.blockPosToTag(Objects.requireNonNullElse(target, getBlockPos())));
    }

    @Override
    public void load(CompoundTag nbt) {
        localVector = TINBTUtils.tagToVec(nbt.getCompound("LocalVector"));
        target = TINBTUtils.tagToBlockPos((IntArrayTag) nbt.get("TargetPos"));
        targetVector = TINBTUtils.tagToVec(nbt.getCompound("TargetVector"));
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("LocalVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(localVector, new Vec3(0,0,0))));
        tag.put("TargetVector", TINBTUtils.vec3ToTag(Objects.requireNonNullElse(targetVector, new Vec3(0,0,0))));
        tag.put("TargetPos", TINBTUtils.blockPosToTag(Objects.requireNonNullElse(target, getBlockPos())));
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
