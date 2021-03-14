package com.alain898.course.realtimestreaming.course_bonus02.example;

public class CountedEvent extends Event {
    public int count;
    public long minTimestamp;
    public long maxTimestamp;

    public CountedEvent() {
        super();
        this.count = 0;
        this.minTimestamp = Long.MAX_VALUE;
        this.maxTimestamp = Long.MIN_VALUE;
    }

    public CountedEvent(String product, int count, long timestamp) {
        super(product, timestamp);
        this.count = count;
        this.minTimestamp = timestamp;
        this.maxTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CountedEvent{" +
                "count=" + count +
                ", minTimestamp=" + minTimestamp +
                ", maxTimestamp=" + maxTimestamp +
                ", product='" + product + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
