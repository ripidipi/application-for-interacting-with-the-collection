package io;

import commands.Rules;
import exceptions.*;
import storage.Logging;
import storage.Request;
import storage.SavingAnEmergencyStop;
import commands.Commands;
import commands.Exit;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Processes user commands from console or file input and generates corresponding requests.
 * <p>
 * Validates authentication, command existence, and rule-based permissions before execution.
 * Supports emergency save and rollback in case of errors.
 * </p>
 */
public class CommandsHandler {

    /**
     * Checks whether the provided string corresponds to a valid {@link Commands} enum constant.
     *
     * @param s the command name to validate
     * @return {@code true} if {@code s} matches a defined command, {@code false} otherwise
     */
    private static boolean convertToEnum(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                throw new IllegalArgumentException("Command string is null or empty");
            }
            Enum.valueOf(Commands.class, s.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates and executes a command represented by an array of tokens.
     * <p>
     * Verifies user authentication, checks command existence, enforces rule-level restrictions,
     * and builds a {@link Request} for the target command.
     * In case of failure, prints errors or logs exceptions as appropriate.
     * </p>
     *
     * @param inputSplit the first element is the command name; remaining elements are arguments
     * @param inputMode  mode flag (e.g., "C" for console, "F" for file)
     * @return a {@code Request<?>} ready for server interaction, or {@code null} on error
     */
    public static Request<?> isCommand(String[] inputSplit, String inputMode) {
        try {
            if (!Authentication.getInstance().isAuthenticated()) {
                throw new UnauthorizedUser("User must be authenticated");
            }

            if (inputSplit.length > 0 && convertToEnum(inputSplit[0])) {
                Commands command = Commands.valueOf(inputSplit[0].toUpperCase());

                if (new Rules.RulesComparator().compare(command.getRules(), Rules.S) >= 0) {
                    throw new IncorrectCommand(inputSplit[0]);
                }

                SavingAnEmergencyStop.addStringToFile(command.name());

                String args = inputSplit.length > 1
                        ? String.join(",", Arrays.copyOfRange(inputSplit, 1, inputSplit.length))
                        : "";
                Request<?> request = command.execute(args, inputMode);

                SavingAnEmergencyStop.clearFile();
                return request;

            } else {
                throw new IncorrectCommand(
                        inputSplit.length > 0 ? inputSplit[0] : ""
                );
            }

        } catch (UnauthorizedUser e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Authentication.askAuthentication();
        } catch (RuntimeException e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Reads a single line from the console, splits it into tokens, and processes it as a command.
     * <p>
     * On empty input or end-of-stream, exits the application.
     * </p>
     *
     * @return a {@code Request<?>} if a valid command was entered; otherwise {@code null}
     */
    public static Request<?> input() {
        try (Scanner scanner = new Scanner(System.in)) {
            if (!scanner.hasNextLine()) {
                new Exit().execute("", "");
                throw new RemoveOfTheNextSymbol("No more input");
            }
            String line = scanner.nextLine();
            String[] tokens = line.split(" ");
            return isCommand(tokens, "C");

        } catch (IllegalArgumentException e) {
            DistributionOfTheOutputStream.println("Invalid input for command. Try again.");
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Reads commands line-by-line from a file and executes each.
     * <p>
     * Lines are split on commas; each invocation of {@link #isCommand} may print or log errors.
     * Stops processing on {@link ServerDisconnect}.
     * </p>
     *
     * @param filePath the path to the command script file
     */
    public static void inputFromFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                throw new ConnectionToFileFailed("Invalid file path: " + filePath);
            }

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new ConnectionToFileFailed("File not found: " + filePath);
            }

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] args = line.split(",");
                    try {
                        Request<?> request = isCommand(args, "F");
                        if (request != null) {
                            DistributionOfTheOutputStream.printFromServer(
                                    Server.interaction(request)
                            );
                        }
                    } catch (ServerDisconnect sd) {
                        return;
                    }
                }
            } catch (FileNotFoundException fnf) {
                Logging.log(Logging.makeMessage(fnf.getMessage(), fnf.getStackTrace()));
            }

        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
