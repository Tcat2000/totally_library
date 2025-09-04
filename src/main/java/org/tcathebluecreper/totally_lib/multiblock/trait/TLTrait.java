package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class TLTrait implements ITrait {
    private Map<BlockPos, Predicate<RelativeBlockFace>> exposure = new HashMap<>();
    @Override
    public ITrait expose(BlockPos pos, Predicate<RelativeBlockFace> side) {
        exposure.put(pos, side);
        return this;
    }

    @Override
    public ITrait expose(BlockPos pos, RelativeBlockFace side) {
        exposure.put(pos, s -> s == side);
        return this;
    }

    @Override
    public Map<BlockPos, Predicate<RelativeBlockFace>> getExposure() {
        return exposure;
    }

    @Override
    public Capability<?> getCapType() {
        return ForgeCapabilities.ENERGY;
    }
}
