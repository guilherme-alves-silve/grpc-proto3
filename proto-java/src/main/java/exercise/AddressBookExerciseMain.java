package exercise;

import com.example.tutorial.protos.AddressBook;
import com.example.tutorial.protos.Person;
import com.google.protobuf.Timestamp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class AddressBookExerciseMain {

    public static void main(String[] args) {
        try {
            var path = "address_book.bin";
            var addressBookBuilder = AddressBook.newBuilder();
            if (Files.exists(Paths.get(path))) {
                try (var bis = new BufferedInputStream(new FileInputStream(path))) {
                    addressBookBuilder.mergeFrom(bis);
                }
                System.out.println(addressBookBuilder.build());
                return;
            }

            var richter = Person.newBuilder()
                    .setId(42)
                    .setName("Richter Belmont")
                    .setEmail("richard@vampirehunter.com")
                    .setLastUpdated(currentTimestamp())
                    .addAllPhones(List.of(
                            newPhoneNumber("(11)1512512-512521", Person.PhoneType.MOBILE),
                            newPhoneNumber("(15)1212412-176545", Person.PhoneType.WORK)
                    ))
                    .build();

            var alucard = Person.newBuilder()
                    .setId(2)
                    .setName("Alucard")
                    .setEmail("alucard@castlevania.com")
                    .addPhones(newPhoneNumber("(99)999999-99999", Person.PhoneType.WORK))
                    .build();

            var addressBook = addressBookBuilder
                    .addPeople(richter)
                    .addPeople(alucard)
                    .build();

            try (var bos = new BufferedOutputStream(new FileOutputStream(path))) {
                addressBook.writeTo(bos);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static Timestamp currentTimestamp() {
        long millis = System.currentTimeMillis();
        return Timestamp.newBuilder()
                .setSeconds(millis / 1000)
                .build();
    }

    private static Person.PhoneNumber newPhoneNumber(String number, Person.PhoneType type) {
        return Person.PhoneNumber.newBuilder()
                .setNumber(number)
                .setType(type)
                .build();
    }
}
