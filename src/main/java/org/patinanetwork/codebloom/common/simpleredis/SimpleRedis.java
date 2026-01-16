package org.patinanetwork.codebloom.common.simpleredis;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleRedis<T> extends ConcurrentHashMap<String, T> {
    private boolean shouldEvict;

    public SimpleRedis() {
        this(true);
    }

    public SimpleRedis(final boolean shouldEvict) {
        this.shouldEvict = shouldEvict;
    }

    public T get(String key) {
        Object value = super.get(key);
        return (T) value;
    }

    protected boolean isShouldEvict() {
        return shouldEvict;
    }
}
