package oneofs;

import example.oneofs.Oneofs;

public class OneOfsMain {

    public static void main(String[] args) {

        Oneofs.Result message = Oneofs.Result.newBuilder()
                .setMessage("a message")
                .build();

        System.out.println("Has message: " + message.hasMessage());
        System.out.println("Has id: " + message.hasId());
        System.out.println(message);

        Oneofs.Result message2 = Oneofs.Result.newBuilder(message)
                .build();

        System.out.println("Has message: " + message2.hasMessage());
        System.out.println("Has id: " + message2.hasId());
        System.out.println(message2);

        Oneofs.Result message3 = Oneofs.Result.newBuilder()
                .setId(42)
                .build();

        System.out.println("Has message: " + message3.hasMessage());
        System.out.println("Has id: " + message3.hasId());
        System.out.println(message3);
    }
}
