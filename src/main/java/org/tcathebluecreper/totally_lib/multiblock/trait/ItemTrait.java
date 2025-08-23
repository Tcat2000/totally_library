package org.tcathebluecreper.totally_lib.multiblock.trait;

import dev.latvian.mods.rhino.NativeArray;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.tcathebluecreper.totally_lib.crafting.RangedDetectorWrapper;

import java.util.List;

public class ItemTrait implements ITrait {
    public final String name;

    ItemStackHandler storage;

    public ItemTrait(String name, int stacks) {
        this.name = name;
        this.storage = new ItemStackHandler(stacks) {
        };
    }

    public ItemTrait(String name, List<ItemStack> startingStacks) {
        this.name = name;
        NonNullList<ItemStack> list = NonNullList.create();
        list.addAll(startingStacks);
        this.storage = new ItemStackHandler(list);
    }

//    public ItemTrait(String name, NativeArray startingStacks) {
//        this.name = name;
//        this.storage = new ItemStackHandler((NonNullList<ItemStack>) NonNullList.of(startingStacks));
//    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readSaveNBT(CompoundTag tag) {
        if(tag.contains(getName())) storage.deserializeNBT(tag.getCompound(getName()));
    }

    @Override
    public void writeSaveNBT(CompoundTag tag) {
        tag.put(getName(), storage.serializeNBT());
    }
}
