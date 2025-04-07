package storage;

import io.DistributionOfTheOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Logging utility class for handling log messages.
 * This class provides methods to initialize, log, and create error messages in a log file.
 */
public class Logging {

    /**
     * Name of the primary log file.
     */
    static final String fileName = "client/data/log.txt";

    /**
     * Initializes the log file by deleting any existing file and writing an initialization message.
     * This method will overwrite the existing log file if it already exists.
     * If there is an error during initialization, it will be caught and printed as a message.
     */
    public static void initialize() {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write("Logging Initialized \n");
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error");
        }
    }


    /**
     * Logs a message to the log file.
     * If the log file does not exist, it will be initialized.
     *
     * @param message The message to be logged.
     */
    public static void log(String message) {

        File file = new File(fileName);

        if (!file.exists()) {
            initialize();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8)) {
            writer.write(message);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error");
        }
    }

    /**
     * Creates an error message from a given stack trace.
     * This method formats the stack trace and combines it with an error message string.
     *
     * @param stackTraceElements The stack trace elements to be included in the error message.
     * @return A formatted error message string containing the error message and stack trace.
     */
    public static String makeMessage(String errorMessage, StackTraceElement[] stackTraceElements) {
        String[] strings = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            strings[i] = stackTraceElements[i].toString() + '\n';
        }
        return "[ERROR] " + errorMessage + '\n' + Arrays.toString(strings) + '\n';
    }

}
