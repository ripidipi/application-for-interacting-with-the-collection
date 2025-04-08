import commands.Exit;
import exceptions.RemoveOfTheNextSymbol;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.Server;
import storage.Logging;
import storage.RequestPair;
import storage.SavingAnEmergencyStop;

import java.net.*;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {
        initialize();
        runCommandLoop();
    }

    public static void runCommandLoop() {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(Server.getServerHost(), Server.getServerPort()));

            while (Exit.running) {
                System.out.print("Enter the command: ");

                RequestPair<?> request = CommandsHandler.input();

                Server.interaction(client, request);

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
