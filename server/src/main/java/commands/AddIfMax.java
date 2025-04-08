package commands;

import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Command that adds a study group to the collection only if it is the largest from the console.
 */
public class AddIfMax implements Helpable, Command<StudyGroup> {

    /**
     * Adds a new study group if it is the maximum in the collection.
     *
     * @param studyGroup the study group to be checked and potentially added
     */
    private static void addStudyGroupIfMax(StudyGroup studyGroup, boolean muteMode) {
        if (studyGroup != null && isMax(studyGroup)) {
            Collection.getInstance().addElement(studyGroup);
            if (!muteMode) {
                DistributionOfTheOutputStream.println("Study group added successfully.");
            }
        } else {
            DistributionOfTheOutputStream.println("The study group is not the maximum and was not added.");
        }
    }

    /**
     * Checks if the given study group is the maximum in the collection.
     *
     * @param studyGroup the study group to compare
     * @return {@code true} if the study group is the largest; {@code false} otherwise
     */
    private static boolean isMax(StudyGroup studyGroup) {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        return collection.stream()
                .max(Comparator.naturalOrder())
                .map(maxStudyGroup -> maxStudyGroup.compareTo(studyGroup) < 0)
                .orElse(true);
    }


    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode) {
        try {
            addStudyGroupIfMax(studyGroup, muteMode);
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
        return "Adds a new element to the collection if the element is larger than the maximum in the collection.";
    }
}
