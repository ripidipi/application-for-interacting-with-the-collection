package commands;

import collection.StudyGroup;
import collection.fabrics.StudyGroupFabric;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.IncorrectValue;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.Logging;
import storage.Request;

/**
 * Command that removes all study groups from the collection
 * that are lower than the specified one.
 */
public class RemoveLower implements Helpable, Command {

    /**
     * Executes the RemoveLower command.
     * Parses the input into a StudyGroup and sends it for comparison-based removal.
     *
     * @param arg       serialized StudyGroup or input string
     * @param inputMode input mode ("C" for console, "F" for file)
     * @return a Request containing the command and parsed StudyGroup, or null if an error occurs
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            StudyGroup studyGroup =
                    StudyGroupFabric.parseStudyGroup(arg, inputMode, "RemoveLower", false);
            return new Request<>(Commands.REMOVE_LOWER, studyGroup);
        } catch (InsufficientNumberOfArguments | IncorrectValue e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Returns help information for the RemoveLower command.
     *
     * @return a string describing what the command does
     */
    @Override
    public String getHelp() {
        return "Removes all study groups from the collection that are lower than the specified study group.";
    }
}
