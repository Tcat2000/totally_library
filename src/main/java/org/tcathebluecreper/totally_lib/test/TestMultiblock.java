package org.tcathebluecreper.totally_lib.test;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockInfo;

public class TestMultiblock {
    public static final TLMultiblockInfo testMB = new TLMultiblockBuilder(ResourceLocation.fromNamespaceAndPath("test","test"), TotallyLibrary.regManager, (mb) -> {}, false).build();
}
