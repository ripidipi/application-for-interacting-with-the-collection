package collection;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents two-dimensional coordinates for a study group, consisting of an x (Long) and y (Float) value.
 * Provides methods to obtain string representations and handle null coordinates gracefully.
 */
public record Coordinates(Long x, Float y) implements Serializable {

    /**
     * Returns a string representation of this Coordinates instance.
     * The format includes labeled x and y values, using fallback empty space for nulls.
     *
     * @return a formatted string showing the x and y coordinate values
     */
    @Override
    public String toString() {
        return "Coordinates {" +
                "x coordinate: " + xToString() +
                "\t y coordinate: " + yToString() +
                '}';
    }

    /**
     * Converts the x coordinate to its string representation.
     * If the x value is null, returns a single space string.
     *
     * @return the x coordinate as a string, or a space if x is null
     */
    public String xToString() {
        return Optional.ofNullable(x)
                .map(Object::toString)
                .orElse(" ");
    }

    /**
     * Converts the y coordinate to its string representation.
     * If the y value is null, returns a single space string.
     *
     * @return the y coordinate as a string, or a space if y is null
     */
    public String yToString() {
        return Optional.ofNullable(y)
                .map(Object::toString)
                .orElse(" ");
    }

}