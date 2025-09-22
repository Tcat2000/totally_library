package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.rhino.annotations.JSFunction;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockState;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.Map;
import java.util.function.Consumer;
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
    void setOnValueChanged(Consumer<ITrait> consumer);

    default <T> T nullDefault(T main, T fallback) {
        if(main == null) return fallback;
        return main;
    }

    default boolean needsBER() {
        return false;
    }

    default void render(TraitMultiblockState state, MultiblockBlockEntityMaster<TraitMultiblockState> te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {}
}
