package com.alain898.course.realtimestreaming.common.concurrency;

import com.google.common.base.Preconditions;

public class HashPartitioner implements IPartitioner {
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
