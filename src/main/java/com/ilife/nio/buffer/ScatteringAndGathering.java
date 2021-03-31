package com.ilife.nio.buffer;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ScatteringAndGathering {
    public static void main(String[] args) throws Exception{
        ServerSocketChannel open = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(6666);
        open.socket().bind(inetSocketAddress);
        ByteBuffer[] bs = new ByteBuffer[2];
        bs[0] = ByteBuffer.allocate(5);
        bs[1] = ByteBuffer.allocate(88);
        SocketChannel accept = open.accept();
        while (true){

        }
    }
}
