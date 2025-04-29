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
 * that are greater than the specified one.
 */
public class RemoveGreater implements Helpable, Command {

    /**
     * Executes the RemoveGreater command.
     * Constructs a StudyGroup object from the input and sends it for comparison-based removal.
     *
     * @param arg       serialized StudyGroup or input string
     * @param inputMode input mode ("C" for console, "F" for file)
     * @return a Request containing the command and parsed StudyGroup, or null if an error occurs
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            StudyGroup studyGroup =
                    StudyGroupFabric.parseStudyGroup(arg, inputMode, "RemoveGreater", false);
            return new Request<>(Commands.REMOVE_GREATER, studyGroup);
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
     * Returns help information for the RemoveGreater command.
     *
     * @return a string describing what the command does
     */
    @Override
    public String getHelp() {
        return "Removes all study groups from the collection that are greater than the specified study group.";
    }
}
