package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.RequestPair;

/**
 * Command that removes a study group by its ID.
 * This command searches for a study group with the specified ID and removes it from the collection if it exists.
 */
public class RemoveById implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.REMOVE_BY_ID, arg);
    }

    @Override
    public String getHelp() {
        return "Removes a study group from the collection by its ID. " +
                "If no study group with the specified ID exists, no action is performed.";
    }
}
