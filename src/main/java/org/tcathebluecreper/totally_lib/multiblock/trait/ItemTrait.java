package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class ItemTrait extends TLTrait {
    public final String name;

    public StoredCapability<ItemStackHandler> storage;

    public ItemTrait(String name, int stacks) {
        this.name = name;
        this.storage = new StoredCapability<>(new ItemStackHandler(stacks));
    }

    public ItemTrait(String name, List<ItemStack> startingStacks, int emptyStacks) {
        this.name = name;
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(startingStacks);
        for(int i = 0; i < emptyStacks; i++) list.add(ItemStack.EMPTY);
        this.storage = new StoredCapability<>(new ItemStackHandler(list));
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
}
