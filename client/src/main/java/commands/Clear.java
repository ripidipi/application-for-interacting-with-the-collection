package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

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
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.CLEAR, (Void)null);
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
