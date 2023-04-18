package com.ddk.netty.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Selector基本使用
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));

            //1. create selector
            Selector selector = Selector.open();

            //2. register channel to selector
            SelectionKey selectionKey = ssc.register(selector, 0, null);

            //3. selectionKey interest accept event
            selectionKey.interestOps(SelectionKey.OP_ACCEPT);

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
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    log.debug("socketChannel={}", socketChannel);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
