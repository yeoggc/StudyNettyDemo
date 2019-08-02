package com.sxt.netty.serialized;

import com.sxt.netty.utils.GzipUtils;
import com.sxt.netty.utils.RequestMessage;
import com.sxt.netty.utils.SerializableFactory4Marshalling;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client4Serializable {

    //处理请求和处理服务端响应的线程组
    private EventLoopGroup group = null;

    // 服务启动相关配置信息
    private Bootstrap bootstrap = null;

    public Client4Serializable() {
        init();
    }

    public static void main(String[] args) {
        Client4Serializable client = null;
        ChannelFuture future = null;
        try{
            client = new Client4Serializable();
            future = client.doRequest("localhost", 9999, new Client4SerializableHandler());
            String attachment = "test attachment";
            byte[] attBuf = attachment.getBytes();
            attBuf = GzipUtils.zip(attBuf);
            RequestMessage msg = new RequestMessage(new Random().nextLong(),
                    "test", attBuf);
            future.channel().writeAndFlush(msg);
            TimeUnit.SECONDS.sleep(1);
            future.addListener(ChannelFutureListener.CLOSE);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(null != client){
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

    private ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingDecoder());
                ch.pipeline().addLast(SerializableFactory4Marshalling.buildMarshallingEncoder());
                ch.pipeline().addLast(handlers);
            }
        });
        return this.bootstrap.connect(host, port).sync();
    }

    public void release() {
        this.group.shutdownGracefully();
    }
}
