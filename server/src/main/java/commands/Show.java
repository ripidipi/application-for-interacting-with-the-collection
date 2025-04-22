package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.Logging;

import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that shows all study groups in the collection.
 */
public class Show implements Helpable, Command<Void> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Displays all study groups in the collection.
     */
    private static void show() {
        try {
            lock.lock();
            TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();

            if (collection.isEmpty()) {
                DistributionOfTheOutputStream.println("Collection is empty");
                return;
            }

            for (StudyGroup studyGroup : collection) {
                DistributionOfTheOutputStream.println(studyGroup.toString());
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        show();
    }

    @Override
    public String getHelp() {
        return "Displays all study groups in the collection. If the collection is empty, shows a message indicating that.";
    }
}
