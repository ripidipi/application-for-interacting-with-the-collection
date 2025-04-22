package commands;

import storage.Authentication;
import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;

import java.util.concurrent.locks.ReentrantLock;


/**
 * Command that removes study groups greater than a given one from the collection.
 * This command removes all study groups in the collection that have greater values than
 * the specified study group based on comparison logic defined in the class.
 */
public class RemoveGreater implements Helpable, Command<StudyGroup> {

    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode, Authentication auth) {
        try {
            lock.lock();
            Command.remove(studyGroup, (sG1, sG2) -> sG1.compareTo(sG2) > 0);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getHelp() {
        return "Removes all study groups from the collection that are greater than the specified study group.";
    }
}
