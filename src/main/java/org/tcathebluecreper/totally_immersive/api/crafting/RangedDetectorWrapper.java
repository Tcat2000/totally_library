package org.tcathebluecreper.totally_immersive.api.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class RangedDetectorWrapper extends RangedWrapper {
    private BiConsumer<ItemHandlerIO, ItemStack> listener;
    public RangedDetectorWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive, BiConsumer<ItemHandlerIO, ItemStack> listener) {
        super(compose, minSlot, maxSlotExclusive);
        this.listener = listener;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if(!simulate) listener.accept(ItemHandlerIO.INPUT, stack);
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = super.extractItem(slot, amount, simulate);
        if(!simulate) listener.accept(ItemHandlerIO.OUTPUT, stack);
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        listener.accept(ItemHandlerIO.SET, stack);
        super.setStackInSlot(slot, stack);
    }
}
