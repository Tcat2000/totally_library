package org.tcathebluecreper.totally_lib.waila;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.IFluidTank;

public interface IUnifiedWaila {

    public void text(String text);

    void translatable(String text);

    void component(Component component);

    void verticalSpacer();

    void tank(IFluidTank tank);
}
