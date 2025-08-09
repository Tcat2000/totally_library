package org.tcathebluecreper.totally_lib.crafting;

import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class RangedDetectorWrapper extends RangedWrapper {
    private BiConsumer<ItemHandlerIO, ItemStack> listener;
    Field min;
    Field max;

    {
        try {
            min = RangedWrapper.class.getDeclaredField("minSlot");
            min.setAccessible(true);
            max = RangedWrapper.class.getDeclaredField("maxSlot");
            max.setAccessible(true);
        } catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public RangedDetectorWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        super(compose, minSlot, maxSlotExclusive);
        this.listener = (a,b) -> {};
    }

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

    public boolean compare(RangedWrapper other) {
        try {
            return min.get(this) == min.get(other) && max.get(this) == max.get(other);
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public IProbeInfo displayItems(IProbeInfo info) {
        int slots = getSlots();
        IProbeInfo horizontal = info.horizontal();
        for(int i = 0; i < slots; i++) {
            horizontal.item(getStackInSlot(i));
        }
        return horizontal;
    }
}
