package com.alain898.course.realtimestreaming.course06.cftests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class Tests {
    private static final Logger logger = LoggerFactory.getLogger(Tests.class);

    private static String source() {
        return "hello";
    }

    private static String echo(String str) {
        return str;
    }

    private static String echo1(String str) {
        return str;
    }

    private static String echo2(String str) {
        return str;
    }

    private static String echo3(String str) {
        return str;
    }

    private static void print(String str) {
        logger.info(str);
    }

    private static final ExecutorService executor1 = new ForkJoinPool(4);
    private static final ExecutorService executor2 = new ForkJoinPool(4);
    private static final ExecutorService executor3 = new ForkJoinPool(4);
    private static final ExecutorService executor4 = new ForkJoinPool(4);

    private static void close() {
        executor1.shutdown();
        executor2.shutdown();
        executor3.shutdown();
        executor4.shutdown();
    }

    public static void main(String[] args) throws Exception {
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(Tests::source, executor1);
        CompletableFuture<String> cf2 = cf1.thenApplyAsync(Tests::echo, executor2);
        CompletableFuture<String> cf3_1 = cf2.thenApplyAsync(Tests::echo1, executor3);
        CompletableFuture<String> cf3_2 = cf2.thenApplyAsync(Tests::echo2, executor3);
        CompletableFuture<String> cf3_3 = cf2.thenApplyAsync(Tests::echo3, executor3);
        CompletableFuture<Void> cf3 = CompletableFuture.allOf(cf3_1, cf3_2, cf3_3);
        CompletableFuture<Void> cf4 = cf3.thenAcceptAsync(x -> print("world"), executor4);
        cf4.get();

        close();
    }
}
