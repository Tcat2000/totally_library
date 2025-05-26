package org.tcathebluecreper.totally_immersive.Multiblock;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ChemicalBathProcess {
    public final int PROCESS_TIME = 120;
    public final int RESET_TIME = 20;
    public int progress = 0;
    public int resetCooldown = 0;
    public ChemicalBathRecipe recipe;

    public ChemicalBathProcess(CompoundTag tag) {
        progress = tag.getInt("progress");
        resetCooldown = tag.getInt("cooldown");
    }
    public ChemicalBathProcess() {}

    public void tick(Level level, ChemicalBathState state) {
        if(progress != 0 && recipe == null) {
            recipe = ChemicalBathRecipe.findRecipe(level, state.processSlot.getValue().getStackInSlot(0), state.chemTank.getValue().getFluidInTank(0));
            if(recipe == null) progress = 0;
        }
        if(progress == 0) {
            recipe = ChemicalBathRecipe.findRecipe(level, state.input.getValue().getStackInSlot(0), state.chemTank.getValue().getFluidInTank(0));
            if(recipe == null) return;
            progress++;

            ((RangedWrapper)state.processSlot.getValue()).setStackInSlot(0, state.input.getValue().extractItem(0, recipe.input.getItems()[0].getCount(), false));
        }
        else if(progress > 0) {
            if(progress >= PROCESS_TIME) {
                progress = -1;
                resetCooldown = RESET_TIME;

                state.chemTank.getValue().drain(recipe.fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                ((RangedWrapper)state.processSlot.getValue()).setStackInSlot(0, ItemStack.EMPTY);
                ItemStack stack = state.output.getValue().getStackInSlot(0);
                if(stack.isEmpty()) state.output.getValue().insertItem(0, recipe.output.copy(), false);
                else stack.grow(recipe.output.getCount());
            }
            else progress++;
        }
        else if(progress == -1) {
            resetCooldown--;
            if(resetCooldown <= 0) progress = 0;
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("process", progress);
        tag.putInt("cooldown", resetCooldown);
        if(recipe != null) tag.putString("recipe", recipe.getId().toString());
        return tag;
    }

    public void tickClient() {
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
