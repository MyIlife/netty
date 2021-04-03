package com.ilife.nio.zerocopy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ZeroCopyServer {
    public static void main(String[] args) throws Exception {
        InetSocketAddress a = new InetSocketAddress(7001);
        ServerSocketChannel open = ServerSocketChannel.open();
        ServerSocket socket = open.socket();
        socket.bind(a);
        ByteBuffer b =ByteBuffer.allocate(4096);
        while (true){
            SocketChannel accept = open.accept();
            int count = 0;
            while (count != -1){
                count = accept.read(b);
            }
            // 倒带
            b.rewind();
        }
    }
}
