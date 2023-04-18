package com.ddk.netty.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.ddk.netty.nio.utils.ByteBufferUtil.debugRead;

/**
 * 使用 nio 来理解阻塞模式
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 1. 开启服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2. 绑定端口
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        List<SocketChannel> scList = new ArrayList<>();

        // 3. 与客户端建立连接
        while (true) {
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                // 4. 保存客户端信息
                sc.configureBlocking(false);
                scList.add(sc);
                log.debug("Connected,{}", sc);
            }
            for (SocketChannel scc : scList) {
                int read = scc.read(buffer);
                if (read > 0) {
                    // 5. 获取客户端发来的消息
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                }
            }
        }

    }

    /**
     * 阻塞的服务器
     *
     * @throws IOException
     */
    private static void blockServer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        // 1. 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        // 3. 保存客户端信息
        List<SocketChannel> socketChannelList = new ArrayList<>();
        while (true) {
            // 3. 与客户端建立连接
            log.debug("connecting....");
            SocketChannel sc = ssc.accept();        //serverSocketChannel.accept是阻塞的
            log.debug("connected..{}", sc);
            socketChannelList.add(sc);
            for (SocketChannel scChannel : socketChannelList) {
                // 4. 读取客户端通道中的信息
                log.debug("before read...");
                scChannel.read(buffer);     //channel.read是阻塞的
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read...");
            }
        }
    }
}
