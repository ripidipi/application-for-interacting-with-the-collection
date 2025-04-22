package commands;

import storage.Authentication;
import storage.DBManager;
import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command for adding study groups to the collection from the console.
 */
public class Add implements Helpable, Command<StudyGroup> {

    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Adds a new study group to the collection.
     *
     * @param studyGroup the study group to be added
     */
    private static void addStudyGroup(StudyGroup studyGroup) {
        if (studyGroup != null) {
            lock.lock();
            DBManager.insertStudyGroup(studyGroup);
            Collection.getInstance().reload();
            lock.unlock();
        }
    }


    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode, Authentication auth) {
        try {
            addStudyGroup(studyGroup);
            if (!muteMode) {
                DistributionOfTheOutputStream.println("Added successfully");
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Returns the help information for the command.
     *
     * @return a string describing the command usage
     */
    @Override
    public String getHelp() {
        return "Adds a new element to the collection.";
    }
}
