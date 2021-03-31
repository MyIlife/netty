package com.ilife.nio.buffer;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MyFileChannel {
    public static void main(String[] args) throws Exception {
        //write();
        //read();
        //readWrite();
        transfer();
    }
    public static void write() throws Exception{
        String s = "你好";
        FileOutputStream fileOutputStream = new FileOutputStream("D://testFile.txt");
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建缓冲区
        ByteBuffer b = ByteBuffer.allocate(1024);
        // 字符串放入buffer
        b.put(s.getBytes());
        // 对buffer读写反转
        b.flip();
        // 将buffer数据写入channel
        fileChannel.write(b);
        fileOutputStream.close();
    }
    public static void read() throws Exception{
        FileInputStream f = new FileInputStream("D://testFile.txt");
        FileChannel channel = f.getChannel();
        //创建缓冲区
        ByteBuffer b = ByteBuffer.allocate(1024*1024);
        // 将通道数据读入到buffer
        channel.read(b);
        b.flip();
        System.out.println(new String(b.array()));
        f.close();
    }
    public static void readWrite() throws Exception{
        FileInputStream fi = new FileInputStream("from.txt");
        FileChannel fiChannel = fi.getChannel();

        FileOutputStream fo = new FileOutputStream("to.txt");
        FileChannel foChannel = fo.getChannel();

        ByteBuffer b = ByteBuffer.allocate(10);
        int read = 0;
        // 将数据读入buffer
        while(read!=-1){
            // buffer复位
            b.clear();
            read = fiChannel.read(b);
            b.flip();
            foChannel.write(b);
        }
        fo.close();
        fi.close();
    }

    public static void transfer() throws Exception{
        FileInputStream fi = new FileInputStream("b+tree.jpg");
        FileOutputStream fo = new FileOutputStream("b+tree_copy.jpg");
        FileChannel fiChannel = fi.getChannel();
        FileChannel foChannel = fo.getChannel();
        foChannel.transferFrom(fiChannel,0,fiChannel.size());
        foChannel.close();
        fiChannel.close();
        fo.close();
        fi.close();
    }
}
