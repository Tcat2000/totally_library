package org.tcathebluecreper.totally_lib.test;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.RegisterableMultiblock;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;

public class TestMultiblock {
    public static final RegisterableMultiblock testMB = new TLMultiblockBuilder(ResourceLocation.fromNamespaceAndPath("test","test"), TotallyLibrary.regManager, (mb) -> {}).build();
}
