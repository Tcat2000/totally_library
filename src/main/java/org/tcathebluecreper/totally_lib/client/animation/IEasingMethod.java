package org.tcathebluecreper.totally_lib.client.animation;

import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3f;

public interface IEasingMethod extends TriFunction<Vector3f, Vector3f, Float, Vector3f> {
}
