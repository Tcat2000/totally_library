package org.tcathebluecreper.totally_lib.lib;

public class AnimationUtils {
    public static float lerp(float min, float max, float amount) {
        return min * (1 - amount) + max * amount;
    }
    public static double lerp(double min, double max, double amount) {
        return min * (1 - amount) + max * amount;
    }
    public static float amount(float amount, float max) {
        if(max == 0) return 0;
        return amount / max;
    }
}
