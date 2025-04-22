package commands;

import storage.Authentication;
import storage.Logging;
import collection.Collection;
import collection.Person;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that counts the number of study groups where a specified person is the admin from console.
 */
public class CountByGroupAdmin implements Helpable, Command<Person> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Counts the number of study groups where the user-specified person is the admin.
     */
    public static void countByGroupAdmin(Person person) {
        long adminCounter = Collection.getInstance().getCollection()
                .stream()
                .filter(studyGroup -> Objects.equals(studyGroup.getGroupAdmin(), person))
                .count();
        DistributionOfTheOutputStream.println("The person is an admin in " + adminCounter + " groups.");
    }


    @Override
    public void execute(Person person, boolean muteMode, Authentication auth) {
        try {
            lock.lock();
            countByGroupAdmin(person);
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
        return "Counts the number of study groups where the specified person is the admin.";
    }
}
