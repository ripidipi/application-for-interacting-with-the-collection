package collection.fabrics;

import collection.Person;
import exceptions.*;
import io.DistributionOfTheOutputStream;
import io.PrimitiveDataInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Factory class for creating and validating {@link Person} instances (group administrators).
 * Provides methods to check object completeness, prompt user input, and construct
 * Person objects from mixed or file-based input data.
 */
public class PersonFabric {

    /**
     * Checks if the {@link Person} object is fully initialized.
     * Validates that name, birthday, and passportID fields are non-null.
     *
     * @param person the Person object to check
     * @return true if all required fields are non-null; false otherwise
     */
    public static boolean isRightFill(Person person) {
        if (person == null) {
            return false;
        }
        return person.name() != null && person.birthday() != null && person.passportID() != null;
    }

    /**
     * Prompts the user to input details for a {@link Person} (group admin).
     * Reads and validates name, birthday (formatted as DD.MM.YYYY), height, and passport ID.
     *
     * @return a Person object created from user input
     * @throws RemoveOfTheNextSymbol if input contains invalid or unexpected symbols
     * @throws EmptyLine if an empty string is provided where disallowed
     * @throws ZeroValue if a numeric value is less than or equal to zero
     */
    public static Person input() throws RemoveOfTheNextSymbol, EmptyLine, ZeroValue {
        DistributionOfTheOutputStream.println("Enter information about group admin");
        String name = PrimitiveDataInput.input("name", String.class);
        LocalDateTime birthday = PrimitiveDataInput.input(
                "birthday data in format DD.MM.YYYY",
                LocalDateTime.class,
                /* canBeNull */ false,
                /* allowFraction */ false,
                /* allowMinus */ true,
                /* formatter */ DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );
        Double height = PrimitiveDataInput.input(
                "height", Double.class,
                /* canBeNull */ false,
                /* allowFraction */ true,
                /* allowMinus */ false,
                /* defaultValue */ null
        );
        String passportID = PrimitiveDataInput.input("passportID", String.class);
        return new Person(name, birthday, height, passportID);
    }

    /**
     * Creates a {@link Person} object from file-based input values.
     * Parses name, birthday (using Person's birthday formatter), height, and passport ID.
     *
     * @param name the name as a string
     * @param birthday the birthday string to parse
     * @param height the height string to parse
     * @param passportID the passport ID as a string
     * @return a Person object created from the parsed values
     */
    public static Person inputFromFile(String name, String birthday, String height, String passportID) {
        return new Person(
                PrimitiveDataInput.inputFromFile("groupAdminName", name, String.class),
                PrimitiveDataInput.inputFromFile(
                        "adminBirthday", birthday,
                        LocalDateTime.class,
                        /* canBeNull */ false,
                        /* allowFraction */ false,
                        /* allowMinus */ true,
                        /* formatter */ Person.getBirthdayFormatter(),
                        /* fromFile */ false
                ),
                PrimitiveDataInput.inputFromFile(
                        "adminHeight", height,
                        Double.class,
                        /* canBeNull */ false,
                        /* allowFraction */ true,
                        /* allowMinus */ false,
                        /* defaultValue */ null,
                        /* fromFile */ false
                ),
                PrimitiveDataInput.inputFromFile("adminPassportID", passportID, String.class)
        );
    }

    /**
     * Processes mixed input data to create a {@link Person} object.
     * Reads fields sequentially from the input array or prompts when missing.
     *
     * @param inputSplit an array of input strings
     * @return a Person object if all required fields are filled; null otherwise
     */
    public static Person inputMixed(String[] inputSplit) {
        int index = 1;
        Person groupAdmin = PersonMixedInput(inputSplit, index);
        return isRightFill(groupAdmin) ? groupAdmin : null;
    }

    /**
     * Helper for mixed input parsing: reads or prompts for each Person field.
     *
     * @param inputSplit an array of input strings
     * @param index the starting index for array parsing
     * @return a Person instance built from mixed input
     */
    static Person PersonMixedInput(String[] inputSplit, int index) {
        String adminName = (index < inputSplit.length)
                ? PrimitiveDataInput.inputFromFile("groupAdminName", inputSplit[index++], String.class)
                : PrimitiveDataInput.input("group admin name", String.class);

        LocalDateTime birthday = (index < inputSplit.length)
                ? PrimitiveDataInput.inputFromFile(
                "adminBirthday", inputSplit[index++], LocalDateTime.class,
                /* canBeNull */ false,
                /* allowFraction */ false,
                /* allowMinus */ true,
                /* formatter */ DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                /* fromFile */ false
        )
                : PrimitiveDataInput.input(
                "admin birthday data in format DD.MM.YYYY",
                LocalDateTime.class,
                /* canBeNull */ false,
                /* allowFraction */ false,
                /* allowMinus */ true,
                /* formatter */ DateTimeFormatter.ofPattern("dd.MM.yyyy")
        );

        Double height = (index < inputSplit.length)
                ? (inputSplit[index].isBlank()
                ? null
                : PrimitiveDataInput.inputFromFile(
                "adminHeight", inputSplit[index], Double.class,
                /* canBeNull */ false,
                /* allowFraction */ true,
                /* allowMinus */ false,
                /* defaultValue */ null,
                /* fromFile */ false
        )
        )
                : PrimitiveDataInput.input("admin height", Double.class, false, true, false, null);
        index++;

        String adminPassport = (index < inputSplit.length)
                ? PrimitiveDataInput.inputFromFile("adminPassportID", inputSplit[index++], String.class)
                : PrimitiveDataInput.input("admin passportID", String.class);

        return new Person(adminName, birthday, height, adminPassport);
    }

    /**
     * Factory method to create a {@link Person} based on an input string and mode.
     * Supports mixed ("M"), file-based ("F"), or interactive input modes.
     *
     * @param input the comma-separated input string
     * @param inputMode "M" for mixed, "F" for file, or other for interactive
     * @return a Person object created according to the specified mode
     * @throws RemoveOfTheNextSymbol if an invalid symbol is encountered
     * @throws InsufficientNumberOfArguments if too few arguments are provided
     * @throws IncorrectValue if the resulting Person is invalid
     */
    public static Person getPersonFrom(String input, String inputMode)
            throws RemoveOfTheNextSymbol, InsufficientNumberOfArguments, IncorrectValue {
        String[] inputSplit = input.split(",");
        Person person;
        if (inputMode.equalsIgnoreCase("M")) {
            person = PersonFabric.inputMixed(inputSplit);
        } else if (inputMode.equalsIgnoreCase("F")) {
            person = PersonFabric.inputFromFile(inputSplit[0], inputSplit[1], inputSplit[2], inputSplit[3]);
        } else {
            person = PersonFabric.input();
        }
        if (!isRightFill(person)) {
            throw new IncorrectValue(input);
        }
        return person;
    }
}
