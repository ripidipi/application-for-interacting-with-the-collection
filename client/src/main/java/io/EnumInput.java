package io;

import storage.SavingAnEmergencyStop;
import commands.Exit;
import exceptions.IncorrectConstant;
import exceptions.RemoveOfTheNextSymbol;

import java.util.ArrayList;
import java.util.Scanner;

import static io.EnumTransform.TransformToEnum;

/**
 * Utility for reading and validating enum values from standard input.
 * <p>
 * Prompts the user to enter a constant name of the specified enum type,
 * converts the input string to the enum constant, and records the input
 * for emergency rollback support.
 * </p>
 *
 * @see EnumTransform#TransformToEnum(Class, String)
 * @see SavingAnEmergencyStop
 */
public class EnumInput {

    /** Scanner for reading console input; shared across calls. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user to enter a value matching one of the enum's constants,
     * reads a line from the console, converts it to the enum constant, and
     * returns the result.
     * <p>
     * Displays the enum's simple name followed by a list of allowed values.
     * Exits the application if end-of-input is reached.
     * </p>
     *
     * @param enumType the {@code Class} object of the desired enum type
     * @param <T>      the enum type
     * @return the enum constant corresponding to the user input
     * @throws RemoveOfTheNextSymbol if no input is available (EOF)
     * @throws IncorrectConstant     if the entered string is not a valid constant name
     */
    static <T extends Enum<T>> T inputAssistant(Class<T> enumType) {
        T[] constants = enumType.getEnumConstants();
        ArrayList<String> names = new ArrayList<>();
        for (T c : constants) {
            names.add(c.name());
        }
        DistributionOfTheOutputStream.print(
                "Enter " + enumType.getSimpleName() + " " + names.toString().toLowerCase() + ": "
        );
        if (!scanner.hasNextLine()) {
            new Exit().execute("", "");
            throw new RemoveOfTheNextSymbol("No more input");
        }
        String input = scanner.nextLine().trim().toUpperCase();
        return TransformToEnum(enumType, input);
    }

    /**
     * Reads an enum constant from the console, logs the selection for emergency rollback,
     * and returns the value.
     * <p>
     * Internally calls {@link #inputAssistant(Class)} and appends the chosen name
     * to the emergency-stop log via {@link SavingAnEmergencyStop}.
     * </p>
     *
     * @param enumType the {@code Class} object of the desired enum type
     * @param <T>      the enum type
     * @return the enum constant selected by the user
     * @throws IncorrectConstant     if the input does not match any constant name
     * @throws RemoveOfTheNextSymbol if end-of-input is reached during prompt
     */
    public static <T extends Enum<T>> T inputFromConsole(Class<T> enumType) {
        T result = inputAssistant(enumType);
        SavingAnEmergencyStop.addStringToFile(result.name());
        return result;
    }
}
