package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import dev.latvian.mods.rhino.annotations.JSFunction;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Map;
import java.util.function.Predicate;

public interface ITrait {
    String getName();
    void readSaveNBT(CompoundTag tag);
    void writeSaveNBT(CompoundTag tag);
    ITrait expose(BlockPos pos, Predicate<RelativeBlockFace> side);
    @RemapForJS("exposeSide")
    ITrait expose(BlockPos pos, RelativeBlockFace side);
    Capability<?> getCapType();
    StoredCapability<?> getCap();
    Map<BlockPos, Predicate<RelativeBlockFace>> getExposure();

    default <T> T nullDefault(T main, T fallback) {
        if(main == null) return fallback;
        return main;
    }
}
