package io;

public class OutputFileSettings {

    private static final String OUTPUT_FILE_PATH = "client/data/output.txt";
    private static final String LOGGING_FILE_PATH = "client/data/log.txt";
    private static final String EMERGENCY_FILE_PATH = "client/data/emergency_stop.csv";

    public static String getOutputFilePath() {
        return OUTPUT_FILE_PATH;
    }

    public static String getLoggingFilePath() {
        return LOGGING_FILE_PATH;
    }

    public static String getEmergencyFilePath() {
        return EMERGENCY_FILE_PATH;
    }
}
