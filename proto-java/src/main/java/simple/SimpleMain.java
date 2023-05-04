package simple;

import example.simple.SimpleOuterClass;

import java.util.List;

public class SimpleMain {

    public static void main(String[] args) {
        var message = SimpleOuterClass.Simple.newBuilder()
                .setId(42)
                .setIsSimple(true)
                .setName("My Name")
                .addSampleList(1)
                .addSampleList(2)
                .addSampleList(3)
                .addAllSampleList(List.of(4, 5, 6))
                .build();

        System.out.println(message);
    }
}
