import commands.Exit;
import exceptions.RemoveOfTheNextSymbol;
import exceptions.ServerDisconnect;
import exceptions.UnauthorizedUser;
import io.*;
import storage.Logging;
import storage.Request;
import storage.RunningFiles;
import storage.SavingAnEmergencyStop;

import java.util.Scanner;

/**
 * Entry point for the client application that connects to the study-group management server.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Initializing subsystems (logging, output routing, emergency-stop recovery).</li>
 *   <li>Optionally resuming an interrupted session.</li>
 *   <li>Entering a command loop to process user commands until exit.</li>
 * </ul>
 * </p>
 * <p>
 * On each iteration, prompts for a command, builds a {@link Request},
 * sends it to the server via {@link Server#interaction(Request)}, and
 * displays the response. Ensures authentication via {@link Authentication}.
 * </p>
 * <p>
 * Handles network interruptions, input termination, and unexpected errors
 * by logging and providing graceful shutdown messages.
 * </p>
 */
public class ClientApp {

    /** Scanner for reading console input during recovery prompts. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Application entry point. Initializes subsystems and starts the command loop.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        initialize();
        runCommandLoop();
    }

    /**
     * Initializes logging, running-files registry, emergency-stop recovery, and output file.
     */
    private static void initialize() {
        Authentication.getInstance();
        Logging.initialize();
        RunningFiles.getInstance();
        checkPreviousSession();
        DistributionOfTheOutputStream.clear();
    }

    /**
     * Checks for an unfinished previous session and offers to resume it.
     */
    public static void checkPreviousSession() {
        if (SavingAnEmergencyStop.checkIfPreviousSession()) {
            runPreviousSession();
        }
    }

    /**
     * Prompts the user to resume a previous session if available.
     * <p>
     * If the user confirms ("yes"), ensures authentication and invokes
     * {@link SavingAnEmergencyStop#recapCommandFromFile()} to replay commands.
     * </p>
     */
    private static void runPreviousSession() {
        try {
            Handshake.makeHandshake();
            System.out.println("Previous session was interrupted. Enter 'Yes' to resume:");
            if (!scanner.hasNextLine()) {
                new Exit().execute("", "");
                throw new RemoveOfTheNextSymbol("No input available");
            }
            String input = scanner.nextLine();
            if ("yes".equalsIgnoreCase(input)) {
//                if (Authentication.getInstance().isNotAuthenticated(true)) {
//                    throw new UnauthorizedUser("User not authenticated for recovery");
//                }
                // TODO
                SavingAnEmergencyStop.recapCommandFromFile();
            }
        } catch (ServerDisconnect e) {
            System.out.println(e.getMessage());
        }catch (RemoveOfTheNextSymbol e) {
            System.out.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Main loop that reads commands from the user, sends them to the server, and outputs responses.
     * Continues until {@link Exit#running} is set to false or a disconnect occurs.
     */
    public static void runCommandLoop() {
        try {
            while (Exit.running && Handshake.makeHandshake()) {
                System.out.print("Enter the command: ");
                Request<?> request = CommandsHandler.input();
                if (request != null) {
                    DistributionOfTheOutputStream.printFromServer(
                            Server.interaction(request)
                    );
                }
            }
        } catch (ServerDisconnect e) {
            System.out.println(e.getMessage());
        }catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            System.out.println("End of work");
        }
    }

}