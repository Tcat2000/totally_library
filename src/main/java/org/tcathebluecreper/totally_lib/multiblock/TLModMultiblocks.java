package org.tcathebluecreper.totally_lib.multiblock;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TLModMultiblocks {
    public static List<TLMultiblockInfo> allMultiblocks = new ArrayList<>();
    public static void init() {
        for(TLMultiblockInfo mb : allMultiblocks) MultiblockHandler.registerMultiblock(mb.getMultiblock());
    }

    public static TLMultiblockInfo byId(ResourceLocation id) {
        return allMultiblocks.stream().filter(info -> info.getId().equals(id)).findFirst().orElse(null);
    }
}
