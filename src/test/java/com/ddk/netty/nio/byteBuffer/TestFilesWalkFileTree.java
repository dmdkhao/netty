package com.ddk.netty.nio.byteBuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 打印指定目录下的文件树,
 * 注意：遍历文件夹是由Files.walkFileTree完成的，
 * 而我们的匿名内部类 SimpleFileVisitor中的处理逻辑比如打印文件名是我们自定义的，这种模式就叫做访问者模式。
 */
@Slf4j
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {

    }

    private static void m2() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\myServer\\java8"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")) {
                    System.out.println(file);
                    jarCount.incrementAndGet();
                }
                return super.preVisitDirectory(file, attrs);
            }
        });
        System.out.println("jarCount = " + jarCount);
    }

    private static void m1() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\myServer\\java8"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dirCount=" + dirCount);
        System.out.println("fileCount=" + fileCount);
    }
}
