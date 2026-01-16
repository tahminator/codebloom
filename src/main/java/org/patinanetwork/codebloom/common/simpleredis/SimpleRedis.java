package org.patinanetwork.codebloom.common.simpleredis;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleRedis<T> extends ConcurrentHashMap<String, T> {
    public T get(String key) {
        Object value = super.get(key);
        return (T) value;
    }
}
