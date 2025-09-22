package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.rhino.annotations.JSConstructor;
import dev.latvian.mods.rhino.annotations.JSFunction;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.tcathebluecreper.totally_lib.RenderFunction;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FluidTrait extends TLTrait<FluidTank> {
    public final String name;
    private final RenderFunction renderFunction;
    private Consumer<ITrait> onValueChanged;

    StoredCapability<FluidTank> storage;

    public FluidTrait(String name, int capacity) {
        this.name = name;
        this.storage = new StoredCapability<>(new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                if(onValueChanged != null) onValueChanged.accept(FluidTrait.this);
            }
        });
        renderFunction = null;
    }

    public FluidTrait(String name, int capacity, Predicate<FluidStack> filter) {
        this.name = name;
        this.storage = new StoredCapability<>(new FluidTank(capacity, filter) {
            @Override
            protected void onContentsChanged() {
                if(onValueChanged != null) onValueChanged.accept(FluidTrait.this);
            }
        });
        renderFunction = null;
    }

    public FluidTrait(RenderFunction renderFunction, String name, int capacity) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                if(onValueChanged != null) onValueChanged.accept(FluidTrait.this);
            }
        });
    }

    @JSConstructor
    public FluidTrait(RenderFunction renderFunction, String name, int capacity, Predicate<FluidStack> filter) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new FluidTank(capacity, filter));
    }

    @HideFromJS
    public void setOnValueChanged(Consumer<ITrait> consumer) {
        onValueChanged = consumer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        if(tag.contains(getName())) storage.getValue().readFromNBT(tag.getCompound(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), new CompoundTag());
        storage.getValue().writeToNBT(tag.getCompound(getName()));
    }

    @Override
    public Capability<?> getCapType() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    @HideFromJS
    @Override
    public StoredCapability<?> getCap() {
        return storage;
    }

    public FluidTank getValue() { return storage.getValue(); }

    @Override
    public boolean needsBER() {
        return renderFunction != null;
    }

    @Override
    public void render(TraitMultiblockState state, MultiblockBlockEntityMaster<TraitMultiblockState> te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderFunction.render(state, te, this, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
