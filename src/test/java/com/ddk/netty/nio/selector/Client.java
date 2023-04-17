package com.ddk.netty.nio.selector;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Selector-Client
 */
@Slf4j
public class Client {
    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            log.debug("connected");
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
