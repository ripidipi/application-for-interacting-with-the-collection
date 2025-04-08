package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.RequestPair;

/**
 * Command that groups study groups by ID and counts the number of elements in each group.
 */
public class GroupCountingById implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.GROUP_COUNTING_BY_ID, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Groups study groups by their ID and counts the number of elements in each group.";
    }
}
