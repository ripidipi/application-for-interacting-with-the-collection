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

import static io.PrimitiveDataInput.inputAssistant;

/**
 * Utility class for parsing and validating raw input strings into required data types.
 * <p>
 * Supports conversion to primitive wrapper types and date/time, with configurable
 * validation rules for empty input, non-positive numeric values, and future dates.
 * On validation failure, optionally reprompts user or returns null for file-mode input.
 * </p>
 *
 * @see PrimitiveDataInput
 */
public class PrimitiveDataTransform {

    /**
     * Attempts to parse a string into {@link LocalDateTime} using the provided formatter.
     * Falls back to parsing as {@link LocalDate} at the start of day if initial parse fails.
     *
     * @param input     the raw date/time string
     * @param formatter the formatter to use for parsing
     * @return the parsed {@code LocalDateTime}
     * @throws IncorrectValue if input cannot be parsed as date or datetime
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
     * Transforms and validates an input string into the specified type.
     * <p>
     * Applies validation rules (empty line, zero value, future date) and parsing logic.
     * On validation or parsing failure:
     * <ul>
     *   <li>For interactive mode, prints errors and re-prompts via {@link PrimitiveDataInput#inputAssistant}.</li>
     *   <li>For file mode, returns null.</li>
     * </ul>
     * Unexpected exceptions are logged via {@link Logging}.
     * </p>
     *
     * @param name                 descriptive label used in error messages
     * @param type                 the target class (e.g., Integer.class, LocalDateTime.class)
     * @param emptyLineCheck       if true, rejects blank input with {@link EmptyLine}
     * @param zeroValueCheck       if true, rejects non-positive numbers with {@link ZeroValue}
     * @param dateInTheFutureCheck if true, rejects future dates with {@link DataInTheFuture}
     * @param input                the raw input string to transform
     * @param fileMode             if true, input originates from file and failures return null
     * @param formatter            date formatter for parsing date/time types
     * @param muteMode             if true, suppresses error messages
     * @param <T>                  resulting data type
     * @return the parsed and validated value, or null in file mode on failure
     * @throws EmptyLine       if blank input and emptyLineCheck is enabled
     * @throws ZeroValue       if numeric input is <= 0 and zeroValueCheck is enabled
     * @throws DataInTheFuture if date input is in the future and dateInTheFutureCheck is enabled
     */
    public static <T> T transformToRequiredType(String name,
                                                Class<T> type,
                                                Boolean emptyLineCheck,
                                                Boolean zeroValueCheck,
                                                Boolean dateInTheFutureCheck,
                                                String input,
                                                Boolean fileMode,
                                                DateTimeFormatter formatter,
                                                Boolean muteMode)
            throws EmptyLine, ZeroValue, DataInTheFuture {
        try {
            dataValidator(input, name, type, formatter,
                    emptyLineCheck, zeroValueCheck, dateInTheFutureCheck);
            return type.cast(
                    dataParser(input, type, formatter)
            );
        } catch (EmptyLine | ZeroValue | DataInTheFuture | IncorrectValue e) {
            if (!muteMode) {
                DistributionOfTheOutputStream.println(e.getMessage());
            }
        } catch (NumberFormatException e) {
            if (!muteMode) {
                DistributionOfTheOutputStream.println("Invalid input. Try again");
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        if (fileMode) {
            return null;
        }
        // Interactive mode: reprompt
        return inputAssistant(name, type, emptyLineCheck,
                zeroValueCheck, dateInTheFutureCheck, formatter);
    }

    /**
     * Validates raw input against configured rules before parsing.
     *
     * @param input                the raw input string
     * @param name                 label used in exception messages
     * @param type                 target type class
     * @param formatter            date/time formatter
     * @param emptyLineCheck       if true, checks for empty input
     * @param zeroValueCheck       if true, checks numeric positivity
     * @param dateInTheFutureCheck if true, checks date not in future
     * @param <T>                  target type
     * @throws EmptyLine          if blank input and emptyLineCheck is enabled
     * @throws ZeroValue          if numeric <= 0 and zeroValueCheck is enabled
     * @throws DataInTheFuture    if parsed date is after now and dateInTheFutureCheck is enabled
     * @throws IncorrectValue     if date parsing fails
     */
    private static <T> void dataValidator(String input,
                                          String name,
                                          Class<T> type,
                                          DateTimeFormatter formatter,
                                          Boolean emptyLineCheck,
                                          Boolean zeroValueCheck,
                                          Boolean dateInTheFutureCheck)
            throws EmptyLine, ZeroValue, DataInTheFuture, IncorrectValue {
        if (emptyLineCheck && input.isBlank()) {
            throw new EmptyLine(name);
        }
        if (Number.class.isAssignableFrom(type)) {
            Number num = (Number) dataParser(input, type, formatter);
            if (zeroValueCheck && !input.isBlank() && num.doubleValue() <= 0) {
                throw new ZeroValue(name);
            }
        }
        if (type == LocalDateTime.class) {
            LocalDateTime date = applyFormatter(input, formatter);
            if (dateInTheFutureCheck && date.isAfter(LocalDateTime.now())) {
                throw new DataInTheFuture(name);
            }
        }
    }

    /**
     * Parses the raw input into the specified type after validation.
     * Supports String, Integer, Double, Float, Long, and LocalDateTime.
     *
     * @param input     the raw input string
     * @param type      the target class for casting
     * @param formatter formatter for date/time types
     * @param <T>       the return type
     * @return the parsed value or null if input blank
     * @throws NumberFormatException if numeric parsing fails
     * @throws IncorrectValue       if type not supported or date parsing fails
     */
    private static <T> T dataParser(String input,
                                    Class<T> type,
                                    DateTimeFormatter formatter)
            throws NumberFormatException, IncorrectValue {
        if (input.isBlank()) {
            return type.cast(null);
        } else if (type == String.class) {
            return type.cast(input);
        } else if (type == Integer.class) {
            return type.cast(Integer.parseInt(input));
        } else if (type == Double.class) {
            return type.cast(Double.parseDouble(input));
        } else if (type == Float.class) {
            return type.cast(Float.parseFloat(input));
        } else if (type == Long.class) {
            return type.cast(Long.parseLong(input));
        } else if (type == LocalDateTime.class) {
            return type.cast(applyFormatter(input, formatter));
        }
        throw new IncorrectValue(type.toString());
    }

}
