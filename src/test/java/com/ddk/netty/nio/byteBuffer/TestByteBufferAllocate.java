package com.ddk.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(10));
        System.out.println(ByteBuffer.allocateDirect(10));
    }
}
