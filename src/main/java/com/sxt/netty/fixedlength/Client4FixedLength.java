package com.sxt.netty.fixedlength;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client4FixedLength {

    // 处理线程组，处理请求和处理服务端响应
    private EventLoopGroup group = null;
    // 服务启动相关配置信息
    private Bootstrap bootstrap = null;

    public Client4FixedLength() {
        init();
    }

    public static void main(String[] args) {
        Client4FixedLength client = null;
        ChannelFuture future = null;

        try {
            client = new Client4FixedLength();
            future = client.doRequest("localhost", 9999);

            Scanner s = null;
            while (true) {
                s = new Scanner(System.in);
                System.out.print("enter message send to server > ");
                String line = s.nextLine();
                byte[] temp = line.getBytes(StandardCharsets.UTF_8);
                future.channel().writeAndFlush(Unpooled.copiedBuffer(temp));
                TimeUnit.SECONDS.sleep(1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != future) {
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null != client) {
                client.release();
            }
        }


    }

    private void init() {

        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 绑定线程组
        bootstrap.group(group);
        // 设定通讯模式为NIO
        bootstrap.channel(NioSocketChannel.class);

    }

    private ChannelFuture doRequest(String host, int port) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelHandler[] handlers = new ChannelHandler[3];
                handlers[0] = new FixedLengthFrameDecoder(3);
                // 字符串解码器Handler，会自动处理channelRead方法的msg参数，将ByteBuf类型的数据转换为字符串对象
                handlers[1] = new StringDecoder(StandardCharsets.UTF_8);
                handlers[2] = new Client4FixedLengthHandler();

                ch.pipeline().addLast(handlers);
            }
        });
        return this.bootstrap.connect(host, port).sync();
    }


    public void release() {
        this.group.shutdownGracefully();
    }

}
