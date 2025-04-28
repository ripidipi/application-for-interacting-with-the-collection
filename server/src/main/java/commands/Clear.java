package commands;

import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.DBManager;
import storage.Logging;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that clears the entire collection.
 */
public class Clear implements Helpable, Command<Void> {

    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Clears all elements from the collection and resets study group IDs.
     */
    public static void clearCollection() {
        try {
            lock.lock();
            DBManager.requestStudyGroup("DELETE FROM study_group WHERE owner_username = ?");
            Collection.getInstance().reload();
            DistributionOfTheOutputStream.println("The collection has been cleared.");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
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
