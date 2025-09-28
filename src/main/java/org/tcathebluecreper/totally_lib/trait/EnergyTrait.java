package org.tcathebluecreper.totally_lib.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;
import org.tcathebluecreper.totally_lib.RenderFunction;
import org.tcathebluecreper.totally_lib.multiblock.TLTraitMultiblockState;

import java.util.function.Consumer;

public class EnergyTrait extends TLTrait<EnergyStorage> {
    public final String name;
    private final RenderFunction renderFunction;

    StoredCapability<EnergyStorage> storage;

    public EnergyTrait(String name, int maxPower) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, 1000));
        renderFunction = null;
    }

    public EnergyTrait(String name, int maxPower, int maxTransfer) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxTransfer));
        renderFunction = null;
    }

    public EnergyTrait(String name, int maxPower, int maxInsert, int maxExtract) {
        this.name = name;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxInsert, maxExtract));
        renderFunction = null;
    }

    public EnergyTrait(String name, int maxPower, RenderFunction renderFunction) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, 1000));
    }

    public EnergyTrait(String name, int maxPower, int maxTransfer, RenderFunction renderFunction) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxTransfer));
    }

    public EnergyTrait(String name, int maxPower, int maxInsert, int maxExtract, RenderFunction renderFunction) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new EnergyStorage(maxPower, maxInsert, maxExtract));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        if(tag.contains(getName())) storage.getValue().deserializeNBT(tag.get(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), storage.getValue().serializeNBT());
    }

    @Override
    public StoredCapability<?> getCap() {
        return storage;
    }

    @Override
    public void setOnValueChanged(Consumer<ITrait> consumer) {

    }

    @Override
    public boolean needsBER() {
        return renderFunction != null;
    }

    @Override
    public void render(TLTraitMultiblockState state, MultiblockBlockEntityMaster<TLTraitMultiblockState> te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderFunction.render(state, te, this, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
