package com.ilife.nio.groupchat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ChatClient2 {
    private static final String HOST = "localhost";
    private static final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName = "user222222222222222222222222";

    public ChatClient2() {
        try {
            selector = Selector.open();
            socketChannel = socketChannel.open(new InetSocketAddress(HOST,PORT));
            socketChannel.configureBlocking(false);
            System.out.println(userName + " is ok");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String info) {
        try {
            info = userName + " 说：" + info;
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readServerMsg() {
        try {
            int count = selector.select();
            if (count > 0) { // 事件可以处理
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.write(buffer);
                        System.out.println(new String(buffer.array()).trim());
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 启动客户端
        ChatClient2 c = new ChatClient2();
        new Thread() {
            public void run() {
                while (true) {
                    c.readServerMsg();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        // 接受控制台输入，并发送消息
        Scanner s = new Scanner(System.in);
        while (s.hasNextLine()) {
            String s1 = s.nextLine();
            c.sendMsg(s1);
        }
    }
}
