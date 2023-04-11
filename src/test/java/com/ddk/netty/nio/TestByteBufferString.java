package com.ddk.netty.nio;

import com.ddk.netty.nio.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * String与ByteBuffer相互转换
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        // 1 字符串转ByteBuffer
        // 1.1 buffer.put() , 注意，并没有flip()
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        ByteBufferUtil.debugAll(buffer1);

        // 1.2 charset, 会自动flip
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        ByteBufferUtil.debugAll(buffer2);

        // 1.3 wrap， 会自动flip
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        ByteBufferUtil.debugAll(buffer3);

        // 2 byteBuffer转String
        String s = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(s);

        buffer1.flip();
        String s2 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(s2);


    }
}
