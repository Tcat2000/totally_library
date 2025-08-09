package org.tcathebluecreper.totally_lib.waila;

import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.IFluidTank;
import snownee.jade.api.ITooltip;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.IElementHelper;

public class UnifiedWaila implements IUnifiedWaila {
    private IProbeInfo top;
    private ITooltip jade;

    @Override
    public void text(String text) {
        if(top != null) top.text(text);
        if(jade != null) jade.add(Component.literal(text));
    }

    @Override
    public void translatable(String key) {
        if(top != null) top.text(Component.translatable(key));
        if(jade != null) jade.add(Component.translatable(key));
    }

    @Override
    public void component(Component component) {
        if(top != null) top.text(component);
        if(jade != null) jade.add(component);
    }

    @Override
    public void verticalSpacer() {
        if(top != null) top.vertical();
//        if(jade != null) jade.;
    }

    @Override
    public void tank(IFluidTank tank) {
        if(top != null) top.tank(tank);
        if(jade != null) jade.add(IElementHelper.get().fluid(JadeFluidObject.of(tank.getFluid().getFluid(), tank.getFluidAmount())));
    }
}
