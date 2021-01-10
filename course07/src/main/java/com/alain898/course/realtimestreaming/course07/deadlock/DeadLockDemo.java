package com.alain898.course.realtimestreaming.course07.deadlock;

import com.alain898.course.realtimestreaming.common.concurrency.BackPressureExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public class DeadLockDemo {
    private static final Logger logger = LoggerFactory.getLogger(DeadLockDemo.class);

    private final ExecutorService AExecutor = new BackPressureExecutor(
            "AExecutor", 1, 1, 1, 10, 1);
    private final ExecutorService BExecutor = new BackPressureExecutor(
            "BExecutor", 1, 1, 1, 10, 1);
    private final ExecutorService CExecutor = new BackPressureExecutor(
            "CExecutor", 1, 1, 1, 10, 1);


    private final AtomicLong itemCounter = new AtomicLong(0L);

    private String stepA() {
        String item = String.format("item-%d", itemCounter.getAndIncrement());
        logger.info(String.format("stepA item[%s]", item));
        sleep(10);  // 睡眠 10 ms
        return item;
    }

    private String stepB(String item) {
        String newItem = item + "+B";
        logger.info(String.format("stepB item[%s]", newItem));
        sleep(100);  // 睡眠 100 ms，让 stepB 的处理速度是 stepA 的十分之一
        return newItem;
    }

    private void demo1() {
        while (!Thread.currentThread().isInterrupted()) {
            CompletableFuture
                    .supplyAsync(this::stepA, this.AExecutor)
                    .thenApplyAsync(this::stepB, this.BExecutor)
                    .thenApplyAsync(this::stepB, this.BExecutor);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DeadLockDemo demo = new DeadLockDemo();
        demo.demo1();
    }
}
