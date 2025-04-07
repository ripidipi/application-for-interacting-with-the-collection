package storage;

import commands.Commands;
import commands.Exit;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.ServerSetting;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
            sentRequestToServer(command.execute(line, "M"));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static void sentRequestToServer(RequestPair<?> request) {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(
                    new InetSocketAddress(ServerSetting.getServerHost(), ServerSetting.getServerPort()));

            // to bite
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(request);
            objectOut.flush();
            byte[] bytes = byteOut.toByteArray();

            // send
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            client.write(buffer);

            // take response
            ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);
            long startTime = System.currentTimeMillis();


            while (receiveBuffer.position() == 0) {
                if (System.currentTimeMillis() - startTime > 3000) {
                    System.out.println("Server unavailable.");
                    Exit.exit();
                }
                client.read(receiveBuffer);
            }

            receiveBuffer.flip();
            String response = new String(receiveBuffer.array(), 0,
                    receiveBuffer.limit(), StandardCharsets.UTF_8);
            DistributionOfTheOutputStream.printFromServer(response);
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
