package org.tcathebluecreper.totally_immersive.api.client;

import org.joml.Vector3f;

public interface SubTickAnimationSetting {
    Vector3f runFunction(Vector3f v1, Vector3f v2, Float f);
    int getStepCount();
}
