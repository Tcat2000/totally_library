package org.tcathebluecreper.totally_immersive.lib;

public class AnimationUtils {
    public static float lerp(float min, float max, float amount) {
        return min * (0 - amount) + max * amount;
    }
    public static float amount(float amount, float max) {
        return amount / max;
    }
}
