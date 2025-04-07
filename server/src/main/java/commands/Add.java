package commands;

import storage.Logging;
import collection.Collection;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

/**
 * Command for adding study groups to the collection from the console.
 */
public class Add implements Helpable, Command<StudyGroup> {

    /**
     * Adds a new study group to the collection.
     *
     * @param studyGroup the study group to be added
     */
    private static void addStudyGroup(StudyGroup studyGroup) {
        if (studyGroup != null) {
            Collection.getInstance().addElement(studyGroup);
        }
    }


    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode) {
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
