package collection.fabrics;

import collection.Coordinates;
import exceptions.EmptyLine;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import io.PrimitiveDataInput;

public class CoordinatesFabric {

    /**
     * Checks if the coordinates are correctly filled (not null).
     * Validates that both x and y coordinates are provided.
     *
     * @param coordinates the Coordinates object to check.
     * @return true if the coordinates are not null, false otherwise.
     */
    public static boolean isRightFill(Coordinates coordinates) {
        return coordinates != null;
    }

    /**
     * Prompts the user to input the coordinates (x and y).
     * Uses the {@link PrimitiveDataInput} class to read and validate the input.
     *
     * @return a new Coordinates object with the user-input values.
     * @throws RemoveOfTheNextSymbol if the input contains invalid or unexpected symbols.
     * @throws EmptyLine if the input is empty and not allowed.
     */
    public static Coordinates input() throws RemoveOfTheNextSymbol {
        DistributionOfTheOutputStream.println("Enter information about coordinates");
        Long x = PrimitiveDataInput.input("x coordinate", Long.class, false,
                false, false, null);
        Float y = PrimitiveDataInput.input("y coordinate", Float.class, false,
                false, false, null);
        return new Coordinates(x, y);
    }

    /**
     * Creates a Coordinates object from file input.
     * Uses {@link PrimitiveDataInput} to read and convert the x and y values from the file.
     *
     * @param x the x coordinate value as a string.
     * @param y the y coordinate value as a string.
     * @return a new Coordinates object with the values from the file, or null if the values are invalid.
     */
    public static Coordinates inputFromFile(String x, String y) {
        return new Coordinates(PrimitiveDataInput.inputFromFile("CoordinateX", x, Long.class,
                false, false, false, null, false),
                PrimitiveDataInput.inputFromFile("CoordinateY", y, Float.class, false,
                        false, false, null, false));
    }
}
