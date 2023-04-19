package com.ddk.netty.nio.messageBoundary;

import com.ddk.netty.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 消息边界问题
 *      解决消息边界问题的方案
 *          方案一：固定长度不足则用空补齐
 *          方案二：指定特殊分割字符来分割包
 *          方案三：先发送内容的长度，根据长度分配buffer
 *      本Demo使用方案二， 若内容大于buffer的初始长度则扩容
 *
 *      * SelectionKey key = channel.register(selector, option, attach);
 *
 */
@Slf4j
public class Server {
    public static void main(String[] args) {
        try{
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            Selector selector = Selector.open();
            SelectionKey selectionKey = ssc.register(selector, 0, null);
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);
            ssc.bind(new InetSocketAddress(8080));
            while(true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    log.info("key={}", key);
                    iterator.remove();
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = serverSocketChannel.accept();
                        log.debug("sc={}", sc);
                        sc.configureBlocking(false);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
                        SelectionKey scKey = sc.register(selector, 0, byteBuffer);
                        scKey.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel)key.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        int read = sc.read(byteBuffer);
                        if (read == -1) {
                            key.cancel();
                        }else {
                            split(byteBuffer);
                            if (byteBuffer.position() == byteBuffer.limit()) {
                                // 给byteBuffer扩容
                                ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                                byteBuffer.flip();
                                newBuffer.put(byteBuffer);
                                key.attach(newBuffer);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if ( source.get(i) == '\n') {
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length ; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        source.compact();
    }
}
