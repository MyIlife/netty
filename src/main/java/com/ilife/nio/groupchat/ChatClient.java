package com.ilife.nio.groupchat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

public class ChatClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public ChatClient() {
        try {
            selector = Selector.open();
            socketChannel = socketChannel.open(new InetSocketAddress(HOST,PORT));
            socketChannel.configureBlocking(false);
            //将channel 注册到selector
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + " is ok...");
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
                        channel.read(buffer);
                        String s = new String(buffer.array());
                        System.out.println(s.trim());
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
        final ChatClient c = new ChatClient();
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
