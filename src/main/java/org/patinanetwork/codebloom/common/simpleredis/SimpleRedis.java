package org.patinanetwork.codebloom.common.simpleredis;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
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
    private Counter counter;

    public SimpleRedis() {
        this(true);
    }

    /**
     * Creates SimpleRedis instance with eviction
     *
     * @param shouldEvict determines if entries should be evicted during scheduled cleanup
     */
    public SimpleRedis(final boolean shouldEvict) {
        this.shouldEvict = shouldEvict;
    }

    /**
     * Creates a SimpleRedis instance with metrics tracking
     *
     * @param shouldEvict determines if entries should be evicted during scheduled cleanup
     * @param registry meter registry for tracking metrics
     * @param index database index for this SimpleRedis instance
     */
    public SimpleRedis(final boolean shouldEvict, MeterRegistry registry, int index) {
        this(shouldEvict);
        setMetrics(registry, index);
    }

    void setMetrics(MeterRegistry registry, int index) {
        registry.gauge("simpleredis.index.size", Tags.of("index", String.valueOf(index)), this, m -> (double) m.size());
        this.counter = registry.counter("simpleredis.index.entries", Tags.of("index", String.valueOf(index)));
    }

    @Override
    public T put(String key, T value) {
        T result = super.put(key, value);
        if (counter != null && result == null) {
            counter.increment();
        }
        return result;
    }

    @Override
    public T remove(Object key) {
        return super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
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
