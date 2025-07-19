package org.tcathebluecreper.totally_immersive.api.kubejs;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import dev.latvian.mods.kubejs.event.EventJS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegisterMultiblockMachineEventJS extends EventJS {
    public static Map<String, MultiblockRegistration<IMultiblockState>> multiblocks = new HashMap<>();
    public void create(String modid, Supplier<MultiblockRegistration<IMultiblockState>> multiblock) {
        multiblocks.put(modid, multiblock.get());
    }
}
