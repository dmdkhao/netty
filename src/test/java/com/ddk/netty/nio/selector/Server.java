package com.ddk.netty.nio.selector;

import com.ddk.netty.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Selector基本使用
 *
 *  selectionKey.cancel(); 取消事件
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // selector需要channel工作在非阻塞模式
            ssc.configureBlocking(false);

            //1. create selector
            Selector selector = Selector.open();

            //2. register channel to selector
            SelectionKey selectionKey = ssc.register(selector, 0, null);

            //3. selectionKey interest accept event
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);

            ssc.bind(new InetSocketAddress(8080));
            while (true) {
                //4. select
                selector.select();
                log.debug("selector selected");

                //5. get channel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    log.debug("key={}", key.toString());
                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = serverSocketChannel.accept();
                        log.debug("socketChannel={}", sc);
                        //register client channel to selector
                        sc.configureBlocking(false);
                        SelectionKey scSelectionKey = sc.register(selector, 0, null);
                        scSelectionKey.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        // client channel trigger read event
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        channel.read(buffer);
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                    }
//                    key.cancel();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
