package org.tcathebluecreper.totally_lib.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class HeatableItem extends Item {
    public HeatableItem(Properties p_41383_) {
        super(p_41383_);
    }

    public abstract int thermalMass();
    public void heatChanged(ItemStack stack, int energy, float kelvin) {}

    public static void addHeat(ItemStack stack, int heat) {
        if(!(stack.getItem() instanceof HeatableItem)) return;
        CompoundTag tag;
        if(stack.getTag() != null) tag = stack.getTag().getCompound("TIHeat");
        else {
            stack.setTag(new CompoundTag());
            tag = new CompoundTag();
            tag.putInt("energy", 297 * ((HeatableItem) stack.getItem()).thermalMass());
        }

        tag.putInt("energy", tag.getInt("energy") + heat);
        ((HeatableItem) stack.getItem()).heatChanged(stack, tag.getInt("energy"), tag.getInt("energy") / (float) ((HeatableItem) stack.getItem()).thermalMass());
    }
}
