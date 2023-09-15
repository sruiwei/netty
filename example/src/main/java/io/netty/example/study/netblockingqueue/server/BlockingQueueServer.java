package io.netty.example.study.netblockingqueue.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.study.netblockingqueue.Config;
import io.netty.example.study.netblockingqueue.MarshallingHelper;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author shangruiwei
 * @date 2023/9/2 20:05
 */
public class BlockingQueueServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        final BlockingQueueServerHandler blockingQueueServerHandler = new BlockingQueueServerHandler();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(MarshallingHelper.buildMarshallingEncoder());
                            pipeline.addLast(MarshallingHelper.buildMarshallingDecoder());
                            // 注释：这两种差别很大，使用pipeline.addLast(new BlockingQueueServerHandler());
                            // 每连接一个client，就new一个新的handler，否则使用一个
                            // 在demo中，如果使用blockingQueueServerHandler，才能实现多个客户端互通数据
//                            pipeline.addLast(new BlockingQueueServerHandler());
                            pipeline.addLast(blockingQueueServerHandler);
                        }
                    });

            Channel ch = b.bind(Config.HOST, Config.PORT).sync().channel();

            ch.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
