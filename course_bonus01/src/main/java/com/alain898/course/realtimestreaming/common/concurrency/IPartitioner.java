package com.alain898.course.realtimestreaming.common.concurrency;

public interface IPartitioner {
    int getPartition(String key);
}
