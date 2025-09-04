package org.tcathebluecreper.totally_lib.multiblock.trait;

import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Predicate;

public class FluidTrait extends TLTrait {
    public final String name;

    StoredCapability<FluidTank> storage;

    public FluidTrait(String name, int capacity) {
        this.name = name;
        this.storage = new StoredCapability<>(new FluidTank(capacity));
    }

    public FluidTrait(String name, int capacity, Predicate<FluidStack> filter) {
        this.name = name;
        this.storage = new StoredCapability<>(new FluidTank(capacity, filter));
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

    @Override
    public StoredCapability<?> getCap() {
        return storage;
    }
}
