package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;

import java.util.ArrayList;
import java.util.List;

public class ModMultiblocks {
    public static List<RegistrableMultiblock> allMultiblocks = new ArrayList<>();
    public static void init() {
        for(RegistrableMultiblock mb : allMultiblocks) MultiblockHandler.registerMultiblock(mb.getMultiblock());
    }
}
