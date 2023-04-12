package com.ddk.netty.nio;

import com.ddk.netty.nio.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 *  实例1 - 半包粘包问题
 */
@Slf4j
public class TestByteBufferExam {
    public static void main(String[] args) {
        /**
         * 假设接受到的数据以\n分割，如何正确接收并拆分数据
         */
        ByteBuffer source = ByteBuffer.allocate(64);
        source.put("Hello,World\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
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
