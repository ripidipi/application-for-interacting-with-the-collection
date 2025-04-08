package storage;

import collection.*;
import commands.Add;
import commands.Commands;
import exceptions.InsufficientNumberOfArguments;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A utility class to populate a collection from a file.
 * This class reads a CSV file, processes the data, and adds study groups to the collection.
 */
public class FillCollectionFromFile {

    /**
     * Reads a collection from a file and populates it.
     * The file name is predefined as "data/collection.csv".
     * If an error occurs, the method logs the error message.
     */
    public static void fillCollectionFromFile() {
        try {
            String fileName = Server.getCollectionPath();
            CommandsHandler.inputFromFile(fileName, FillCollectionFromFile::parseObject);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    private static StudyGroup parseObject(String input) {
        try {
            String[] inputSplit = input.split(",");
            Integer id = Integer.parseInt(inputSplit[0]);
            String name = inputSplit[1];
            Coordinates coordinates = new Coordinates(
                            Objects.equals(inputSplit[2], " ") ? null : Long.parseLong(inputSplit[2]),
                            Objects.equals(inputSplit[3], " ") ? null : Float.parseFloat(inputSplit[3]));
            Integer studentCount = Integer.parseInt(inputSplit[4]);
            FormOfEducation formOfEducation = Enum.valueOf(FormOfEducation.class, inputSplit[5].toUpperCase());
            Semester semester = Enum.valueOf(Semester.class, inputSplit[6].toUpperCase());
            Person groupAdmin = new Person(inputSplit[7],
                    LocalDate.parse(inputSplit[8], DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(),
                    Objects.equals(inputSplit[9], " ") ? null : Double.parseDouble(inputSplit[9]), inputSplit[10]);
            return new StudyGroup(id, name, coordinates, studentCount, formOfEducation, semester, groupAdmin);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace())); // TODO
        }
        return null;
    }
}
