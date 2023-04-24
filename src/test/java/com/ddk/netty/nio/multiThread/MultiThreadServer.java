package com.ddk.netty.nio.multiThread;

import com.ddk.netty.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *      多线程优化 nio
 *          * 原理：使用Boss-selector处理客户端连接事件，
 *                  使用Worker-selector启用多线程管理客户端其他事件(例如读写事件)
 *          * 问题：Worker-selector在注册sc时会被阻塞，因为代码无法保证boss线程中的"selector.select();"和worker线程中的"socketChannel.register(selector, SelectionKey.OP_READ, null);"的执行顺序。
 *                  如果worker线程先select()则会一直阻塞在这里，因为没有关注事件，进而导致boss线程中的socketChannel.register无法被执行，无法关注事件。
 *          * 解决：需要让socketChannel.register(selector, SelectionKey.OP_READ, null);成功执行.
 *                  方案一：将socketChannel.register(selector, SelectionKey.OP_READ, null);作为任务放在queue中，队列是进程通信的基础方式。
 *                  方案二：使用方法 selector.wakeup();
 */
@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        //1. 启动boss，监听accept
        Selector bossSelector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.register(bossSelector, SelectionKey.OP_ACCEPT, null);
        Worker worker = new Worker("worker-0");
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            log.debug("boss-select......");
            bossSelector.select();
            Set<SelectionKey> selectionKeys = bossSelector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    log.debug("connecting.......");
                    SocketChannel sc = serverSocketChannel.accept();
                    log.debug("boss-connected.");
                    sc.configureBlocking(false);
                    worker.register(sc);
                }
            }
        }
    }

    static class Worker implements Runnable {

        private String name;
        private Thread thread;
        private volatile Selector selector;
        private volatile boolean registerFlag;
        private ConcurrentLinkedQueue<Runnable> queue;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel socketChannel) throws IOException {
            if (!registerFlag) {
                //还没有注册
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                queue = new ConcurrentLinkedQueue<>();
                registerFlag = true;
            }
            log.debug("worker-register......");
            queue.add(() -> {
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();
            log.debug("worker-registed.");
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable poll = queue.poll();
                    if (poll != null) {
                        poll.run();
                    }
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isReadable()) {
                            SocketChannel sc = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            int read = sc.read(buffer);
                            if (read == -1) {
                                selectionKey.cancel();
                            } else {
                                buffer.flip();
                                ByteBufferUtil.debugAll(buffer);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
