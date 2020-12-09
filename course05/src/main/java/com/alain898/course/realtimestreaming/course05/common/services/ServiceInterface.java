package com.alain898.course.realtimestreaming.course05.common.services;


public interface ServiceInterface {
    void start();

    void stop();

    void waitToShutdown() throws InterruptedException;

    String getName();

    ServiceStatus getStatus();
}
