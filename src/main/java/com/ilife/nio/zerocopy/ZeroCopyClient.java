package com.ilife.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ZeroCopyClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",7001));
        String name = "b+tree.jpg";
        FileChannel channel = new FileInputStream(name).getChannel();
        long start = System.currentTimeMillis();
        // 在linux下一个transferto可完成传输
        // windows下一次调用transferto 只能发送8M 大小的文件，超过8M需要分段传输
        long size = channel.transferTo(0, channel.size(), socketChannel);
        System.out.println("总计时间："+(System.currentTimeMillis()-start)+",大小："+size);
        channel.close();
    }
}
