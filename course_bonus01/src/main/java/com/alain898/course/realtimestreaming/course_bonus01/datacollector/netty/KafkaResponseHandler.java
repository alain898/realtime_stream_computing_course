package com.alain898.course.realtimestreaming.course_bonus01.datacollector.netty;

import com.alain898.course.realtimestreaming.common.concurrency.BackPressureExecutor;
import com.alain898.course.realtimestreaming.common.kafka.KafkaReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public abstract class KafkaResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(KafkaResponseHandler.class);

    private final ExecutorService responseExecutor;


    private volatile boolean stop = false;

    private final String zookeeperConnect;
    private final String topic;
    private final String groupId;

    private final List<Thread> receivers;

    public KafkaResponseHandler(String zookeeperConnect, String topic, String groupId,
                                int receiverNumber, ExecutorService responseExecutor) {
        this.zookeeperConnect = zookeeperConnect;
        this.topic = topic;
        this.groupId = groupId;
        this.responseExecutor = responseExecutor != null ? responseExecutor : new BackPressureExecutor(
                "response", 1, 4, 1024, 1024, 1);
        this.receivers = IntStream.range(0, receiverNumber)
                .mapToObj(i -> new Thread(new KafkaReceiverRunnable(), String.format("receiver-%d", i)))
                .collect(Collectors.toList());
    }


    private class KafkaReceiverRunnable implements Runnable {
        private final KafkaReader kafkaReader = new KafkaReader(
                zookeeperConnect, topic, groupId, "largest");


        private byte[] receive() {
            if (kafkaReader.hasNext()) {
                return kafkaReader.next();
            } else {
                return null;
            }
        }

        @Override
        public void run() {
            while (!stop && !(Thread.currentThread().isInterrupted())) {
                try {
                    byte[] event = receive();
                    if (event == null) {
                        continue;
                    }

                    CompletableFuture
                            .supplyAsync(() -> process(event), responseExecutor)
                            .exceptionally(e -> {
                                logger.error("unexpected exception", e);
                                return null;
                            });
                } catch (Exception e) {
                    logger.error("unexpected exception", e);
                }
            }
        }
    }

    public abstract Void process(byte[] event);

    public void start() {
        receivers.forEach(Thread::start);
    }

    public void stop() {
        stop = true;
        receivers.forEach(Thread::interrupt);

        receivers.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                logger.warn("InterruptedException caught, exit");
            }
        });

        shutdownAndWait(responseExecutor);
    }

    private void shutdownAndWait(ExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("InterruptedException caught, exit");
        }
    }

}
