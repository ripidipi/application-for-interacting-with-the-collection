package io;

/**
 * Holds file path settings used by the client for output, logging, and emergency stop data.
 * <p>
 * Provides centralized access to standard file locations under the client data directory.
 * </p>
 */
public class OutputFileSettings {

    /** Path to the client output file for script-mode messages. */
    private static final String OUTPUT_FILE_PATH = "client/data/output.txt";
    /** Path to the client log file for diagnostic messages. */
    private static final String LOGGING_FILE_PATH = "client/data/log.txt";
    /** Path to the emergency-stop CSV, used for rollback on failures. */
    private static final String EMERGENCY_FILE_PATH = "client/data/emergency_stop.csv";

    /**
     * Returns the file path where script-mode output is written.
     *
     * @return the output file path (never null)
     */
    public static String getOutputFilePath() {
        return OUTPUT_FILE_PATH;
    }

    /**
     * Returns the file path used for logging diagnostic and error messages.
     *
     * @return the logging file path (never null)
     */
    public static String getLoggingFilePath() {
        return LOGGING_FILE_PATH;
    }

    /**
     * Returns the file path for the emergency-stop CSV, which records
     * commands for potential rollback in case of interruption.
     *
     * @return the emergency stop file path (never null)
     */
    public static String getEmergencyFilePath() {
        return EMERGENCY_FILE_PATH;
    }
}
