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
 * This class handles command input either from the console or from a file.
 * It processes the commands, checks their validity, and executes them accordingly.
 */
public class CommandsHandler {

    /**
     * Checks if a given string can be converted to a valid {@link Commands} enum value.
     *
     * @param s the string to check
     * @return true if the string corresponds to a valid command, false otherwise
     */
    private static boolean convertToEnum(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                throw new IllegalArgumentException();
            }
            Enum.valueOf(Commands.class, s.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Processes and executes a command if it is valid.
     * If the command requires arguments, they are passed and executed accordingly.
     *
     * @param inputSplit an array containing the command and its arguments
     * @param inputMode  the mode of input (e.g., console or file)
     */
    public static Request<?> isCommand(String[] inputSplit, String inputMode) {
        try {
            if (!Authentication.getInstance().isAuthenticated()) {
                throw new UnauthorizedUser("");
            }
            if (inputSplit.length != 0 && convertToEnum(inputSplit[0])) {
                Commands command = Enum.valueOf(Commands.class, inputSplit[0].toUpperCase());
                if (new Rules.RulesComparator().compare(command.getRules(), Rules.S) >= 0) {
                    throw new IncorrectCommand(inputSplit[0]);
                }
                SavingAnEmergencyStop.addStringToFile(command.name());
                Request<?> request;
                if (inputSplit.length >= 2) {
                    request = command.execute(String.join(",", Arrays.copyOfRange(inputSplit, 1, inputSplit.length)), inputMode);
                } else {
                    request = command.execute("", inputMode);
                }
                SavingAnEmergencyStop.clearFile();
                return request;
            } else {
                throw new IncorrectCommand(inputSplit[0]);
            }
        } catch (UnauthorizedUser e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Authentication.askAuthentication();
        }catch (RuntimeException e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Reads and processes command input from the console.
     * If a valid command is entered, it is executed accordingly.
     */
    public static Request<?> input() {
        try {
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextLine()) {
                new Exit().execute("", "");
                throw new RemoveOfTheNextSymbol();
            }
            String input = scanner.nextLine();
            String[] inputSplit = input.split(" ");

            return isCommand(inputSplit, "C");
        } catch (IllegalArgumentException e) {
            DistributionOfTheOutputStream.println("Invalid input for command. Try again");
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Reads and processes command input from a file.
     * Each line of the file is parsed, and commands are executed based on the content.
     *
     * @param filePath the path to the input file
     */
    public static void inputFromFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                throw new ConnectionToFileFailed("Connection to environment path failed " + filePath);
            }
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new ConnectionToFileFailed("File path doesn't found " + filePath);
            }

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine();
                        String[] values = line.split(",");
                        Request<?> request = isCommand(values, "F");
                        if(request != null)
                            DistributionOfTheOutputStream.printFromServer(Server.interaction(request));
                    } catch (ServerDisconnect _) {
                        return;
                    }catch (Exception e) {
                        Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
                    }
                }
            } catch (FileNotFoundException e) {
                Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
