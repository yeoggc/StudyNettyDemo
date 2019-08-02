package com.sxt.netty.serialized;

import com.sxt.netty.utils.GzipUtils;
import com.sxt.netty.utils.RequestMessage;
import com.sxt.netty.utils.ResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class Server4SerializableHandler extends ChannelHandlerAdapter {

    // 业务处理逻辑
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("from client : ClassName - " + msg.getClass().getName()
                + " ; message : " + msg.toString());
        if(msg instanceof RequestMessage){
            RequestMessage request = (RequestMessage)msg;
            byte[] attachment = GzipUtils.unzip(request.getAttachment());
            System.out.println(new String(attachment));
        }
        ResponseMessage response = new ResponseMessage(0L, "test response");
        ctx.writeAndFlush(response);
    }

    // 异常处理逻辑
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exceptionCaught method run...");
        cause.printStackTrace();
        ctx.close();
    }
}
