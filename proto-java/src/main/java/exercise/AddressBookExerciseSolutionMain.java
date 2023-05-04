package exercise;

import com.example.tutorial.protos.AddressBook;
import com.example.tutorial.protos.Person;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Solution from:
 *  https://protobuf.dev/getting-started/javatutorial/
 */
class AddressBookExerciseSolutionMain {

    static Person promptForAddress(BufferedReader stdin) throws IOException {
        Person.Builder person = Person.newBuilder();

        System.out.print("Enter person ID: ");
        person.setId(Integer.parseInt(stdin.readLine()));

        System.out.print("Enter name: ");
        person.setName(stdin.readLine());

        System.out.print("Enter email address (blank for none): ");
        String email = stdin.readLine();
        if (email.length() > 0) {
            person.setEmail(email);
        }

        while (true) {
            System.out.print("Enter a phone number (or leave blank to finish): ");
            String number = stdin.readLine();
            if (number.length() == 0) {
                break;
            }

            Person.PhoneNumber.Builder phoneNumber =
                    Person.PhoneNumber.newBuilder().setNumber(number);

            System.out.print("Is this a mobile, home, or work phone? ");
            String type = stdin.readLine();
            switch (type) {
                case "mobile":
                    phoneNumber.setType(Person.PhoneType.MOBILE);
                    break;
                case "home":
                    phoneNumber.setType(Person.PhoneType.HOME);
                    break;
                case "work":
                    phoneNumber.setType(Person.PhoneType.WORK);
                    break;
                default:
                    System.out.println("Unknown phone type.  Using default.");
                    break;
            }

            person.addPhones(phoneNumber);
        }

        return person.build();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: AddPerson ADDRESS_BOOK_FILE");
            System.exit(-1);
        }

        var addressBook = AddressBook.newBuilder();

        // Read the existing address book.
        try (var fis = new FileInputStream(args[0])) {
            addressBook.mergeFrom(fis);
        } catch (FileNotFoundException e) {
            System.out.println(args[0] + ": File not found. Creating a new file.");
        }

        // Add an address.
        addressBook.addPeople(
                promptForAddress(new BufferedReader(new InputStreamReader(System.in))
        ));

        // Write the new address book back to disk.
        try(var output = new FileOutputStream(args[0])) {
            addressBook.build().writeTo(output);
        }
    }
}
