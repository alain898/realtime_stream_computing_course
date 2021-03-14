package com.alain898.course.realtimestreaming.course_bonus02.example;

public class Event {

    public String product;
    public long timestamp;

    public Event() {
        this.product = null;
        this.timestamp = Long.MAX_VALUE;
    }

    public Event(String product, long timestamp) {
        this.product = product;
        this.timestamp = timestamp;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "product='" + product + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

