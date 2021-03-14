package com.alain898.course.realtimestreaming.course_bonus02.example;

public class Tools {
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
