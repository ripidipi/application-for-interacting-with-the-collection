package collection;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Class representing a group admin (person) with their details.
 * It includes information like name, birthday, height, and passport ID.
 */
public record Person(String name, LocalDateTime birthday, Double height, String passportID)
        implements Serializable {

    private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formats and returns the birthday of the group admin as a string.
     *
     * @return the formatted birthday string in "dd/MM/yyyy" format.
     */
    public String getBirthdayString() {
        return birthday.format(BIRTHDAY_FORMATTER);
    }

    /**
     * Converts the person object to a readable string format.
     *
     * @return a string representation of the group admin's details.
     */
    @Override
    public String toString() {
        return "Group admin {" +
                "name: " + name +
                "\tbirthday: " + getBirthdayString() +
                "\theight: " + (height == null ? "" : height) +
                "\tpassportID: " + passportID + '}';
    }

    /**
     * Converts the height to a string, handling null values.
     *
     * @return the height as a string or an empty string if null.
     */
    public String heightToString() {
        return (Optional.ofNullable(height).orElse(Double.valueOf(" "))).toString();
    }

    public static DateTimeFormatter getBirthdayFormatter() {
        return BIRTHDAY_FORMATTER;
    }
}
