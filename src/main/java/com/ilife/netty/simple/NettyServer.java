package com.ilife.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer {
    public static final int LISTEN_PORT = 6668;
    public static void main(String[] args)throws  Exception{
        // 创建两个线程组 boss和worker
        // bossGroup只处理连接请求、workerGroup负责处理客户端业务
        // 两个都是死循环
        // 线程数为 cpu核心数*2 （默认）
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端启动，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 使用链式编程进行设置
            bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 服务器端通道
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道测试对象
                        // 给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    }); // 给workerGroup的EventLoop设置处理器
            System.out.println(" server is ready ...");
            // 绑定端口、启动服务器 并 同步处理
            ChannelFuture future = bootstrap.bind(LISTEN_PORT).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("监听端口["+LISTEN_PORT+"] 成功");
                    }   else{
                        System.out.println("监听端口["+LISTEN_PORT+"] 失败");

                    }
                }
            });
            // 对关闭通道事件的进行监听
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
