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
            DBManager.requestStudyGroup("SELECT * FROM STUDY_GROUP");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
