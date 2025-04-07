package io;

import storage.SavingAnEmergencyStop;
import commands.Exit;
import exceptions.IncorrectConstant;
import exceptions.RemoveOfTheNextSymbol;

import java.util.ArrayList;
import java.util.Scanner;

import static io.EnumTransform.TransformToEnum;

/**
 * A utility class for reading enum values from user input.
 * It allows for the conversion of string input to enum constants and handles user prompts to ensure correct input.
 */
public class EnumInput {

    final private static Scanner scanner = new Scanner(System.in);

    /**
     * A helper method to assist with reading and converting user input into an enum constant.
     *
     * @param enumType The enum class type that is expected to be received from the console.
     * @param <T> The type of the enum.
     * @return The enum constant corresponding to the user's input.
     */
    static <T extends Enum<T>> T inputAssistent(Class<T> enumType) {
        T[] enumConstants = enumType.getEnumConstants();
        ArrayList<String> enumValues = new ArrayList<>();
        for (T constant : enumConstants) {
            enumValues.add(constant.name());
        }
        DistributionOfTheOutputStream.print("Enter " + enumType.getSimpleName() + " " + enumValues.toString().trim().toLowerCase() + ": ");
        if (!scanner.hasNextLine()) {
            new Exit().execute("", "");
            throw new RemoveOfTheNextSymbol();
        }
        String input = scanner.nextLine().toUpperCase();
        return TransformToEnum(enumType, input);
    }

    /**
     * Reads an enum value from the console input.
     * Prompts the user to enter a valid enum value and converts the input string to an enum constant.
     *
     * @param enumType The enum class type that is expected to be received from the console.
     * @param <T> The type of the enum.
     * @return One of the constant enum values.
     * @throws IncorrectConstant if the input is not a valid value from the enum.
     */
    public static <T extends Enum<T>> T inputFromConsole(Class<T> enumType) {
        T result = inputAssistent(enumType);
        SavingAnEmergencyStop.addStringToFile(result.name());
        return result;
    }


}
