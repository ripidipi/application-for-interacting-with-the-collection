package commands;

import storage.Authentication;
import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that groups study groups by ID and counts the number of elements in each group.
 */
public class GroupCountingById implements Helpable, Command<Void> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Groups study groups by their ID and counts the number of elements in each group.
     * The groups are created based on the size of the collection, and each group
     * contains study groups with IDs in a certain range.
     * The result is printed to the output stream.
     */
    public static void groupCountingById() {
        try {
            lock.lock();
            TreeSet<StudyGroup> studyGroups = Collection.getInstance().getCollection();
            int setSize = studyGroups.size();
            if (setSize == 0) {
                DistributionOfTheOutputStream.println("The collection is empty.");
                return;
            }
            int groupCount = (int) Math.ceil(Math.sqrt(setSize));
            List<List<StudyGroup>> groups = new ArrayList<>(groupCount);
            for (int i = 0; i < groupCount; i++) {
                groups.add(new ArrayList<>());
            }
            int index = 0;
            for (StudyGroup studyGroup : studyGroups) {
                groups.get(index / groupCount).add(studyGroup);
                index++;
            }
            groups.removeIf(List::isEmpty);
            DistributionOfTheOutputStream.println("There are " + groups.size() + " groups.");
            int lastID = 1;
            for (List<StudyGroup> group : groups) {
                if (!group.isEmpty()) {
                    int groupSize = group.size();
                    int endID = group.get(group.size() - 1).getId();
                    DistributionOfTheOutputStream.println("In ID range " + lastID + "-" + (endID + 1) + " - " + groupSize + " elements.");
                    lastID = endID + 1;
                }
            }
            DistributionOfTheOutputStream.println("");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        groupCountingById();
    }

    @Override
    public String getHelp() {
        return "Groups study groups by their ID and counts the number of elements in each group.";
    }
}
