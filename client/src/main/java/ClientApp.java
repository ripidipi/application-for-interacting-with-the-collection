import commands.Exit;
import exceptions.RemoveOfTheNextSymbol;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.ServerSetting;
import storage.Logging;
import storage.RequestPair;
import storage.SavingAnEmergencyStop;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        initialize();
        runCommandLoop();
    }

    public static void runCommandLoop() {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(ServerSetting.getServerHost(), ServerSetting.getServerPort()));

            while (Exit.running) {
                System.out.print("Enter the command: ");

                RequestPair<?> request = CommandsHandler.input();

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
            }
        } catch (PortUnreachableException e) {
            DistributionOfTheOutputStream.println("Problem to connect to the server.");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            System.out.println("End of work");
        }
    }

    public static void checkPreviousSession() {
        if (SavingAnEmergencyStop.checkIfFile()) {
            runPreviousSession();
        }
    }

    private static void initialize() {
        Logging.initialize();
        checkPreviousSession();
    }

    private static void runPreviousSession() {
        try {
            System.out.println("Previous session was urgently completed.\nPrint Yes if you want to continue it.");
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextLine()) {
                new Exit().execute("", "");
                throw new RemoveOfTheNextSymbol();
            }
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("yes")) {
                SavingAnEmergencyStop.recapCommandFromFile();
            }
        } catch (RemoveOfTheNextSymbol e) {
            System.out.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
