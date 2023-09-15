package io.netty.example.study.netblockingqueue.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.httpclient.HttpClientWriteService;
import io.netty.example.study.netblockingqueue.Config;
import io.netty.example.study.netblockingqueue.MarshallingHelper;
import io.netty.example.study.netblockingqueue.model.Message;
import io.netty.example.study.netblockingqueue.model.Result;
import io.netty.example.study.netblockingqueue.model.Shop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author shangruiwei
 * @date 2023/9/2 20:05
 */
public class AsyncClient {

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

//            messageService.poll().addListener(new GenericFutureListener<Future<? super Shop>>() {
//                @Override
//                public void operationComplete(Future<? super Shop> future) throws Exception {
//                    Shop shop = (Shop) future.get();
//                    System.out.printf("async poll Shop,id:%s,name:%s\n", shop.getId(), shop.getShopName());
//                }
//            });

            Shop shop = new Shop();
            shop.setId(1001L);
            shop.setShopName("test");
            messageService.offer(shop).addListener(new GenericFutureListener<Future<? super Result>>() {
                @Override
                public void operationComplete(Future<? super Result> future) throws Exception {
                    Result result = (Result) future.get();
                    System.out.printf("async offer result,code:%s,desc:%s\n", result.getCode(), result.getDesc());
                }
            });

            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
