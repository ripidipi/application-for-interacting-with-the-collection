package commands;

import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;

import java.util.Objects;
import java.util.TreeSet;



/**
 * Command that updates a study group in the collection by its ID from console.
 */
public class Update implements Helpable, Command<StudyGroup> {

    private static void update(StudyGroup studyGroup) {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        for (StudyGroup sG : collection) {
            if (Objects.equals(sG.getId(), studyGroup.getId())) {
                Collection.getInstance().removeElement(sG);
                Collection.getInstance().addElement(studyGroup);
                break;
            }
        }
        DistributionOfTheOutputStream.println("Study group " + studyGroup.getId() + " is successfully updated");
    }

    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode) {
        try{
            update(studyGroup);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    @Override
    public String getHelp() {
        return "Updates an existing study group by its ID. You can update study " +
                "groups either through user input or by loading data from a file.";
    }
}
