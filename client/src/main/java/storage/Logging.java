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
 * Logs are written to the path specified by {@link OutputFileSettings#getLoggingFilePath()}.
 */
public class Logging {

    /**
     * Initializes the logging system by deleting any existing log file and creating a new one.
     */
    public static void initialize() {
        File file = new File(OutputFileSettings.getLoggingFilePath());
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("Logging initialized" + System.lineSeparator());
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error: could not initialize log file");
        }
    }

    /**
     * Appends a log entry to the log file. If the log file does not exist, it is initialized first.
     *
     * @param message the log message to append
     */
    public static void log(String message) {
        File file = new File(OutputFileSettings.getLoggingFilePath());
        if (!file.exists()) {
            initialize();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            writer.write(message);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Logging error: could not write to log file");
        }
    }

    /**
     * Formats an error message including stack trace elements.
     *
     * @param errorMessage       the exception message
     * @param stackTraceElements the stack trace
     * @return formatted error message
     */
    public static String makeMessage(String errorMessage, StackTraceElement[] stackTraceElements) {
        String[] traceLines = new String[stackTraceElements.length];
        for (int i = 0; i < stackTraceElements.length; i++) {
            traceLines[i] = stackTraceElements[i].toString();
        }
        return "[ERROR] " + errorMessage + "\n" + Arrays.toString(traceLines);
    }
}
