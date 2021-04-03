package com.ilife.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * 1.自定义的handler需要继承netty相应的handlerAdapter
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 从这里可以读取到客户端的数据
     * @param ctx 上下文，包含管道pipeline，通道，地址
     * @param msg 客户端数据
     * @throws Exception
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        //1.正常情况
        //simple(ctx, (ByteBuf) msg);
        //2.如果这个业务是耗时业务，则异步执行
        // 2.1、用户自定义普通方案提交到channel对应NioEventLoop的taskQueue
        //m1(ctx);
        //2.2、定时任务，提交到scheduleTaskQueue
        //m2(ctx);
        // 2.3 发送给其他客户端

        // 结果：这里先执行，而上面的内容会在对应的等待时间到了后，返回给客户端
        System.out.println("服务端阻塞没？");
    }

    private void m2(final ChannelHandlerContext ctx) {
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(Unpooled.copiedBuffer("定时提交的任务", CharsetUtil.UTF_8));
            }
        },5L, TimeUnit.SECONDS);
    }

    private void simple(ChannelHandlerContext ctx, ByteBuf msg) {
        System.out.println("服务器读取数据的线程为："+ Thread.currentThread().getName());
        System.out.println("server ctx = " + ctx);
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline(); // 通道本身是双向链表

        // msg 转 ByteBuf,Netty提供的，不是ByteBuffer
        ByteBuf byteBuf = msg;
        System.out.println("客户端发送的消息是"+ byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+ channel.remoteAddress());
    }

    private void m1(final ChannelHandlerContext ctx) {
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端 channelRead", CharsetUtil.UTF_8));
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器【channelRegistered】："+ Thread.currentThread().getName());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器【channelActive】："+ Thread.currentThread().getName());
    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // write + flush 写入缓存，刷新
        // 编码并发送
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端 channelReadComplete",CharsetUtil.UTF_8));
    }

    /**
     * 如果发生异常，需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出现异常，关闭通道");
        ctx.channel().close();
    }
}
