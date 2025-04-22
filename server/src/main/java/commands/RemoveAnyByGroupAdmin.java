package commands;

import collection.Collection;
import storage.Authentication;
import storage.DBManager;
import storage.Logging;
import collection.Person;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that removes a study group by its group admin from console.
 * This command removes the first study group whose group admin matches the provided person.
 */
public class RemoveAnyByGroupAdmin implements Helpable, Command<Person> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Removes the first study group with the given group admin from the collection.
     *
     * @param person The group admin whose group needs to be removed.
     */
    public boolean removeGroupByAdmin(Person person, Authentication auth) throws SQLException {
        Connection connection = DBManager.getConnection();
        String sql = """
        DELETE FROM study_group
        WHERE id = (
            SELECT id FROM study_group
            WHERE admin_name = ?
              AND admin_birthday = ?
              AND admin_height = ?
              AND admin_passport_id = ?
            AND owner_username = ?
            LIMIT 1
        )
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, person.name());
            statement.setDate(2, person.birthday() != null ? Date.valueOf(person.birthday().toLocalDate()) : null);
            statement.setDouble(3, person.height());
            statement.setString(4, person.passportID());
            statement.setString(5, auth.name());
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    @Override
    public void execute(Person person, boolean muteMode, Authentication auth) {
        try {
            lock.lock();
            removeGroupByAdmin(person, auth);
            Collection.getInstance().reload();
        } catch (InsufficientNumberOfArguments e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getHelp() {
        return "Removes the first study group with the specified group admin.";
    }
}
