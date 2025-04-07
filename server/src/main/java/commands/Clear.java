package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

/**
 * Command that clears the entire collection.
 */
public class Clear implements Helpable, Command<Void> {

    /**
     * Clears all elements from the collection and resets study group IDs.
     */
    public static void clearCollection() {
        Collection.getInstance().clearCollection();
        StudyGroup.clearIds();
        DistributionOfTheOutputStream.println("The collection has been cleared.");
    }


    @Override
    public void execute(Void arg, boolean muteMode) {
        clearCollection();
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
