package org.tcathebluecreper.totally_lib.test;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.RegistrableMultiblock;
import org.tcathebluecreper.totally_lib.multiblock.TLMultiblockBuilder;

public class TestMultiblock {
    public static final RegistrableMultiblock testMB = new TLMultiblockBuilder(ResourceLocation.fromNamespaceAndPath("test","test"), TotallyLibrary.regManager, (mb) -> {}, false).build();
}
