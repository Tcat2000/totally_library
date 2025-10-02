package org.tcathebluecreper.totally_lib.lib;

import java.util.function.Supplier;

public class TLUtils {
    public static <T> Supplier<T> supplier(T value) {
        return () -> value;
    }
}
