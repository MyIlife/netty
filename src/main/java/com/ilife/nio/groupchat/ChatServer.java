package com.ilife.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    public ChatServer() {
        // 得到选择器
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while (true) {
                int count = selector.select();
                if (count > 0) { //说明有事件可以处理
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel accept = listenChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            System.out.println(accept.getRemoteAddress() + ":上线了");
                        }
                        if (key.isReadable()) { // 接收到可读事件
                            this.read(key);
                        }
                        // 删除当前key防止重复操作
                        iterator.remove();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        SocketChannel channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            //
            if (count > 0) { //读取到数据
                String msg = new String(buffer.array());
                System.out.println("收到客户端【" + channel.getRemoteAddress() + "】消息：" + msg);
                this.sendMsgToOthers(msg, channel);
            }
        } catch (Exception e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了");
                // 离线处理
                key.cancel();
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 给其他channel发送
     *
     * @param msg
     * @param self 排除自己
     */
    private void sendMsgToOthers(String msg, SocketChannel self) throws Exception {
        System.out.println("服务器转发【" + self.getRemoteAddress() + "】的消息");
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != self) {
                SocketChannel socketChannel = (SocketChannel) channel;
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                // 将buffer的数据写入通道
                socketChannel.write(byteBuffer);
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.listen();
    }
}
