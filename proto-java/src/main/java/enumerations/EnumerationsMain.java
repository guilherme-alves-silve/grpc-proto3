package enumerations;

import example.enumerations.Enumerations;

public class EnumerationsMain {

    public static void main(String[] args) {
        Enumerations.Enumeration message = Enumerations.Enumeration.newBuilder()
                .setEyeColor(Enumerations.EyeColor.EYE_COLOR_BROWN)
                .build();

        System.out.println(message);
    }
}
