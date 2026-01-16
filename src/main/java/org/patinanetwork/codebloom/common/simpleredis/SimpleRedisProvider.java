package org.patinanetwork.codebloom.common.simpleredis;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This KeyValueStore works very similarly to a Redis database, but in memory. The first parameter of {@code index} is
 * used to determine a specific index to utilize, similar to {@code REDIS_INSTANCE/0}. Within each index you can use all
 * the same features that you may normally use inside of any {@code Map} class, such as putting and getting.
 */
@Service
public class SimpleRedisProvider {

    private final Map<Integer, SimpleRedis<?>> store = new ConcurrentHashMap<>();

    /** Initialize the Redis store with indices of all the databases we support. */
    @PostConstruct
    public void init() {
        SimpleRedisSlot.ALL.stream().forEach(this::register);
    }

    private <T> void register(final SimpleRedisSlot<T> slot) {
        store.put(slot.getIndex(), new SimpleRedis<T>());
    }

    /**
     * Selects database slot.
     *
     * @throws IllegalArgumentException if database slot does not exist.
     */
    public <T> SimpleRedis<T> select(final SimpleRedisSlot<T> slot) {
        SimpleRedis<?> redis = store.get(slot.getIndex());
        if (redis == null) {
            throw new IllegalArgumentException("This database is not initialized: " + slot.getIndex());
        }
        return (SimpleRedis<T>) redis;
    }

    /** Clears all evictable databases every 24 hours. */
    @Scheduled(fixedRate = 24, timeUnit = TimeUnit.HOURS)
    public void autoCleanup() {
        clearAll();
    }

    /** Clear all databases with {@code shouldEvict = true} */
    private void clearAll() {
        store.values().stream().filter(SimpleRedis::isShouldEvict).forEach(SimpleRedis::clear);
    }
}
