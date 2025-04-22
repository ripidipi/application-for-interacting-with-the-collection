package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Authentication;

/**
 * Command that saves the collection data to a file.
 */
public class Save implements Helpable, Command<Void> {

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        DistributionOfTheOutputStream.println("Saving...");
        Collection.output();
        DistributionOfTheOutputStream.println("Save finished");
    }

    @Override
    public String getHelp() {
        return "Save the collection data to a file";
    }

}
