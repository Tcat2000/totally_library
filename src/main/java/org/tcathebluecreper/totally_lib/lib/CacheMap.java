package org.tcathebluecreper.totally_lib.lib;

import java.util.HashMap;
import java.util.function.Supplier;

public class CacheMap<K,V> extends HashMap<K,V> {
    public V getAndAdd(K key, Supplier<V> value) {
        if(!containsKey(key)) put(key, value.get());
        return get(key);
    }
}
