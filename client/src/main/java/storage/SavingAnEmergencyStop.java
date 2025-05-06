package storage;

import commands.Commands;
import exceptions.ServerDisconnect;
import io.DistributionOfTheOutputStream;
import io.OutputFileSettings;
import io.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Manages the emergency-stop data file used to resume command execution after unexpected interruptions.
 * <p>
 * As commands are processed, tokens representing each step are appended to the emergency-stop CSV file.
 * On startup, if the file exists, pending commands can be replayed to continue processing where it left off.
 * </p>
 */
public class SavingAnEmergencyStop {

    /**
     * Appends a message token to the emergency stop file.
     * <p>
     * Each token is separated by a comma, forming a single-line CSV of pending command data.
     * I/O errors are logged.
     * </p>
     *
     * @param message the string token to append
     */
    public static void addStringToFile(String message) {
        File file = new File(OutputFileSettings.getEmergencyFilePath());
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            writer.write(message + ',');
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Deletes the emergency stop file if it exists, clearing any recorded state.
     */
    public static void clearFile() {
        File file = new File(OutputFileSettings.getEmergencyFilePath());
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Checks whether there is a previous unfinished session by inspecting the emergency stop file.
     * <p>
     * Returns {@code true} if the file exists and contains at least one token; {@code false} otherwise.
     * </p>
     *
     * @return {@code true} if an emergency-stop file with pending data exists
     */
    public static boolean checkIfPreviousSession() {
        File file = new File(OutputFileSettings.getEmergencyFilePath());
        if (!file.exists()) return false;
        try (Scanner scanner = new Scanner(file)) {
            String line = scanner.nextLine();
            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1);
            }
            String[] values = line.split(",");
            return values.length > 1;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return false;
    }

    /**
     * Reads the pending command tokens from the emergency stop file and resumes execution.
     * <p>
     * The first token is interpreted as a {@link Commands} enum value, and the entire line is
     * passed to its {@code execute(...)} method in recovery mode "M". The resulting request is sent
     * to the server via {@link #sendRequestToServer(Request)}.
     * </p>
     */
    public static void recapCommandFromFile() {
        File file = new File(OutputFileSettings.getEmergencyFilePath());
        try (Scanner scanner = new Scanner(file)) {
            String line = scanner.nextLine();
            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1);
            }
            String[] values = line.split(",");
            Commands command = Commands.valueOf(values[0].toUpperCase());
            DistributionOfTheOutputStream.println("Continuing command: " + command.name());
            sendRequestToServer(command.execute(line, "M"));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Sends a request to the server and processes the response.
     * <p>
     * On {@link ServerDisconnect}, the exception is ignored to avoid disrupting recovery.
     * Other exceptions are logged.
     * </p>
     *
     * @param request the {@link Request} to send
     */
    public static void sendRequestToServer(Request<?> request) {
        try {
            DistributionOfTheOutputStream.printFromServer(
                    Server.interaction(request)
            );
        } catch (ServerDisconnect e) {}
        catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

}
