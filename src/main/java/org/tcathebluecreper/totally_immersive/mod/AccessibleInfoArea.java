package org.tcathebluecreper.totally_immersive.mod;

import net.minecraft.client.renderer.Rect2i;

// CREDIT: This class is directly taken from Immersive Industry
// TODO: Replace with own code
public interface AccessibleInfoArea<T> {

    Rect2i getArea();

    T getStack();

}