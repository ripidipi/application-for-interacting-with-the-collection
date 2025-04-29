package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that terminates the application.
 * <p>Sets the running flag to false and notifies the server of exit.</p>
 */
public class Exit implements Helpable, Command {

    /**
     * Indicates whether the application is currently running.
     */
    public static boolean running = true;

    /**
     * Stops the application by setting running to false.
     */
    public static void exit() {
        running = false;
    }

    /**
     * Executes the exit command, terminating the application and sending an EXIT request.
     *
     * @param arg unused argument
     * @param inputMode unused input mode
     * @return a Request signaling the EXIT command with no payload
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        exit();
        return new Request<>(Commands.EXIT, (Void) null);
    }

    /**
     * Provides help information for the exit command.
     *
     * @return a brief description indicating program termination
     */
    @Override
    public String getHelp() {
        return "Exits the program.";
    }
}