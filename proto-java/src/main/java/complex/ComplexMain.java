package complex;

import example.complex.ComplexOuterClass;

import java.util.List;

public class ComplexMain {

    public static void main(String[] args) {
        var message = ComplexOuterClass.Complex.newBuilder()
                .setOneDummy(newDummy(55, "One Dummy"))
                .addAllDummies(List.of(
                        newDummy(66, "Second Dummy"),
                        newDummy(67, "Third Dummy"),
                        newDummy(68, "Fourth Dummy")
                ))
                .build();

        System.out.println(message);
    }

    private static ComplexOuterClass.Dummy newDummy(int id, String name) {
        return ComplexOuterClass.Dummy.newBuilder()
                .setId(id)
                .setName(name)
                .build();
    }
}
