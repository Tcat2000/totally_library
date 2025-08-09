package org.tcathebluecreper.totally_lib.client;

import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3f;

public enum SubTickAnimationSettings implements SubTickAnimationSetting {
    STEP_UNCACHED ((v, v2,f) -> v, 0),
    STEP_1 ((v, v2,f) -> v, 1),
    LINEAR_UNCACHED(Vector3f::lerp, 0),
    LINEAR_1 (Vector3f::lerp, 1),
    LINEAR_2 (Vector3f::lerp, 2),
    LINEAR_4 (Vector3f::lerp, 4),
    LINEAR_8 (Vector3f::lerp, 8),
    LINEAR_16 (Vector3f::lerp, 16);

    private final TriFunction<Vector3f, Vector3f, Float, Vector3f> function;
    private final int steps;
    SubTickAnimationSettings(TriFunction<Vector3f, Vector3f, Float, Vector3f> function, int steps) {
        this.function = function;
        this.steps = steps;
    }

    @Override
    public Vector3f runFunction(Vector3f v1, Vector3f v2, Float f) {
        return function.apply(v1, v2, f);
    }

    @Override
    public int getStepCount() {
        return steps;
    }

    public static SubTickAnimationSetting custom(TriFunction<Vector3f, Vector3f, Float, Vector3f> function, int steps) {
        return new Custom(function, steps);
    }

    public static class Custom implements SubTickAnimationSetting {
        private final TriFunction<Vector3f, Vector3f, Float, Vector3f> function;
        private final int steps;
        Custom(TriFunction<Vector3f, Vector3f, Float, Vector3f> function, int steps) {
            this.function = function;
            this.steps = steps;
        }

        @Override
        public Vector3f runFunction(Vector3f v1, Vector3f v2, Float f) {
            return function.apply(v1, v2, f);
        }

        @Override
        public int getStepCount() {
            return steps;
        }
    }
}
