package io;

import collection.StudyGroup;
import commands.Commands;
import exceptions.ConnectionToFileFailed;
import storage.Logging;
import storage.RequestPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
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
