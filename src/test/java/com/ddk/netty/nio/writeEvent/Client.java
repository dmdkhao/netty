package com.ddk.netty.nio.writeEvent;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) {

        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            int count = 0;
            while(true) {
                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                count += sc.read(buffer);
                System.out.println(count);
                buffer.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
