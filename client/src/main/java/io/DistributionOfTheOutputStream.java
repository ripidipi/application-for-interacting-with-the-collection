package io;

import commands.ExecuteScript;
import storage.Logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Manages application output, routing messages to the console or to a file
 * depending on the current execution mode.
 * <p>
 * When script execution mode is active ({@link ExecuteScript#getExecuteScriptMode()}),
 * output is appended to a file defined by {@link OutputFileSettings#getOutputFilePath()}.
 * Otherwise, messages are printed directly to {@code System.out}.
 * </p>
 * <p>
 * All file I/O errors automatically disable script mode and fall back to console output.
 * </p>
 */
public interface DistributionOfTheOutputStream {

    /**
     * Deletes the existing output file to start fresh.
     * Should be called before beginning script-based output.
     * On failure, disables script mode.
     */
    static void clear() {
        try {
            File file = new File(OutputFileSettings.getOutputFilePath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("Output to file error");
            ExecuteScript.setExecuteScriptMode(false);
        }
    }

    /**
     * Prints a message followed by a newline to the active output target.
     * @param message the message to print or write
     */
    static void println(String message) {
        if (ExecuteScript.getExecuteScriptMode()) {
            printlnToFile(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Prints a message without a newline to the active output target.
     * @param message the message to print or write
     */
    static void print(String message) {
        if (ExecuteScript.getExecuteScriptMode()) {
            printToFile(message);
        } else {
            System.out.print(message);
        }
    }

    /**
     * Appends a message plus newline to the output file.
     * Disables script mode on failure.
     * @param message the line to append
     */
    static void printlnToFile(String message) {
        String fileName = OutputFileSettings.getOutputFilePath();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8)) {
            writer.write(message);
            writer.write(System.lineSeparator());
        } catch (Exception e) {
            System.out.println("Output to file error");
            ExecuteScript.setExecuteScriptMode(false);
        }
    }

    /**
     * Appends a message without newline to the output file.
     * Disables script mode on failure.
     * @param message the text to append
     */
    static void printToFile(String message) {
        String fileName = OutputFileSettings.getOutputFilePath();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileName, true), StandardCharsets.UTF_8)) {
            writer.write(message);
        } catch (Exception e) {
            System.out.println("Output to file error");
            ExecuteScript.setExecuteScriptMode(false);
        }
    }

    /**
     * Processes a server response, splitting on marker tokens and routing
     * parts tagged for console ("C#...") and file ("F#...").
     * <p>
     * Lines prefixed with "C#" are printed to console; those with "F#" are
     * appended to the output file. If the response is null, prints an error.
     * </p>
     * @param response the raw response string from the server
     */
    static void printFromServer(String response) {
        try {
            if (response == null) {
                println("Problem with server response");
                throw new NullPointerException("response is null");
            }
            List<String> toConsole = Pattern.compile("##")
                    .splitAsStream(response)
                    .filter(line -> line.startsWith("C#"))
                    .map(line -> line.substring(2))
                    .collect(Collectors.toList());
            List<String> toFile = Pattern.compile("##")
                    .splitAsStream(response)
                    .filter(line -> line.startsWith("F#"))
                    .map(line -> line.substring(2))
                    .collect(Collectors.toList());
            toConsole.forEach(System.out::print);
            toFile.forEach(DistributionOfTheOutputStream::printToFile);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
