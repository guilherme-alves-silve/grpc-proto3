package io;

import example.simple.SimpleOuterClass;

import java.io.*;

public class IOMain {

    public static void main(String[] args) {
        SimpleOuterClass.Simple message = SimpleOuterClass.Simple.newBuilder()
                .setId(42)
                .setName("John Wick")
                .setIsSimple(true)
                .build();

        var path = "simple.bin";
        writeTo(message, path);
        System.out.println(readFrom(path));
    }

    private static void writeTo(SimpleOuterClass.Simple message, String path) {
        try (var bos = new BufferedOutputStream(new FileOutputStream(path))) {
            message.writeTo(bos);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static SimpleOuterClass.Simple readFrom(String path) {
        try (var bis = new BufferedInputStream(new FileInputStream(path))) {
            return SimpleOuterClass.Simple.parseFrom(bis);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
