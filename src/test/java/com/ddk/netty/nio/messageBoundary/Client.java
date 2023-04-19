package com.ddk.netty.nio.messageBoundary;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

@Slf4j
public class Client {
    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("localhost", 8080));
            sc.write(Charset.defaultCharset().encode("01234567\n89abcdef"));
            sc.write(Charset.defaultCharset().encode("0123456789abcdef\n"));
            log.debug("dsa");
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
