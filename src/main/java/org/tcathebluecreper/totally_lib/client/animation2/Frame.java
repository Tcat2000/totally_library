package org.tcathebluecreper.totally_lib.client.animation2;

import org.joml.Vector3f;
import org.tcathebluecreper.totally_lib.client.animation.Easing;

public class Frame {
    public final AnimationElement owner; // The element this frame belongs to.
    public final int frame;
    public Vector3f value;
    public Easing interpolation; // The interpolation method between the last frame and this one.

    public Frame(AnimationElement owner, int frame) {
        this.owner = owner;
        this.frame = frame;
    }
}
