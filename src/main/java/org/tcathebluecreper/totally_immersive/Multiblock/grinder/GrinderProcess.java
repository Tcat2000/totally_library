package org.tcathebluecreper.totally_immersive.Multiblock.grinder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class GrinderProcess {
    public final int PROCESS_TIME = 140;
    public final int RESET_TIME = 20;
    public int progress = 0;
    public int resetCooldown = 0;
    public GrinderRecipe recipe;

    public GrinderProcess(CompoundTag tag) {
        progress = tag.getInt("progress");
        resetCooldown = tag.getInt("cooldown");
    }
    public GrinderProcess() {}

    public void tick(Level level, GrinderState state) {
        if(progress != 0 && recipe == null) {
            recipe = GrinderRecipe.findRecipe(level, state.processSlot.getValue().getStackInSlot(0));
            if(recipe == null) progress = -1;
        }
        if(progress == 0 || progress == -1) {
            recipe = GrinderRecipe.findRecipe(level, state.input.getValue().getStackInSlot(0));
            if(recipe == null) {
                progress = -1;
                return;
            }
            progress = 1;
        }
        else if(progress == PROCESS_TIME) {
            state.input.getValue().extractItem(0, recipe.input.getItems()[0].getCount(), false);

            ((RangedWrapper)state.processSlot.getValue()).setStackInSlot(0, ItemStack.EMPTY);
            ItemStack stack = state.output.getValue().getStackInSlot(0);
//            if(stack.isEmpty()) state.output.getValue().insertItem(0, recipe.output.copy(), false);
//            else stack.grow(recipe.output.getCount());
            progress = 0;
            resetCooldown = RESET_TIME;
        }
        else if(progress > 0) {
            if(drawPower(level, state)) progress++;
        }
    }
    public boolean drawPower(Level level, GrinderState state) {
        if(state.power.getValue().extractEnergy(recipe.energyCost, true) == recipe.energyCost) {
            state.power.getValue().extractEnergy(recipe.energyCost, false);
            return true;
        }
        return false;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("process", progress);
        tag.putInt("cooldown", resetCooldown);
        if(recipe != null) tag.putString("recipe", recipe.getId().toString());
        return tag;
    }

    public void tickClient() {
        if(progress == -2) return;
        if(progress == 0) {
            progress++;
        }
        else if(progress > 0) {
            if(progress >= PROCESS_TIME) {
                progress = -1;
                resetCooldown = RESET_TIME;
            }
            else progress++;
        }
        else if(progress == -1) {
            resetCooldown--;
            if(resetCooldown <= 0) progress = 0;
        }
    }
}
