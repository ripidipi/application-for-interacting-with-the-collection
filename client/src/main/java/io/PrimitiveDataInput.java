package io;

import storage.Logging;
import storage.SavingAnEmergencyStop;
import commands.Exit;
import exceptions.*;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;

import static io.PrimitiveDataTransform.transformToRequiredType;

/**
 * Utility for reading and converting primitive and simple object types from user input or file data.
 * <p>
 * Supports validation rules such as non-empty checks, non-zero numeric values, and future date detection.
 * Records each input value to an emergency-stop log for rollback in case of failure.
 * </p>
 *
 * @see PrimitiveDataTransform
 * @see SavingAnEmergencyStop
 */
public class PrimitiveDataInput {

    /** Shared scanner for console input. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Core helper: prompts for, reads, and transforms a single input value.
     * <p>
     * Displays a prompt with the given name, reads one line, and delegates
     * parsing and validation to {@link PrimitiveDataTransform#transformToRequiredType}.
     * Exits application on EOF.
     * </p>
     *
     * @param name                 descriptive label for the input prompt
     * @param type                 target class of the value (e.g., Integer.class, LocalDate.class)
     * @param emptyLineCheck       if true, throws {@link EmptyLine} on blank input
     * @param zeroValueCheck       if true, throws {@link ZeroValue} on non-positive numbers
     * @param dateInTheFutureCheck if true, throws {@link DataInTheFuture} on future dates
     * @param formatter            date formatter to use for date inputs (may be null for non-date types)
     * @param <T>                  resulting value type
     * @return parsed and validated value of type T
     * @throws RemoveOfTheNextSymbol if end-of-input is reached
     */
    static <T> T inputAssistant(String name, Class<T> type,
                                boolean emptyLineCheck,
                                boolean zeroValueCheck,
                                boolean dateInTheFutureCheck,
                                DateTimeFormatter formatter)
            throws RemoveOfTheNextSymbol {
        DistributionOfTheOutputStream.print("Enter " + name + ": ");
        if (!scanner.hasNextLine()) {
            new Exit().execute("", "");
            throw new RemoveOfTheNextSymbol("No more input");
        }
        String raw = scanner.nextLine();
        return transformToRequiredType(name, type,
                emptyLineCheck, zeroValueCheck, dateInTheFutureCheck,
                raw, false, formatter, false);
    }

    /**
     * Reads and validates user input from console, then logs it for emergency rollback.
     * <p>
     * Uses default validation: non-empty, positive numbers, and no future dates.
     * </p>
     *
     * @param name  prompt label
     * @param type  target class of the value
     * @param <T>   resulting type
     * @return validated and logged value
     * @throws EmptyLine         on blank input
     * @throws ZeroValue         on non-positive numbers
     * @throws DataInTheFuture   on future dates
     * @throws RemoveOfTheNextSymbol on end-of-input
     */
    public static <T> T input(String name, Class<T> type)
            throws RemoveOfTheNextSymbol {
        T value = inputAssistant(name, type,
                true, true, true,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        SavingAnEmergencyStop.addStringToFile(Objects.toString(value, ""));
        return value;
    }

    /**
     * Reads, validates, and logs a console input with custom validation flags.
     *
     * @param name                 prompt label
     * @param type                 target class
     * @param emptyLineCheck       enable empty-line validation
     * @param zeroValueCheck       enable non-positive number validation
     * @param dateInTheFutureCheck enable future-date validation
     * @param formatter            custom date formatter
     * @param <T>                  resulting type
     * @return validated and logged value
     * @throws RemoveOfTheNextSymbol on end-of-input
     */
    public static <T> T input(String name,
                              Class<T> type,
                              boolean emptyLineCheck,
                              boolean zeroValueCheck,
                              boolean dateInTheFutureCheck,
                              DateTimeFormatter formatter)
            throws RemoveOfTheNextSymbol {
        T value = inputAssistant(name, type,
                emptyLineCheck, zeroValueCheck, dateInTheFutureCheck,
                formatter);
        SavingAnEmergencyStop.addStringToFile(Objects.toString(value, ""));
        return value;
    }

    /**
     * Transforms a raw string (from file) into the specified type using default validation.
     * Does not log to emergency file since file inputs are pre-recorded.
     *
     * @param name  description for error messages
     * @param raw   raw input string
     * @param type  target class
     * @param <T>   resulting type
     * @return transformed value
     * @throws EmptyLine       on blank input
     * @throws ZeroValue       on non-positive numbers
     * @throws DataInTheFuture on future dates
     */
    public static <T> T inputFromFile(String name, String raw, Class<T> type)
            throws EmptyLine, ZeroValue, DataInTheFuture {
        return transformToRequiredType(name, type,
                true, true, true,
                Objects.equals(raw, " ") ? "" : raw,
                true, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"), false);
    }

    /**
     * Transforms a raw string (from file) with custom validation settings.
     *
     * @param name                 description for error messages
     * @param raw                  raw input string
     * @param type                 target class
     * @param emptyLineCheck       enable empty-line validation
     * @param zeroValueCheck       enable non-positive number validation
     * @param dateInTheFutureCheck enable future-date validation
     * @param formatter            date parser to use
     * @param muteMode             suppress error output if true
     * @param <T>                  resulting type
     * @return transformed value
     * @throws EmptyLine       on blank input when enabled
     * @throws ZeroValue       on non-positive numbers when enabled
     * @throws DataInTheFuture on future dates when enabled
     */
    public static <T> T inputFromFile(String name,
                                      String raw,
                                      Class<T> type,
                                      boolean emptyLineCheck,
                                      boolean zeroValueCheck,
                                      boolean dateInTheFutureCheck,
                                      DateTimeFormatter formatter,
                                      boolean muteMode)
            throws EmptyLine, ZeroValue, DataInTheFuture {
        return transformToRequiredType(name, type,
                emptyLineCheck, zeroValueCheck, dateInTheFutureCheck,
                Objects.equals(raw, " ") ? "" : raw,
                true, formatter, muteMode);
    }
}
