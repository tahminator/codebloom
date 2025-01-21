package com.patina.codebloom.common.kv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class KeyValueStore {
    private final Map<String, Object> store = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        store.put(key, value);
    }

    public Object get(String key) {
        return store.get(key);
    }

    public void remove(String key) {
        store.remove(key);
    }

    public boolean containsKey(String key) {
        return store.containsKey(key);
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
