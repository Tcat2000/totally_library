package org.tcathebluecreper.totally_immersive.Multiblock.chemical_bath;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class ChemicalBathProcess {
    public final int PROCESS_TIME = 140;
    public final int RESET_TIME = 20;
    public int progress = 0;
    public int resetCooldown = 0;
    public ChemicalBathRecipe recipe;

    public ChemicalBathProcess(CompoundTag tag) {
        progress = tag.getInt("progress");
        resetCooldown = tag.getInt("cooldown");
    }
    public ChemicalBathProcess() {}

    public void tick(Level level, ChemicalBathState state, IMultiblockContext<ChemicalBathState> context) {
        IItemHandlerModifiable inputSlot = state.input.getValue();
        IItemHandlerModifiable processSlot = state.processSlot.getValue();
        IItemHandlerModifiable outputSlot = state.output.getValue();
        ArrayFluidHandler tank = state.chemTank.getValue();

        if(!state.redstoneState.isEnabled(context)) return;
        if(progress != 0 && progress != -2 && recipe == null) {
            recipe = ChemicalBathRecipe.findRecipe(level, processSlot.getStackInSlot(0), outputSlot.getStackInSlot(0), tank.getFluidInTank(0));
            if(recipe == null) progress = -2;
        }
        if(progress == 0 || progress == -2) {
            recipe = ChemicalBathRecipe.findRecipe(level, inputSlot.getStackInSlot(0), outputSlot.getStackInSlot(0), tank.getFluidInTank(0));
            if(recipe == null) {
                progress = -2;
                return;
            }
            progress = 1;

            processSlot.setStackInSlot(0, recipe.input.extractFrom(inputSlot.getStackInSlot(0)));
        }
        else if(progress > 0) {
            if(progress == PROCESS_TIME / 2) {
                recipe.fluidInput.extract(tank.getFluidInTank(0));
                processSlot.setStackInSlot(0, recipe.output.value.copy());
                if(drawPower(level, state)) progress++;
            }
            else if(progress >= PROCESS_TIME) {
                processSlot.setStackInSlot(0, ItemStack.EMPTY);
                ItemStack stack = state.output.getValue().getStackInSlot(0);
                recipe.output.insertTo(outputSlot, 0);
                progress = -1;
                resetCooldown = RESET_TIME;
            }
            else if(drawPower(level, state)) progress++;
        }
        else if(progress == -1) {
            resetCooldown--;
            if(resetCooldown <= 0) progress = 0;
        }
    }
    public boolean drawPower(Level level, ChemicalBathState state) {
        if(state.power.getValue().extractEnergy(recipe.energyCost.value, true) == recipe.energyCost.value) {
            state.power.getValue().extractEnergy(recipe.energyCost.value, false);
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
