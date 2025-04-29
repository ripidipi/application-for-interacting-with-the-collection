package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Implements the “show” command, which requests the server (or local controller)
 * to return and display all study groups currently stored in the collection.
 * <p>
 * If the collection is empty, the client should display an appropriate
 * “no items found” message to the user.
 * </p>
 *
 * @implNote
 * This command does not require an argument; both parameters passed
 * to {@link #execute(String, String)} are ignored.
 * It always generates a {@link Request} with command type {@code SHOW}.
 *
 * @see Commands#SHOW
 */
public class Show implements Helpable, Command {

    /**
     * Builds a request to show all study groups.
     *
     * @param arg       unused; should be {@code null} or empty
     * @param inputMode unused; indicates the mode of input ("interactive" vs. "script")
     * @return a {@code Request<Void>} with the command type {@code SHOW} and no body
     */
    @Override
    public Request<Void> execute(String arg, String inputMode) {
        return new Request<>(Commands.SHOW, (Void) null);
    }

    /**
     * Provides a short description of this command for help menus.
     *
     * @return a one-line summary of what the show command does
     */
    @Override
    public String getHelp() {
        return "show : Displays all study groups in the collection. If the collection is empty, displays an appropriate message.";
    }
}
