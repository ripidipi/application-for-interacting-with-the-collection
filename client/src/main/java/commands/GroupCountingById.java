package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that groups study groups by ID and counts the number of elements in each group.
 * <p>Sends a GROUP_COUNTING_BY_ID request to the server with no payload.</p>
 */
public class GroupCountingById implements Helpable, Command {

    /**
     * Executes the group_counting_by_id command, requesting counts per ID.
     *
     * @param arg unused argument string
     * @param inputMode unused input mode identifier
     * @return a Request with command GROUP_COUNTING_BY_ID and null payload
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.GROUP_COUNTING_BY_ID, (Void) null);
    }

    /**
     * Provides help information for the group_counting_by_id command.
     *
     * @return a descriptive usage string
     */
    @Override
    public String getHelp() {
        return "Groups study groups by their ID and counts the number of elements in each group.";
    }
}