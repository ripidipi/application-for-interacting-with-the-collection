package collection;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents coordinates (x, y) for a study group.
 * This class includes methods for input, validation, and conversion of coordinates.
 */
public record Coordinates(Long x, Float y) implements Serializable {

    /**
     * Returns a string representation of the Coordinates object.
     * It includes the x and y coordinates in a readable format.
     *
     * @return a string representation of the Coordinates object, displaying the x and y coordinates.
     */
    @Override
    public String toString() {
        return "Coordinates {" +
                "x coordinate: " + xToString() +
                "\ty coordinate: " + yToString() + '}';
    }

    /**
     * Converts the y coordinate to a string.
     * If the y coordinate is null, returns an empty space string.
     *
     * @return string representation of the y coordinate or an empty space if null.
     */
    public String yToString() {
        return Optional.ofNullable(y)
                .map(Object::toString)
                .orElse(" ");
    }

    /**
     * Converts the x coordinate to a string.
     * If the x coordinate is null, returns an empty space string.
     *
     * @return string representation of the x coordinate or an empty space if null.
     */
    public String xToString() {
        return Optional.ofNullable(x)
                .map(Object::toString)
                .orElse(" ");
    }

}
