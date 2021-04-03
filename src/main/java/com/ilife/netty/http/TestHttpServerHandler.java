package com.ilife.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    /**
     * 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断msg是不是 httprequest
        if(msg instanceof HttpRequest){
            System.out.println("msg 类型："+ msg.getClass());
            // 处理浏览器请求图标而导致两次请求
            HttpRequest httpRequest = (HttpRequest) msg;
            String uri = httpRequest.getUri();
            System.out.println("uri = " + uri);
            if("/favicon.ico".equals(uri)){
                System.out.println("uri = " + uri+",不作处理");
                return;
            }
            // 回复消息给浏览器 http格式
            ByteBuf content = Unpooled.copiedBuffer("你好，我是个莫得感情的服务器", CharsetUtil.UTF_8);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
