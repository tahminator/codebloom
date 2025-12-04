package com.patina.codebloom.common.simpleredis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * This KeyValueStore works very similarly to a Redis database, but in memory. The first parameter of `index` is used to
 * determine a specific index to utilize, similar to `REDIS_INSTANCE/0`. Within each index you can use all the same
 * features that you may normally use, such as putting and getting.
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
public class SimpleRedis {

    private final Map<Integer, Map<String, Object>> store = new ConcurrentHashMap<>();

    /** Puts a value from the hashmap at the given index. */
    public void put(final int index, final String key, final Object value) {
        store.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    /** Gets a value from the hashmap at the given index. */
    public Object get(final int index, final String key) {
        return store.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).get(key);
    }

    /** Removes a value from the hashmap at the given index. */
    public void remove(final int index, final String key) {
        store.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).remove(key);
    }

    /** Checks whether a key exists at the hashmap at the given index. */
    public boolean containsKey(final int index, final String key) {
        return store.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).containsKey(key);
    }

    /** Returns the size of the store. */
    public int size() {
        return store.size();
    }

    /** Returns the size of the hashmap at the given index. */
    public int size(final int index) {
        return store.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).size();
    }

    /** Clear the hashmap at a given index. */
    public void clearIndex(final int index) {
        store.remove(index);
    }

    /** Clear the entire store of all it's indexes */
    public void clearAll() {
        store.clear();
    }
}
