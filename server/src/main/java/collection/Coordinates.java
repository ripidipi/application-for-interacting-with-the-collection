package collection;

import exceptions.EmptyLine;
import exceptions.RemoveOfTheNextSymbol;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents coordinates (x, y) for a study group.
 */
public record Coordinates(Long x, Float y) implements Serializable {

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

    @Override
    public String toString() {
        return "Coordinates {" +
                "X: " + xToString() +
                "\tY: " + yToString() + "}";
    }
}
