package com.alain898.course.realtimestreaming.common.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockingMap<K, V> implements IBlockingMap<K, V> {
    private final Map<K, V> map;
    private final int capacity;
    private final int offerIntervalMs;

    public BlockingMap(int capacity, int offerIntervalMs) {
        this.capacity = capacity > 0 ? capacity : 32;
        this.map = new ConcurrentHashMap<>(this.capacity);
        this.offerIntervalMs = offerIntervalMs > 0 ? offerIntervalMs : 1;
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public void put(K key, V value) throws InterruptedException {
        do {
            if (this.map.size() < capacity) {
                synchronized (this.map) {
                    if (this.map.size() < capacity) {
                        this.map.put(key, value);
                        break;
                    }
                }
            }
            Thread.sleep(offerIntervalMs);
        } while (true);
    }

    public void clear() {
        this.map.clear();
    }

}
