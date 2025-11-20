package org.tcathebluecreper.totally_lib.client.animation;

import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3f;

public enum Easing implements IEasingMethod {
    LINEAR(Vector3f::lerp),
    FIRST((start,end,time) -> start),
    LAST((start,end,time) -> end);

    final TriFunction<Vector3f, Vector3f, Float, Vector3f> function;
    Easing(TriFunction<Vector3f, Vector3f, Float, Vector3f> function) {
        this.function = function;
    }

    @Override
    public Vector3f apply(Vector3f start, Vector3f end, Float time) {
        return function.apply(start, end, time);
    }

    public static IEasingMethod custom(TriFunction<Vector3f, Vector3f, Float, Vector3f> function) {
        return function::apply;
    }
}
