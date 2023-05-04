package options;

import com.example.options.AnotherDummy;

public class OptionsMain {

    public static void main(String[] args) {
        AnotherDummy dummy = AnotherDummy.newBuilder()
                .setId(42)
                .build();

        System.out.println(dummy);
    }
}
