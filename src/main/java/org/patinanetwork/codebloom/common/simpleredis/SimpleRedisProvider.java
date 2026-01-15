package org.patinanetwork.codebloom.common.simpleredis;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * This KeyValueStore works very similarly to a Redis database, but in memory. The first parameter of {@code index} is
 * used to determine a specific index to utilize, similar to {@code REDIS_INSTANCE/0}. Within each index you can use all
 * the same features that you may normally use, such as putting and getting.
 *
 * <p>Databases (indices):
 *
 * <p>0 - Submission Refresh
 *
 * <p>1 - Verification Email Sending
 *
 * <p>2 - Global rate limiting
 */
@Service
public class SimpleRedisProvider {

    private final Map<Integer, SimpleRedis<?>> store = new ConcurrentHashMap<>();

    /** Initialize the Redis store with indices of all the databases we support. */
    @PostConstruct
    public void init() {
        register(SimpleRedisSlot.GLOBAL_RATE_LIMIT);
        register(SimpleRedisSlot.SUBMISSION_REFRESH);
        register(SimpleRedisSlot.VERIFICATION_EMAIL_SENDING);
    }

    private <T> void register(final SimpleRedisSlot<T> slot) {
        store.put(slot.getIndex(), new SimpleRedis<T>());
    }

    /** Selects database slot. Throws exception if slot doesn't exist. */
    public <T> SimpleRedis<T> select(final SimpleRedisSlot<T> slot) {
        SimpleRedis<?> redis = store.get(slot.getIndex());
        if (redis == null) {
            throw new IllegalArgumentException("This database is not initialized: " + slot.getIndex());
        }
        return (SimpleRedis<T>) redis;
    }

    /** Puts a value from the hashmap at the given Redis database slot. */
    public <V> void put(final SimpleRedisSlot<V> slot, final String key, final V value) {
        select(slot).put(key, value);
    }

    /** Returns value of {@code key} at Redis database {@code slot} */
    public <V> V get(final SimpleRedisSlot<V> slot, final String key) {
        return select(slot).get(key);
    }

    /** Removes a value from the hashmap at the given index. */
    public <V> void remove(final SimpleRedisSlot<V> slot, final String key) {
        select(slot).remove(key);
    }

    /** Checks whether a key exists at the hashmap at the given database. */
    public <V> boolean containsKey(final SimpleRedisSlot<V> slot, final String key) {
        return select(slot).containsKey(key);
    }

    /** Returns the size of the database. */
    public int size() {
        return store.size();
    }

    /** Returns the size of the specified database. */
    public <V> int size(final SimpleRedisSlot<V> slot) {
        return select(slot).size();
    }

    /** Clear the hashmap at a given slot. */
    public void clearIndex(final SimpleRedisSlot<?> slot) {
        SimpleRedis<?> redis = store.get(slot.getIndex());
        if (redis != null) {
            redis.clear();
        }
    }

    /** Clear the entire database of all it's values */
    public void clearAll() {
        store.values().forEach(Map::clear);
    }
}
