package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that clears the entire collection.
 */
public class Clear implements Helpable, Command {

    /**
     * Executes the Clear command, removing all elements from the collection.
     *
     * @param arg       unused argument
     * @param inputMode unused input mode
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.CLEAR, (Void)null);
    }

    /**
     * Returns the help information for the command.
     *
     * @return a string describing the command usage
     */
    @Override
    public String getHelp() {
        return "Clears the collection.";
    }
}
