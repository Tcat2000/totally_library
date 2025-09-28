package org.tcathebluecreper.totally_lib.dev_utils;

import java.util.function.Supplier;

public class TLUtils {
    public static <T> Supplier<T> supplier(T value) {
        return () -> value;
    }
}
