package io.netty.example.study.netblockingqueue.client;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.example.study.netblockingqueue.model.Message;
import io.netty.example.study.netblockingqueue.model.Result;
import io.netty.example.study.netblockingqueue.model.Shop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shangruiwei
 * @date 2023/9/14 00:14
 */
public class MessageService {

    private static Map<String, Promise<Result>> offerMap = new ConcurrentHashMap<String, Promise<Result>>();
    private static Map<String, Promise<Shop>> pollMap = new ConcurrentHashMap<String, Promise<Shop>>();

    private Channel ch;

    public MessageService(Channel ch) {
        this.ch = ch;
    }

    public Promise<Result> offer(Shop shop) {
        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setShop(shop);
        message.setOptType(1);
        ch.writeAndFlush(message);
        Promise<Result> promise = new DefaultPromise<Result>(ch.eventLoop());
        offerMap.put(message.getMessageId(), promise);
        return promise;
    }

    public Promise<Shop> poll() {
        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setOptType(2);
        ch.writeAndFlush(message);
        Promise<Shop> promise = new DefaultPromise<Shop>(ch.eventLoop());
        pollMap.put(message.getMessageId(), promise);
        return promise;
    }

    public void callbackResult(Result result) {
        Promise<Result> resultPromise = offerMap.get(result.getMessageId());
        Promise<Shop> shopPromise = pollMap.get(result.getMessageId());
        if (resultPromise == null && shopPromise == null) {
            throw new RuntimeException(String.format("messageId:%s在map中不存在", result.getMessageId()));
        }
        if (resultPromise != null) {
            resultPromise.setSuccess(result);
            return;
        }
        shopPromise.setSuccess(result.getShop());
    }
}
