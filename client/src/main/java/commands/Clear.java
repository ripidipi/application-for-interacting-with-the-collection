package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that clears all elements from the collection on the server side.
 * <p>Sends a CLEAR request with no payload.</p>
 */
public class Clear implements Helpable, Command {

    /**
     * Executes the clear command, producing a Request to clear the collection.
     *
     * @param arg unused argument string
     * @param inputMode unused input mode identifier
     * @return a Request with command CLEAR and null payload
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.CLEAR, (Void) null);
    }

    /**
     * Provides help information for the clear command.
     *
     * @return a brief description of the clear operation
     */
    @Override
    public String getHelp() {
        return "Clears the collection.";
    }
}
