package com.ddk.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        // 1. 创建文件输入流的channel
        try (FileChannel fileChannel = new FileInputStream("data.txt").getChannel()) {
            // 2. 创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            // 3. 将channel中的数据读入到buffer中
            while(true) {
                int length = fileChannel.read(byteBuffer);
                if (length == -1) {
                    break;
                }
                // 4. 打印buffer中的内容
                byteBuffer.flip();
                while(byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    log.debug("读取到内容{}", (char)b);
                }
                byteBuffer.flip();
            }
        } catch (IOException e) {
            log.error("遇到异常, e={}", e.getMessage());
        }
        // 5. 改进读取代码
    }
}
