package storage;

import commands.Commands;
import io.DistributionOfTheOutputStream;
import io.OutputFileSettings;
import io.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utility class for managing the emergency stop file.
 * Provides methods to add data to the file, clear the file, and resume execution from the file.
 */
public class SavingAnEmergencyStop {

    /**
     * Adds a message to the emergency stop file. The message is appended to the file.
     *
     * @param message The message to be added to the emergency stop file.
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
     * Clears the contents of the emergency stop file if it exists.
     * Deletes the file completely.
     */
    public static void clearFile() {

        File file = new File(OutputFileSettings.getEmergencyFilePath());

        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Recaps and continues the command execution from the emergency stop file.
     * If the file contains a command, it will be executed from where it was stopped.
     */
    public static void recapCommandFromFile() {

        File file = new File(OutputFileSettings.getEmergencyFilePath());

        try (Scanner scanner = new Scanner(file)) {
            String line = scanner.nextLine();
            if(line.endsWith(",")) {
                line = line.substring(0, line.length() - 1);
            }
            String[] values = line.split(",");
            Commands command = Enum.valueOf(Commands.class, values[0].toUpperCase());
            DistributionOfTheOutputStream.println("Continue work with command: " + command.name());
            sentRequestToServer(command.execute(line, "M"));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static void sentRequestToServer(RequestPair<?> request) {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(
                    new InetSocketAddress(Server.getServerHost(), Server.getServerPort()));

            DistributionOfTheOutputStream.printFromServer(Server.interaction(client, request));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Checks if the emergency stop file exists.
     *
     * @return true if the emergency stop file exists, false otherwise
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
            if (values.length > 0) {return true;}
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return false;
    }

}
