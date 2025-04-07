package collection.fabrics;

import collection.Person;
import exceptions.EmptyLine;
import exceptions.RemoveOfTheNextSymbol;
import exceptions.ZeroValue;
import io.DistributionOfTheOutputStream;
import io.PrimitiveDataInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class PersonFabric {

    /**
     * Checks if the person object is fully initialized (non-null values for name, birthday, and passportID).
     *
     * @param person the person object to check.
     * @return true if all required fields are filled; false otherwise.
     */
    public static boolean isRightFill(Person person) {
        if (person == null) {
            return false;
        }
        return person.name() != null && person.birthday() != null && person.passportID() != null;
    }

    /**
     * Input manager for creating a Person object (group admin) from user input.
     *
     * @return a Person object created from user input.
     * @throws RemoveOfTheNextSymbol if the input contains invalid or unexpected symbols.
     * @throws EmptyLine if an empty string is provided where it's not allowed.
     * @throws ZeroValue if the provided numeric value is less than or equal to zero.
     */
    public static Person input() throws RemoveOfTheNextSymbol {
        DistributionOfTheOutputStream.println("Enter information about group admin");
        String name = PrimitiveDataInput.input("name", String.class);
        LocalDateTime birthday = PrimitiveDataInput.input("birthday data in format DD.MM.YYYY",
                LocalDateTime.class, false, false,
                true, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        Double height = PrimitiveDataInput.input("height", Double.class, false,
                true, false, null);
        String passportID = PrimitiveDataInput.input("passportID", String.class);
        return new Person(name, birthday, height, passportID);
    }

    /**
     * Creates a Person object (group admin) from input values, typically used for reading from a file.
     *
     * @param name the name of the group admin.
     * @param birthday the birthday of the group admin in string format.
     * @param height the height of the group admin.
     * @param passportID the passport ID of the group admin.
     * @return a Person object created from the input values.
     */
    public static Person inputFromFile(String name, String birthday, String height, String passportID) {
        return new Person(PrimitiveDataInput.inputFromFile("groupAdminName", name, String.class),
                PrimitiveDataInput.inputFromFile("adminBirthday", birthday, LocalDateTime.class,
                        false, false,
                        true, Person.getBirthdayFormatter(), false),
                PrimitiveDataInput.inputFromFile("adminHeight", height, Double.class,
                        false, true,
                        false, null, false),
                PrimitiveDataInput.inputFromFile("adminPassportID", passportID, String.class));
    }

    /**
     * Processes mixed input data to create a Person object from an array of input strings.
     * It checks each element in the input array and fills the respective fields for a Person object.
     *
     * @param inputSplit an array of input strings.
     * @return a Person object created from the array of input strings, or null if any required field is missing.
     */
    public static Person inputMixed(String[] inputSplit) {
        int index = 1;
        Person groupAdmin = PersonMixedInput(inputSplit, index);
        return isRightFill(groupAdmin) ? groupAdmin : null;
    }

    /**
     * Helper method for processing mixed input data to create a Person object.
     *
     * @param inputSplit an array of input strings.
     * @param index the current index in the input array.
     * @return a Person object created from the array of input strings.
     */
    static Person PersonMixedInput(String[] inputSplit, int index) {
        String adminName = (index < inputSplit.length) ?
                PrimitiveDataInput.inputFromFile("groupAdminName", inputSplit[index++], String.class) :
                PrimitiveDataInput.input("group admin name", String.class);

        LocalDateTime birthday = (index < inputSplit.length) ?
                PrimitiveDataInput.inputFromFile("adminBirthday", inputSplit[index++], LocalDateTime.class,
                        false, false,
                        true, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"), false) :
                PrimitiveDataInput.input("admin birthday data in format DD.MM.YYYY",
                        LocalDateTime.class, false, false,
                        true, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Double height = (index < inputSplit.length) ?
                (inputSplit[index].isBlank()) ?
                        null :
                        PrimitiveDataInput.inputFromFile("adminHeight", inputSplit[index],
                                Double.class, false, true,
                                false, null, false) :
                PrimitiveDataInput.input("admin height", Double.class, false,
                        true, false, null);
                index++;
        String adminPassport = (index < inputSplit.length) ?
                PrimitiveDataInput.inputFromFile("adminPassportID", inputSplit[index++], String.class) :
                PrimitiveDataInput.input("admin passportID", String.class);
        return new Person(adminName, birthday, height, adminPassport);
    }

    /**
     * Factory method to create a {@link Person} object based on the provided input string and mode.
     * The method processes the input differently based on the mode specified:
     * <ul>
     * <li>"M" mode processes the input as mixed user input.</li>
     * <li>"F" mode processes the input as file-based input.</li>
     * <li>Default mode processes the input interactively.</li>
     * </ul>
     *
     * @param input the input string to create a {@link Person} object.
     * @param inputMode the mode that determines how the input is processed:
     *                  "M" for mixed input, "F" for file input, or default for interactive input.
     * @return a {@link Person} object created based on the input and mode.
     * @throws RemoveOfTheNextSymbol if an unexpected or invalid symbol is found in the input.
     */
    public static Person getPersonFrom(String input, String inputMode) throws RemoveOfTheNextSymbol {
        String[] inputSplit = input.split(",");
        if (inputMode.equalsIgnoreCase("M")) {
            return PersonFabric.inputMixed(inputSplit);
        } else if (inputMode.equalsIgnoreCase("F")) {
            return PersonFabric.inputFromFile(inputSplit[0], inputSplit[1], inputSplit[2], inputSplit[3]);
        }
        return PersonFabric.input();
    }

}
