package org.tcathebluecreper.totally_immersive.api.NBT;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.phys.Vec3;

public class TINBTUtils {
    public static CompoundTag vec3ToTag(Vec3 vec) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vec.x);
        tag.putDouble("y", vec.y);
        tag.putDouble("z", vec.z);
        return tag;
    }
    public static Vec3 tagToVec(CompoundTag tag) {
        return new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }
    public static IntArrayTag blockPosToTag(BlockPos pos) {
        return new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }
    public static BlockPos tagToBlockPos(IntArrayTag tag) {
        return new BlockPos(tag.get(0).getAsInt(), tag.get(1).getAsInt(), tag.get(2).getAsInt());
    }
}
