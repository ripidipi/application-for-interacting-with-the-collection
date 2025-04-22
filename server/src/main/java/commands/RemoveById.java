package commands;

import collection.Collection;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.DBManager;
import storage.Logging;
import storage.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that removes a study group by its ID from the database.
 * Only the owner of the group can remove it.
 */
public class RemoveById implements Helpable, Command<Integer> {

    private static final ReentrantLock lock = new ReentrantLock();

    private boolean removeById(int id, String username) {
        String sql = "DELETE FROM study_group WHERE id = ? AND owner_username = ?";
        return DBManager.applyRequest(id, username, sql);
    }

    @Override
    public void execute(Integer id, boolean muteMode, Authentication auth) {
        try {
            lock.lock();

            boolean deleted = removeById(id, auth.name());
            if (deleted) {
                DistributionOfTheOutputStream.println("StudyGroup with id " + id + " has been removed.");
            } else {
                DistributionOfTheOutputStream.println("StudyGroup not found or you don't have permission to delete it.");
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
        return "Removes a study group from the database by its ID. Only the group owner can perform this action.";
    }
}