package com.alain898.course.realtimestreaming.course20.riskengine;

public class Tools {
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
