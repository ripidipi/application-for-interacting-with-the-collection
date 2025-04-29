package storage;

import io.DistributionOfTheOutputStream;
import io.OutputFileSettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utility class for writing diagnostic and error logs to a file.
 * <p>
 * Provides methods to initialize the log, append messages, and format exceptions
 * with stack trace details. Logs are written to the path specified by
 * {@link OutputFileSettings#getLoggingFilePath()}.
 * </p>
 */
public class Logging {

    /**
     * Initializes the logging system by deleting any existing log file and creating a new one
     * with an initialization header. If initialization fails, a console message is printed.
     */
    public static void initialize() {
        File file = new File(OutputFileSettings.getLoggingFilePath());
        if (file.exists()) {
            file.delete();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(OutputFileSettings.getLoggingFilePath()), StandardCharsets.UTF_8)) {
            writer.write("Logging initialized" + System.lineSeparator());
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error: could not initialize log file");
        }
    }

    /**
     * Appends a log entry to the log file. If the log file does not exist, it is initialized first.
     * <p>
     * Each log entry is followed by a newline. I/O errors during logging will print
     * a console message but will not interrupt application flow.
     * </p>
     *
     * @param message the log message to append
     */
    public static void log(String message) {
        File file = new File(OutputFileSettings.getLoggingFilePath());
        if (!file.exists()) {
            initialize();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(OutputFileSettings.getLoggingFilePath(), true), StandardCharsets.UTF_8)) {
            writer.write(message);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error: could not write to log file");
        }
    }

    /**
     * Formats an error message including stack trace elements.
     * <p>
     * Prepends an "[ERROR] " tag to the provided message, then appends the
     * stack trace elements in array form. Intended for use as input to {@link #log(String)}.
     * </p>
     *
     * @param errorMessage         the exception message or description
     * @param stackTraceElements   the array of stack trace elements from an exception
     * @return a composite string containing the error tag, message, and stack trace
     */
    public static String makeMessage(String errorMessage, StackTraceElement[] stackTraceElements) {
        String[] traceLines = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            traceLines[i] = stackTraceElements[i].toString();
        }
        return "[ERROR] " + errorMessage + "\n" + Arrays.toString(traceLines);
    }

}
