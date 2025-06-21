package commands;

import storage.Authentication;
import storage.DBManager;
import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that updates a study group by its ID from the database.
 */
public class Update implements Helpable, Command<StudyGroup> {

    private static final ReentrantLock lock = new ReentrantLock();

    private boolean updateInDatabase(StudyGroup studyGroup, String username) {
        return DBManager.updateStudyGroup(studyGroup, username);
    }

    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode, Authentication auth) {
        try {
            lock.lock();
            if (!CheckIsWithId.validateId(studyGroup.getId(), auth)) {
                DistributionOfTheOutputStream.println("No objects with this id in the database");
                return;
            }
            boolean updated = updateInDatabase(studyGroup, auth.name());

            if (updated) {
                DistributionOfTheOutputStream.println("StudyGroup with id " + studyGroup.getId() + " has been updated.");
            } else {
                DistributionOfTheOutputStream.println("StudyGroup not found or you don't have permission to update it.");
            }

            Collection.getInstance().reload();

        } catch (InsufficientNumberOfArguments | RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getHelp() {
        return "Updates an existing study group by its ID in the database. " +
                "Only the owner of the study group can update it.";
    }
}