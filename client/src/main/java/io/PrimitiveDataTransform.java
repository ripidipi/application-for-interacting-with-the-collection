package io;

import exceptions.DataInTheFuture;
import exceptions.EmptyLine;
import exceptions.IncorrectValue;
import exceptions.ZeroValue;
import storage.Logging;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static io.PrimitiveDataInput.inputAssistent;

public class PrimitiveDataTransform {

    /**
     * Parses a date-time string using a specified formatter.
     * Attempts to parse the input first as a `LocalDateTime`, and if that fails, as a `LocalDate`.
     *
     * @param input The input string to parse.
     * @param formatter The formatter to use.
     * @return The parsed `LocalDateTime`.
     */
    private static LocalDateTime applyFormatter(String input, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(input, formatter).atStartOfDay();
            } catch (DateTimeParseException e2) {
                throw new IncorrectValue(input);
            }
        }
    }

    /**
     * Transforms an input string into a specified data type with validation for empty lines, zero values,
     * and future dates.
     *
     * @param name The name of the expected input.
     * @param type The expected data type.
     * @param emptyLineCheck Whether to check for empty input.
     * @param zeroValueCheck Whether to check for zero or negative values.
     * @param dateInTheFutureCheck Whether to check if a date is in the future.
     * @param input The input string to transform.
     * @param fileMode Whether the input comes from a file.
     * @param formatter The formatter to use for date parsing.
     * @param muteMode Whether to suppress error messages.
     * @param <T> The type of the expected data.
     * @return The transformed input.
     * @throws EmptyLine        If input is empty and emptyLineCheck is enabled.
     * @throws ZeroValue        If input is a non-positive number and zeroValueCheck is enabled.
     * @throws DataInTheFuture  If input is a future date and dateInTheFutureCheck is enabled.
     */
    public static <T> T transformToRequiredType(String name, Class<T> type, Boolean emptyLineCheck,
                                                Boolean zeroValueCheck,
                                                Boolean dateInTheFutureCheck, String input, Boolean fileMode,
                                                DateTimeFormatter formatter, Boolean muteMode)
            throws EmptyLine, ZeroValue, DataInTheFuture {
        try {
            dataValidator(input, name, type, formatter, emptyLineCheck, zeroValueCheck, dateInTheFutureCheck);
            return type.cast(dataParser(input, type, formatter));
        } catch (EmptyLine | ZeroValue | DataInTheFuture | IncorrectValue e) {
            if (!muteMode)
                DistributionOfTheOutputStream.println(e.getMessage());
        } catch (NumberFormatException e) {
            if (!muteMode)
                DistributionOfTheOutputStream.println("Invalid input. Try again");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        if (fileMode) {
            return null;
        }
        return inputAssistent(name, type, emptyLineCheck, zeroValueCheck, dateInTheFutureCheck, formatter);
    }

    private static <T> void dataValidator(String input, String name, Class<T> type, DateTimeFormatter formatter,
                                          Boolean emptyLineCheck, Boolean zeroValueCheck, Boolean dateInTheFutureCheck
    ) throws EmptyLine, ZeroValue, DataInTheFuture, IncorrectValue {
        if (emptyLineCheck && input.isBlank())
            throw new EmptyLine(name);
        if (Number.class.isAssignableFrom(type)) {
            Number number = (Number) dataParser(input, type, formatter);
            if (zeroValueCheck && !input.isBlank() && number.doubleValue() <= 0)
                throw new ZeroValue(name);
        }
        if (type == LocalDateTime.class) {
            LocalDateTime date = applyFormatter(input, formatter);
            if (date.isAfter(LocalDateTime.now()) & dateInTheFutureCheck) {
                throw new DataInTheFuture(name);
            }
        }
    }

    private static <T> T dataParser(String input, Class<T> type, DateTimeFormatter formatter)
            throws NumberFormatException, IncorrectValue {
        if (input.isBlank())
            return type.cast(null);
        if (type == String.class) {
            return type.cast(input);
        } else if (type == Integer.class) {
            int value = Integer.parseInt(input);
            return type.cast(value);
        } else if (type == Double.class) {
            double value = Double.parseDouble(input);
            return type.cast(value);
        } else if (type == Float.class) {
            float value = Float.parseFloat(input);
            return type.cast(value);
        } else if (type == Long.class) {
            long value = Long.parseLong(input);
            return type.cast(value);
        } else if (type == LocalDateTime.class) {
            LocalDateTime date = applyFormatter(input, formatter);
            return type.cast(date);
        }
        throw new IncorrectValue(type.toString());
    }

}

