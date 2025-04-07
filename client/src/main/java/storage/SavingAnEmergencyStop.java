package storage;

import commands.Commands;
import io.DistributionOfTheOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utility class for managing the emergency stop file.
 * Provides methods to add data to the file, clear the file, and resume execution from the file.
 */
public class SavingAnEmergencyStop {

    /**
     * Name of the primary emergency stop file.
     */
    static final String emergencyFile = "client/data/emergency_stop.csv";

    /**
     * Adds a message to the emergency stop file. The message is appended to the file.
     *
     * @param message The message to be added to the emergency stop file.
     */
    public static void addStringToFile(String message) {

        File file = new File(emergencyFile);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            writer.write(message + ',');
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Clears the contents of the emergency stop file if it exists.
     * Deletes the file completely.
     */
    public static void clearFile() {

        File file = new File(emergencyFile);

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Recaps and continues the command execution from the emergency stop file.
     * If the file contains a command, it will be executed from where it was stopped.
     */
    public static void recapCommandFromFile() {

        File file = new File(emergencyFile);

        try (Scanner scanner = new Scanner(file)) {
            String line = scanner.nextLine();
            if(line.endsWith(",")) {
                line = line.substring(0, line.length() - 1);
            }
            String[] values = line.split(",");
            Commands command = Enum.valueOf(Commands.class, values[0].toUpperCase());
            DistributionOfTheOutputStream.println("Continue work with command: " + command.name());
            command.execute(line, "M");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Checks if the emergency stop file exists.
     *
     * @return true if the emergency stop file exists, false otherwise
     */
    public static boolean checkIfFile() {
        File file = new File(emergencyFile);

        return file.exists();
    }

}
