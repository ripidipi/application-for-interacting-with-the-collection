package io;

import commands.ExecuteScript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * This interface is responsible for managing the output stream for console and file outputs.
 * It allows printing messages either to the console or to a file, depending on the current execution mode.
 * If the script execution mode is active, messages are written to a file; otherwise, they are printed to the console.
 */
public interface DistributionOfTheOutputStream {

    static void println(String message) {
        PreparingOfOutputStream.addToOutMassage((ExecuteScript.getExecuteScriptMode() ?
                "##F#" : "##C#") + message + "\n");
    }

    static void print(String message) {
        PreparingOfOutputStream.addToOutMassage((ExecuteScript.getExecuteScriptMode() ?
                "##F#" : "##C#") + message );
    }

    static void printlnC(String message) {
        PreparingOfOutputStream.addToOutMassage( "##C#" + message + "\n");
    }

    static void printlnF(String message) {
        PreparingOfOutputStream.addToOutMassage( "##F#" + message + "\n");
    }

}
