package org.tcathebluecreper.totally_lib.theoneprobe;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TheOneProbeModule implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(new MultiblocksTOPProvider());
        return null;
    }
}
