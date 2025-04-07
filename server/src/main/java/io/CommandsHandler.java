package io;

import collection.StudyGroup;
import commands.Add;
import commands.Commands;
import commands.Exit;
import exceptions.ConnectionToFileFailed;
import exceptions.IncorrectCommand;
import exceptions.RemoveOfTheNextSymbol;
import storage.Logging;
import storage.RequestPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class handles command input either from the console or from a file.
 * It processes the commands, checks their validity, and executes them accordingly.
 */
public class CommandsHandler {

    public static void execute(RequestPair<?> request, boolean muteMode) {
        Commands command = request.command();
        command.execute(request.object(), muteMode);
    }

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
     * @return null
     */
    public static Void isCommand(String[] inputSplit, String inputMode) {
        try {
            if (inputSplit.length != 0 && convertToEnum(inputSplit[0])) {
                Commands command = Enum.valueOf(Commands.class, inputSplit[0].toUpperCase());
            } else {
                throw new IncorrectCommand(inputSplit[0]);
            }
        } catch (IncorrectCommand e) {
            DistributionOfTheOutputStream.println(e.getMessage());
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
     * @param handler  a function to process each line of input
     */
    public static void inputFromFile(String filePath, Function<String, StudyGroup> handler) {
        try {
            if (filePath == null || filePath.isEmpty()) {
                throw new ConnectionToFileFailed("Connection to environment path failed " + filePath);
            }
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                throw new ConnectionToFileFailed("File path doesn't found " + filePath);
            }
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine();
                        StudyGroup studyGroup = handler.apply(line);
                        Commands.ADD.execute(studyGroup, true);
                    } catch (Exception e) {
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
