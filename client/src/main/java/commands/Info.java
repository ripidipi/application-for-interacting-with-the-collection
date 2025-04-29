package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that provides information about the collection.
 * This includes the collection type, initialization date, and the number of elements.
 */
public class Info implements Helpable, Command {

    /**
     * Executes the Info command.
     * Sends a request to retrieve metadata about the collection.
     *
     * @param arg       unused argument
     * @param inputMode unused input mode
     * @return a request object containing the command type
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.INFO, (Void)null);
    }

    /**
     * Returns help information for the Info command.
     *
     * @return a string describing the purpose of the command
     */
    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
