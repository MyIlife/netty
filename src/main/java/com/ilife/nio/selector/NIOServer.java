package com.ilife.nio.selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args)throws Exception {
        //创建serverSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector s = Selector.open();
        // 绑定端口，进行监听
        ssc.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞
        ssc.configureBlocking(false);
        // 将ssc注册到selector，关心的事件为连接事件
        ssc.register(s, SelectionKey.OP_ACCEPT);
        while (true){
            if (s.select(1000)==0) {
                System.out.println("服务端阻塞1秒，此时无连接事件发生");
                continue;
            }
            // 1.此时表示已经获取到关注的事件
            // 2.获取关注事件的sKeys集合
            // 3.通过sKeys方向获取通道
            Set<SelectionKey> sKeys = s.selectedKeys();
            Iterator<SelectionKey> iterator = sKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){ // 如果是OP_ACCEPT，即有新的客户端连接
                    //给该客户端生成一个新的socketChannel
                    SocketChannel accept = ssc.accept();
                    System.out.println("客户端连接，生成一个channel"+accept.hashCode());
                    accept.configureBlocking(false);
                    // 注册到selector,关注的事件是OP_READ，
                    // 同时关联一个buffer
                    accept.register(s,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }else if(key.isReadable()){ //OP_READ
                    //反向获取对应的channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    ByteBuffer b = (ByteBuffer)key.attachment();
                    //读入buffer
                    channel.read(b);
                    System.out.println("客户端发送来的数据："+new String(b.array()));
                }
                iterator.remove();
            }
        }
    }

}

