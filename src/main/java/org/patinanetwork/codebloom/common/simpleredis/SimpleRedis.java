package org.patinanetwork.codebloom.common.simpleredis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a KeyValueStore that works similarly to a Redis database, but in memory. The value will be typed to the
 * specific database slot being utilized.
 *
 * <p><b>Example Usage:</b>
 *
 * <pre>{@code
 * // 1. Select the specific database slot
 * this.simpleRedis = provider.select(SimpleRedisSlot.VERIFICATION_EMAIL_SENDING);
 *
 * // 2. Perform operations (e.g., storing current time with associated userId)
 * simpleRedis.put(userId, System.currentTimeMillis());
 * }</pre>
 *
 * @see SimpleRedisSlot
 * @see SimpleRedisProvider
 */
public class SimpleRedis<T> extends ConcurrentHashMap<String, T> {
    private boolean shouldEvict;

    public SimpleRedis() {
        this(true);
    }

    public SimpleRedis(final boolean shouldEvict) {
        this.shouldEvict = shouldEvict;
    }

    /**
     * Cast object value as type of {@code SimpleRedisSlot} in database slot.
     *
     * @param key
     * @return typed value
     */
    public T get(String key) {
        Object value = super.get(key);
        return (T) value;
    }

    protected boolean isShouldEvict() {
        return shouldEvict;
    }
}
