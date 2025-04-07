package io;

import storage.Logging;
import storage.SavingAnEmergencyStop;
import commands.Exit;
import exceptions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

import static io.PrimitiveDataTransform.transformToRequiredType;

/**
 * A utility class for reading and transforming basic data types from user input or file.
 * This class handles the conversion of various input types, such as numbers and dates, with validation for empty input,
 * zero values, and future dates, and also provides file input handling.
 */
public class PrimitiveDataInput {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Assists in reading and transforming user input into the required data type.
     * Validates input for empty lines, zero values, and future dates based on the provided checks.
     *
     * @param name                 The name of the expected input.
     * @param type                 The expected data type.
     * @param emptyLineCheck       Whether to check for empty input.
     * @param zeroValueCheck       Whether to check for zero or negative values.
     * @param dateInTheFutureCheck Whether to check if a date is in the future.
     * @param formatter            The formatter to use for date parsing.
     * @param <T>                  The type of the expected data.
     * @return The transformed user input.
     * @throws RemoveOfTheNextSymbol if there is an issue with the input.
     */
    static <T> T inputAssistent(String name, Class<T> type, Boolean emptyLineCheck,
                                Boolean zeroValueCheck,
                                Boolean dateInTheFutureCheck,
                                DateTimeFormatter formatter) throws RemoveOfTheNextSymbol {
        DistributionOfTheOutputStream.print("Enter " + name + ": ");
        if (!scanner.hasNextLine()) {
            new Exit().execute("", "");
            throw new RemoveOfTheNextSymbol();
        }
        String input = scanner.nextLine();
        return transformToRequiredType(name, type, emptyLineCheck,
                zeroValueCheck, dateInTheFutureCheck, input,
                false, formatter, false);
    }

    /**
     * Reads and transforms user input into a specified data type with validation for empty lines, zero values,
     * and future dates.
     *
     * @param name                 The name of the expected input.
     * @param type                 The expected data type.
     * @param emptyLineCheck       Whether to check for empty input.
     * @param zeroValueCheck       Whether to check for zero or negative values.
     * @param dateInTheFutureCheck Whether to check if a date is in the future.
     * @param formatter            The formatter to use for date parsing.
     * @param <T>                  The type of the expected data.
     * @return The transformed user input.
     * @throws EmptyLine       If input is empty and emptyLineCheck is enabled.
     * @throws ZeroValue       If input is a non-positive number and zeroValueCheck is enabled.
     * @throws DataInTheFuture If input is a future date and dateInTheFutureCheck is enabled.
     */
    public static <T> T input(String name, Class<T> type, Boolean emptyLineCheck, Boolean zeroValueCheck,
                              Boolean dateInTheFutureCheck, DateTimeFormatter formatter) throws RemoveOfTheNextSymbol {
        T result = inputAssistent(name, type, emptyLineCheck,
                zeroValueCheck, dateInTheFutureCheck, formatter);
        SavingAnEmergencyStop.addStringToFile(result == null ? " " : result.toString());
        return result;
    }

    /**
     * Reads and transforms user input with default validation settings for empty lines, zero values,
     * and future dates.
     *
     * @param name The name of the expected input.
     * @param type The expected data type.
     * @param <T>  The type of the expected data.
     * @return The transformed user input.
     * @throws EmptyLine       If input is empty.
     * @throws ZeroValue       If input is a non-positive number.
     * @throws DataInTheFuture If input is a future date.
     */
    public static <T> T input(String name, Class<T> type) throws RemoveOfTheNextSymbol {
        T result = inputAssistent(name, type, true, true, true,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        SavingAnEmergencyStop.addStringToFile(result == null ? " " : result.toString());
        return result;
    }

    /**
     * Transforms an input string from a file into a specified data type with validation for empty lines,
     * zero values, and future dates.
     *
     * @param name  The name of the expected input.
     * @param input The input string to transform.
     * @param type  The expected data type.
     * @param <T>   The type of the expected data.
     * @return The transformed input.
     * @throws EmptyLine       If input is empty.
     * @throws ZeroValue       If input is a non-positive number.
     * @throws DataInTheFuture If input is a future date.
     */
    public static <T> T inputFromFile(String name, String input, Class<T> type) {
        return transformToRequiredType(name, type, true, true, true,
                (Objects.equals(input, " ") ? "" : input),
                true, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"), false);
    }

    /**
     * Transforms an input string from a file into a specified data type with custom validation settings.
     *
     * @param name                 The name of the expected input.
     * @param input                The input string to transform.
     * @param type                 The expected data type.
     * @param emptyLineCheck       Whether to check for empty input.
     * @param zeroValueCheck       Whether to check for zero or negative values.
     * @param dateInTheFutureCheck Whether to check if a date is in the future.
     * @param formatter            The formatter to use for date parsing.
     * @param muteMode             Whether to suppress error messages.
     * @param <T>                  The type of the expected data.
     * @return The transformed input.
     * @throws EmptyLine       If input is empty and emptyLineCheck is enabled.
     * @throws ZeroValue       If input is a non-positive number and zeroValueCheck is enabled.
     * @throws DataInTheFuture If input is a future date and dateInTheFutureCheck is enabled.
     */
    public static <T> T inputFromFile(String name, String input, Class<T> type, Boolean emptyLineCheck,
                                      Boolean zeroValueCheck, Boolean dateInTheFutureCheck,
                                      DateTimeFormatter formatter, Boolean muteMode) {
        return transformToRequiredType(name, type, emptyLineCheck, zeroValueCheck,
                dateInTheFutureCheck, (Objects.equals(input, " ") ? "" : input),
                true, formatter, muteMode);
    }

}