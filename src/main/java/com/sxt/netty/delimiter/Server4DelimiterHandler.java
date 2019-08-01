package com.sxt.netty.delimiter;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

public class Server4DelimiterHandler extends ChannelHandlerAdapter {
	
	// 业务处理逻辑
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String message = msg.toString();
		System.out.println("from client : " + message);
		String line = "server message $E$ test delimiter handler!! $E$ second message $E$";
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
