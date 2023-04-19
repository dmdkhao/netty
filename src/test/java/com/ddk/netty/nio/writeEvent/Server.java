package com.ddk.netty.nio.writeEvent;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * write可写事件
 */
@Slf4j
public class Server {
    public static void main(String[] args) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);

            Selector selector = Selector.open();
            SelectionKey sscKey = ssc.register(selector, 0, null);
            sscKey.interestOps(SelectionKey.OP_ACCEPT);

            ssc.bind(new InetSocketAddress(8080));
            while (true) {
                int select = selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < 30000000; i++) {
                            stringBuffer.append("a");
                        }
                        ByteBuffer buffer = Charset.defaultCharset().encode(stringBuffer.toString());
                        // 含有问题的写法，
//                        while(buffer.hasRemaining()) {
//                            int write = sc.write(buffer);
//                            System.out.println(write);
//                        }
                        // 正确写法
                        if (buffer.hasRemaining()) {
                            //关联写事件
                            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                            scKey.attach(buffer);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();;
                        int write = sc.write(buffer);
                        System.out.println(write);
                        if (!buffer.hasRemaining()) {
                            key.attach(null);
                            key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
