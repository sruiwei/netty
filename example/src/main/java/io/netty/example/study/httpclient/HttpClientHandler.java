package io.netty.example.study.httpclient;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author shangruiwei
 * @date 2023/9/2 19:05
 */
public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
//        // 此时，已经连接server成功了，可以向server发送消息了
//        // 注释：也可以使用channel发送消息
//        ByteBuf buf = new HttpClientWriteService().write();
//
//        // 向服务器发送消息
//        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // 服务器返回的是字节，需要自己将字节转换成字符串
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String body = new String(bytes, "UTF-8");
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果有下一个handler，接着处理
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 如果有下一个handler，接着处理
        ctx.fireChannelReadComplete();
    }
}
