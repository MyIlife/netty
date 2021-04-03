package com.ilife.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向pipeline加入handler
        ch.pipeline().addLast("MyHttpServerCodec",new HttpServerCodec()) // netty提供的http编码解码器
                .addLast("MyHttpServerHandler",new TestHttpServerHandler());
    }
}
