package io.netty.example.study.netblockingqueue.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.netblockingqueue.model.Message;
import io.netty.example.study.netblockingqueue.model.Result;
import io.netty.example.study.netblockingqueue.model.Shop;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author shangruiwei
 * @date 2023/9/10 11:15
 */
@ChannelHandler.Sharable
public class BlockingQueueServerHandler extends SimpleChannelInboundHandler<Message> {

    private BlockingQueue<Shop> queue = new LinkedBlockingQueue<Shop>();
    //    private BlockingQueue<Shop> priorityQueue = new PriorityBlockingQueue<Shop>();
    private Queue<QueueChannelItem> channelQueue = new ConcurrentLinkedQueue<QueueChannelItem>();

    private Lock lock = new ReentrantLock();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        System.out.printf("messageId:%s,optType:%s,id:%s,shopName:%s\n", message.getMessageId(), message.getOptType(),
                message.getShop() != null ? message.getShop().getId() : null,
                message.getShop() != null ? message.getShop().getShopName() : null);
        Thread.sleep(3000);
        Result result = null;
        if (message.getOptType().equals(1)) {
            boolean offerResult = queue.offer(message.getShop());
            if (offerResult) {
                result = Result.success(message.getMessageId(), null);
            } else {
                result = Result.fail(message.getMessageId(), "-1", "投递失败");
            }
            sendChannel(ctx.channel(), result);

            try {
                lock.lock();

                // 最好使用事件机制，来处理
                QueueChannelItem channelItem = null;
                while (!channelQueue.isEmpty()) {
                    channelItem = channelQueue.poll();
                    if (channelItem != null) {
                        break;
                    }
                }
                if (channelItem != null) {
                    Shop shop = queue.poll();
                    if (shop != null) {
                        sendChannel(channelItem.getChannel(), Result.success(channelItem.getMessageId(), null, shop));
                    }
                }
            } finally {
                lock.unlock();
            }
        } else if (message.getOptType().equals(2)) {
            // 注意：poll不阻塞
            Shop shop = queue.poll();
            if (shop == null) {
                // 队列没有数据，将请求的channel暂存
                channelQueue.offer(new QueueChannelItem(message.getMessageId(), ctx.channel()));
            } else {
                // 队列有数据，直接返回客户端
                result = Result.success(message.getMessageId(), null, shop);
                sendChannel(ctx.channel(), result);
            }
        }
    }

    private void sendChannel(Channel channel, Result result) {
        channel.writeAndFlush(result);
    }

    static class QueueChannelItem {
        private String messageId;
        private Channel channel;

        public QueueChannelItem(String messageId, Channel channel) {
            this.messageId = messageId;
            this.channel = channel;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }
}
