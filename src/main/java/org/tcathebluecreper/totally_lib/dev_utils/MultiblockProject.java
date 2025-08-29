package org.tcathebluecreper.totally_lib.dev_utils;

import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegister;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.editor.data.Resources;
import net.minecraft.nbt.CompoundTag;

@LDLRegister(name = "sm", group = "editor.multiblock")
public class MultiblockProject implements IProject {
    @Override
    public Resources getResources() {
        return null;
    }

    @Override
    public IProject newEmptyProject() {
        return null;
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {

    }
}
