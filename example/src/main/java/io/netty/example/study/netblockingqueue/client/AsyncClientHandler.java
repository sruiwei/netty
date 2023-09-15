package io.netty.example.study.netblockingqueue.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.netblockingqueue.model.Result;

/**
 * @author shangruiwei
 * @date 2023/9/12 17:22
 */
public class AsyncClientHandler extends SimpleChannelInboundHandler<Result> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Result msg) throws Exception {
        new MessageService(ctx.channel()).callbackResult(msg);
    }
}
