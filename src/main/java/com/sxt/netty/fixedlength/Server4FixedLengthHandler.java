package com.sxt.netty.fixedlength;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class Server4FixedLengthHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = msg.toString();
        System.out.println("from client : " + message.trim());
        String line = "ok ";
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes(StandardCharsets.UTF_8)));

    }

    // 异常处理逻辑
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exceptionCaught method run...");
        // cause.printStackTrace();
        ctx.close();
    }
}
