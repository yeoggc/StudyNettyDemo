package com.sxt.netty.serialized;

import com.sxt.netty.utils.SerializableFactory4Marshalling;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server4Serializable {
    //服务启动相关配置信息
    ServerBootstrap serverBootstrap = null;
    //监听线程组，监听客户端的请求
    private EventLoopGroup acceptorGroup = null;
    //处理线程组，处理与客户端的通讯
    private EventLoopGroup clientGroup = null;

    public Server4Serializable() {
        init();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        Server4Serializable server = null;

        try {
            server = new Server4Serializable();
            future = server.doAccept(9999, new Server4SerializableHandler());

            System.out.println("server started.");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != future) {
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (null != server) {
                server.release();
            }
        }


    }

    private void init() {
        acceptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();

        //绑定线程组
        serverBootstrap.group(acceptorGroup, clientGroup);
        // 设定通讯模式为NIO
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 设定缓冲区大小
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
        serverBootstrap.option(ChannelOption.SO_SNDBUF, 16 * 1024)
                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE, true);

    }

    private ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException {

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingDecoder());
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingEncoder());
                ch.pipeline().addLast(acceptorHandlers);
            }
        });

        return serverBootstrap.bind(port).sync();
    }

    public void release() {
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }


}
