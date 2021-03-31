package com.ilife.nio.selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception{
        // 得到一个网络通道
        SocketChannel channel = SocketChannel.open();
        // 设置非阻塞模式
        channel.configureBlocking(false);
        InetSocketAddress localhost = new InetSocketAddress("localhost", 6666);
        if(!channel.connect(localhost)){
            while (!channel.finishConnect()){
                System.out.println("因为连接需要时间，客户端非阻塞，可以干其他事");
            }
        }
        String s = "连一下看看";
        ByteBuffer byteBuffer = ByteBuffer.wrap(s.getBytes());
        //发送数据，将buffer数据写入channel
        channel.write(byteBuffer);
        System.in.read();
    }
}
