package io.netty.example.study.netblockingqueue.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.netblockingqueue.Config;
import io.netty.example.study.netblockingqueue.MarshallingHelper;
import io.netty.example.study.netblockingqueue.model.Result;
import io.netty.example.study.netblockingqueue.model.Shop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author shangruiwei
 * @date 2023/9/10 10:42
 */
public class SyncClient {

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(MarshallingHelper.buildMarshallingDecoder());
                            pipeline.addLast(MarshallingHelper.buildMarshallingEncoder());
                            pipeline.addLast(new AsyncClientHandler());
                        }
                    });

            Channel ch = b.connect(Config.HOST, Config.PORT).sync().channel();

            MessageService messageService = new MessageService(ch);

//            Shop shop = new Shop();
//            shop.setId(1001L);
//            shop.setShopName("test");
//            Result result = messageService.offer(shop).get();

            Shop syncShop = messageService.poll().get();

            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
