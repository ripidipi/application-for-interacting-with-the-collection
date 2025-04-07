package commands;

import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Command that removes a study group by its ID.
 * This command searches for a study group with the specified ID and removes it from the collection if it exists.
 */
public class RemoveById implements Helpable, Command<Integer> {

    private static void removeById(Integer id) {
        try {
            TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
            Iterator<StudyGroup> iterator = collection.iterator();

            while (iterator.hasNext()) {
                StudyGroup studyGroup = iterator.next();
                if (Objects.equals(studyGroup.getId(), id)) {
                    iterator.remove();
                    break;
                }
            }
            DistributionOfTheOutputStream.println("Object has been removed.");
        } catch (RuntimeException e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    @Override
    public void execute(Integer arg, boolean muteMode) {
        removeById(arg);
    }

    @Override
    public String getHelp() {
        return "Removes a study group from the collection by its ID. " +
                "If no study group with the specified ID exists, no action is performed.";
    }
}
