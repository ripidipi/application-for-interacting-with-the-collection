package io;

import commands.Commands;
import storage.Authentication;
import storage.Request;

import java.util.concurrent.locks.ReentrantLock;

/**
 * This class handles command input either from the console or from a file.
 * It processes the commands, checks their validity, and executes them accordingly.
 */
public class CommandsHandler {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void execute(Request<?> request, boolean muteMode) {
        Commands command = request.command();
        command.execute(request.object(), muteMode, (new Authentication(request.username(), request.password())));

    }

}
