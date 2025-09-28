package org.tcathebluecreper.totally_lib.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.tcathebluecreper.totally_lib.RenderFunction;
import org.tcathebluecreper.totally_lib.crafting.RangedDetectorWrapper;
import org.tcathebluecreper.totally_lib.multiblock.TraitMultiblockState;

import java.util.List;
import java.util.function.Consumer;

public class ItemTrait extends TLTrait<RangedDetectorWrapper> {
    public final String name;
    private final RenderFunction renderFunction;

    public StoredCapability<ItemStackHandler> storage;
    public RangedDetectorWrapper wrapper;

    public ItemTrait(String name, int stacks) {
        this.name = name;
        this.storage = new StoredCapability<>(new ItemStackHandler(stacks));
        renderFunction = null;
        wrapper = new RangedDetectorWrapper(storage.getValue(), 0, storage.getValue().getSlots());
    }

    public ItemTrait(String name, List<ItemStack> startingStacks, int emptyStacks) {
        this.name = name;
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(startingStacks);
        for(int i = 0; i < emptyStacks; i++) list.add(ItemStack.EMPTY);
        this.storage = new StoredCapability<>(new ItemStackHandler(list));
        renderFunction = null;
        wrapper = new RangedDetectorWrapper(storage.getValue(), 0, storage.getValue().getSlots());
    }

    public ItemTrait(String name, RenderFunction renderFunction, int stacks) {
        this.name = name;
        this.renderFunction = renderFunction;
        this.storage = new StoredCapability<>(new ItemStackHandler(stacks));
        wrapper = new RangedDetectorWrapper(storage.getValue(), 0, storage.getValue().getSlots());
    }

    public ItemTrait(String name, List<ItemStack> startingStacks, int emptyStacks, RenderFunction renderFunction) {
        this.name = name;
        this.renderFunction = renderFunction;
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(startingStacks);
        for(int i = 0; i < emptyStacks; i++) list.add(ItemStack.EMPTY);
        this.storage = new StoredCapability<>(new ItemStackHandler(list));
        wrapper = new RangedDetectorWrapper(storage.getValue(), 0, storage.getValue().getSlots());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        if(tag.contains(getName())) storage.getValue().deserializeNBT(tag.getCompound(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), storage.getValue().serializeNBT());
    }

    @Override
    public Capability<?> getCapType() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public StoredCapability<?> getCap() {
        return storage;
    }

    @Override
    public RangedDetectorWrapper get() {
        return wrapper;
    }

    @Override
    public void setOnValueChanged(Consumer<ITrait> consumer) {
        wrapper.setListener((handler, stack) -> consumer.accept(this));
    }

    @Override
    public boolean needsBER() {
        return renderFunction != null;
    }

    @Override
    public void render(TraitMultiblockState state, MultiblockBlockEntityMaster<TraitMultiblockState> te, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderFunction.render(state, te, this, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
