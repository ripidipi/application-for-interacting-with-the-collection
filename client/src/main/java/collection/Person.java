package collection;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Class representing a group administrator with personal details.
 * Includes name, birthday, height, and passport ID.
 */
public record Person(String name, LocalDateTime birthday, Double height, String passportID)
        implements Serializable {

    private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Returns the birthday formatted as a string using the pattern dd/MM/yyyy.
     *
     * @return the formatted birthday string
     */
    public String getBirthdayString() {
        return birthday.format(BIRTHDAY_FORMATTER);
    }

    /**
     * Returns a string representation of the Person, including all fields.
     *
     * @return a formatted string with name, formatted birthday, height, and passport ID
     */
    @Override
    public String toString() {
        return "Group admin {" +
                "name: " + name +
                "\tbirthday: " + getBirthdayString() +
                "\theight: " + (height == null ? "" : height) +
                "\tpassportID: " + passportID +
                '}';
    }

    /**
     * Converts the height to its string representation.
     * Returns a single space if height is null.
     *
     * @return height as string or space if null
     */
    public String heightToString() {
        return Optional.ofNullable(height)
                .map(Object::toString)
                .orElse(" ");
    }

    /**
     * Provides the formatter used for parsing and formatting birthdays.
     *
     * @return the DateTimeFormatter for birthdays
     */
    public static DateTimeFormatter getBirthdayFormatter() {
        return BIRTHDAY_FORMATTER;
    }
}
