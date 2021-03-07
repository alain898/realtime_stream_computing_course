package com.alain898.course.realtimestreaming.common.concurrency;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class MultiBlockingMap<K, V> implements IBlockingMap<K, V> {

    private final List<BlockingMap<K, V>> maps;

    private final IPartitioner partitioner;

    public MultiBlockingMap(int mapNumber, int capacity, int offerIntervalMs) {
        Preconditions.checkArgument(mapNumber > 0, "mapNumber must be positive");

        this.maps = new ArrayList<>(mapNumber);
        for (int i = 0; i < mapNumber; i++) {
            this.maps.add(new BlockingMap<>(capacity, offerIntervalMs));
        }
        this.partitioner = new HashPartitioner(mapNumber);
    }

    private interface IPartitioner {
        int getPartition(String key);
    }

    private static class HashPartitioner implements IPartitioner {
        private final int partitions;

        public HashPartitioner(int partitions) {
            Preconditions.checkArgument(partitions > 0, "partitions must be positive");
            this.partitions = partitions;
        }

        @Override
        public int getPartition(String key) {
            Preconditions.checkNotNull(key, "key cannot be null");
            return Math.abs(hash(key)) % partitions;
        }

        private int hash(String k) {
            int h = 0;
            h ^= k.hashCode();
            h ^= (h >>> 20) ^ (h >>> 12);
            return h ^ (h >>> 7) ^ (h >>> 4);
        }
    }

    public V remove(K key) {
        return maps.get(partitioner.getPartition(String.valueOf(key))).remove(key);
    }

    public void put(K key, V value) throws InterruptedException {
        maps.get(partitioner.getPartition(String.valueOf(key))).put(key, value);
    }

    public void clear() {
        maps.forEach(BlockingMap::clear);
    }

    @Override
    public int size() {
        return maps.stream().mapToInt(BlockingMap::size).sum();
    }

}
