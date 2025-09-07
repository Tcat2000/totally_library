package org.tcathebluecreper.totally_lib.test;

import net.minecraft.resources.ResourceLocation;
import org.tcathebluecreper.totally_lib.TotallyLibrary;
import org.tcathebluecreper.totally_lib.multiblock.MultiblockBuilder;
import org.tcathebluecreper.totally_lib.multiblock.RegistrableMultiblock;

public class TestMultiblock {
    public static final RegistrableMultiblock testMB = new MultiblockBuilder(ResourceLocation.fromNamespaceAndPath("test","test"), TotallyLibrary.regManager, (mb) -> {}, false).build();
}
