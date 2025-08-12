package org.tcathebluecreper.totally_lib.multiblock;

import net.minecraftforge.common.MinecraftForge;
import org.tcathebluecreper.totally_lib.TotallyLibrary;

import java.util.ArrayList;
import java.util.List;

public class ModMultiblocks {
    private static List<RegisterableMultiblock> allMultiblocks = new ArrayList<>();
    public static void init() {
        MinecraftForge.EVENT_BUS.post(new TLMultiblockRegistrationEvent(TotallyLibrary.regManager, allMultiblocks::add));
    }
}
