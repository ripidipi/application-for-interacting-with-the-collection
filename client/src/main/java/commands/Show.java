package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

import java.util.TreeSet;

/**
 * Command that shows all study groups in the collection.
 */
public class Show implements Helpable, Command {

    /**
     * Displays all study groups in the collection.
     */
    private static void show() {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();

        if (collection.isEmpty()) {
            DistributionOfTheOutputStream.println("Collection is empty");
            return;
        }

        for (StudyGroup studyGroup : collection) {
            System.out.println(studyGroup);
        }
    }

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.SHOW, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Displays all study groups in the collection. If the collection is empty, shows a message indicating that.";
    }
}
