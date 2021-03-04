package com.alain898.course.realtimestreaming.course_bonus01.netty;

import com.alain898.course.realtimestreaming.common.RestHelper;
import com.alain898.course.realtimestreaming.common.concurrency.BackPressureExecutor;
import com.alain898.course.realtimestreaming.common.concurrency.BlockingMap;
import com.alain898.course.realtimestreaming.common.concurrency.IBlockingMap;
import com.alain898.course.realtimestreaming.common.concurrency.TimeoutHandler;
import com.alain898.course.realtimestreaming.common.kafka.KafkaWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by alain on 18/5/8.
 */
public class AsyncServerHandler extends
        SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyDataCollector.class);

    private final String EVENT_ID_TAG = "EVENT_ID_TAG";

    private final String kafkaBroker = "127.0.0.1:9092";
    private final String requestTopic = "request";
    private final KafkaWriter kafkaWriter = new KafkaWriter(kafkaBroker);

    final private ExecutorService httpDecoderExecutor = new BackPressureExecutor(
            "decoder", 1, 4, 4, 1024, 1);
    final private ExecutorService requestExecutor = new BackPressureExecutor(
            "request", 1, 32, 32, 1024, 1);
    final private ExecutorService timeoutExecutor = new BackPressureExecutor(
            "timeout", 1, 8, 8, 1024, 1);
    final private ExecutorService responseExecutor = new BackPressureExecutor(
            "response", 1, 8, 8, 1024, 1);

    private final IBlockingMap<String, RequestItem> blockingMap = new BlockingMap<>(1024, 1);

    private final String zookeeperConnect = "127.0.0.1:2181";
    private final String responseTopic = "response";
    private final String groupId = "http_response";
    private final KafkaResponseHandler kafkaReader = new KafkaResponseHandler(
            zookeeperConnect, responseTopic, groupId, 2, responseExecutor) {
        @Override
        public Void process(byte[] body) {
            String jsonString = new String(body, Charsets.UTF_8);
            JSONObject e = JSON.parseObject(jsonString);

            String eventId = e.getString(EVENT_ID_TAG);
            if (eventId == null) {
                logger.warn(String.format("invalid response, event[%s]", String.valueOf(e)));
                return null;
            }

            RequestItem requestItem = blockingMap.remove(eventId);
            if (requestItem != null) {
                try {
                    sendResponse(requestItem.ctx, OK, RestHelper.genResponseString(
                            OK.code(), OK.toString()));
                } finally {
                    requestItem.ref.release();
                }
            }
            return null;
        }
    };

    {
        kafkaReader.start();
    }


    private JSONObject httpDecode(ChannelHandlerContext ctx, HttpRequest req) {
        logger.info(String.format("httpDecode thread[%s]", Thread.currentThread().toString()));

        if (!req.getDecoderResult().isSuccess()) {
            throw new RequestException(BAD_REQUEST.code(), RestHelper.genResponseString(
                    BAD_REQUEST.code(), "非法请求"));
        }

        if (OPTIONS.equals(req.getMethod())) {
            throw new RequestException(OK.code(), RestHelper.genResponseString(
                    OK.code(), "OPTIONS"));
        }

        if (req.getMethod() != POST) {
            throw new RequestException(METHOD_NOT_ALLOWED.code(), RestHelper.genResponseString(
                    METHOD_NOT_ALLOWED.code(), "方法不允许"));
        }

        String uri = req.getUri();
        if (!"/event".equals(uri)) {
            throw new RequestException(BAD_REQUEST.code(), RestHelper.genResponseString(
                    BAD_REQUEST.code(), "非法请求路径"));
        }

        byte[] body = readRequestBodyAsString((HttpContent) req);
        String jsonString = new String(body, Charsets.UTF_8);
        return JSON.parseObject(jsonString);
    }

    private void sendRequestToKafka(ChannelHandlerContext ctx, HttpRequest req,
                                    JSONObject event, RefController ref) {
        logger.info(String.format("sendRequestToKafka thread[%s]", Thread.currentThread().toString()));
        Preconditions.checkNotNull(event, "event is null");

        // 这里简单地用 UUID 来生成唯一ID。
        // 更严格的唯一ID，应该考虑"主机名 + IP地址 + 进程PID + timestamp + 随机数"等因素，可以参考MongoDb的_id生成规则。
        String eventId = UUID.randomUUID().toString();

        // EVENT_ID 用于后续接收到响应后，从 blockingMap 中找回之前的请求
        event.put(EVENT_ID_TAG, eventId);

        // 由于请求只是发送到kafka，并不知道什么时候响应能够返回，
        // 所以将请求上下文和相关信息先保存到blockingMap里，
        // 等到之后从kafka里读出响应后，再到blockingMap里找到之前的请求上下文和相关信息，从而返回客户端。
        RequestItem requestItem = new RequestItem(ctx, req, event, ref);
        try {
            blockingMap.put(eventId, requestItem);
        } catch (InterruptedException e) {
            logger.warn("InterruptedException caught");
            throw new RuntimeException("InterruptedException caught, exit");
        }

        // 将请求发送到kafka的request topic，
        // 之后Flink会从kafka中将该请求读取出来并进行处理，并将处理的结果再发送到kafka的response topic。
        try {
            kafkaWriter.send(requestTopic, event.toJSONString().getBytes(Charsets.UTF_8));
        } catch (Exception e) {
            logger.error("failed to send request to kafka", e);
            // 由于请求并没有成功发送到kafka，所以将请求从blockingMap删除掉
            blockingMap.remove(eventId);
            throw new RequestException(INTERNAL_SERVER_ERROR.code(), RestHelper.genResponseString(
                    INTERNAL_SERVER_ERROR.code(), INTERNAL_SERVER_ERROR.toString()));
        }

        // 当请求已经发送到kafka后，就启动一个超时任务，
        // 如果到时候超时设置的时间到了，但是请求对应的响应还没有来，就当超时返回。
        CompletableFuture<Void> timeoutFuture = TimeoutHandler.timeoutAfter(10000, TimeUnit.MILLISECONDS);
        timeoutFuture.thenAcceptAsync(v -> this.timeout(eventId), this.timeoutExecutor);
    }

    private void timeout(String eventId) {
        RequestItem requestItem = blockingMap.remove(eventId);
        if (requestItem != null) {
            try {
                sendResponse(requestItem.ctx, REQUEST_TIMEOUT, RestHelper.genResponseString(
                        REQUEST_TIMEOUT.code(), REQUEST_TIMEOUT.toString()));
            } finally {
                requestItem.ref.release();
            }
        }
    }

    private static class RequestItem {
        ChannelHandlerContext ctx;
        HttpRequest req;
        JSONObject event;
        RefController ref;

        public RequestItem(ChannelHandlerContext ctx, HttpRequest req, JSONObject event, RefController ref) {
            this.ctx = ctx;
            this.req = req;
            this.event = event;
            this.ref = ref;
        }
    }


    private static class RefController {
        private final ChannelHandlerContext ctx;
        private final HttpRequest req;

        public RefController(ChannelHandlerContext ctx, HttpRequest req) {
            this.ctx = ctx;
            this.req = req;
        }

        public void retain() {
            ReferenceCountUtil.retain(ctx);
            ReferenceCountUtil.retain(req);
        }

        public void release() {
            ReferenceCountUtil.release(req);
            ReferenceCountUtil.release(ctx);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req)
            throws Exception {
        logger.info(String.format("current thread[%s]", Thread.currentThread().toString()));
        final RefController refController = new RefController(ctx, req);
        refController.retain();
        CompletableFuture
                .supplyAsync(() -> this.httpDecode(ctx, req), this.httpDecoderExecutor)
                .thenAcceptAsync(e -> this.sendRequestToKafka(ctx, req, e, refController), this.requestExecutor)
                .exceptionally(e -> {
                    try {
                        logger.error("exception caught", e);
                        if (e.getCause() instanceof RequestException) {
                            RequestException re = (RequestException) e.getCause();
                            sendResponse(ctx, HttpResponseStatus.valueOf(re.getCode()), re.getResponse());
                        } else {
                            sendResponse(ctx, INTERNAL_SERVER_ERROR, RestHelper.genResponseString(
                                    INTERNAL_SERVER_ERROR.code(), INTERNAL_SERVER_ERROR.toString()));
                        }
                        return null;
                    } finally {
                        refController.release();
                    }
                });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (ctx.channel().isActive()) {
            sendResponse(ctx, INTERNAL_SERVER_ERROR, RestHelper.genResponseString(
                    INTERNAL_SERVER_ERROR.code(), INTERNAL_SERVER_ERROR.toString()));
        }
    }


    private static void setAllowDomain(FullHttpResponse response) {
        response.headers().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.headers().set("Access-Control-Max-Age", "3600");
        response.headers().set("Access-Control-Allow-Credentials", "true");
    }

    private static void sendResponse(ChannelHandlerContext ctx,
                                     HttpResponseStatus status,
                                     String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        setAllowDomain(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    private byte[] readRequestBodyAsString(HttpContent httpContent) {
        ByteBuf byteBuf = httpContent.content();
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        return data;
    }
}
