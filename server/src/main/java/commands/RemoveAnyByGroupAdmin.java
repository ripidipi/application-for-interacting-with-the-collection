package commands;

import storage.Logging;
import collection.Collection;
import collection.Person;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Command that removes a study group by its group admin from console.
 * This command removes the first study group whose group admin matches the provided person.
 */
public class RemoveAnyByGroupAdmin implements Helpable, Command<Person> {

    /**
     * Removes the first study group with the given group admin from the collection.
     *
     * @param person The group admin whose group needs to be removed.
     */
    static void removeGroupByAdmin(Person person) {
        TreeSet<StudyGroup> studyGroups = Collection.getInstance().getCollection();
        Iterator<StudyGroup> iterator = studyGroups.iterator();
        while (iterator.hasNext()) {
            StudyGroup studyGroup = iterator.next();
            if (Objects.equals(studyGroup.getGroupAdmin(), person)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void execute(Person person, boolean muteMode) {
        try {
            removeGroupByAdmin(person);
        } catch (InsufficientNumberOfArguments e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    @Override
    public String getHelp() {
        return "Removes the first study group with the specified group admin.";
    }
}
