package commands;

import storage.Logging;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;


/**
 * Command that removes study groups lower than a given one from the collection.
 * This command removes all study groups in the collection that have lower values than
 * the specified study group based on comparison logic defined in the class.
 */
public class RemoveLower implements Helpable, Command<StudyGroup> {

    @Override
    public void execute(StudyGroup studyGroup, boolean muteMode) {
        try {
            Command.remove(studyGroup, (sG1, sG2) -> sG1.compareTo(sG2) < 0);
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    @Override
    public String getHelp() {
        return "Removes all study groups from the collection that are lower than the specified study group.";
    }
}
