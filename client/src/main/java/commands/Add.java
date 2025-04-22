package commands;

import collection.fabrics.StudyGroupFabric;
import exceptions.IncorrectValue;
import storage.Logging;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.Request;

/**
 * Command for adding study groups to the collection from the console.
 */
public class Add implements Helpable, Command {

    /**
     * Executes the add command.
     *
     * @param arg       the input arguments for creating a study group
     * @param inputMode the input mode (e.g., console or file)
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            String[] inputSplit = arg.split(",");
            if (inputMode.equalsIgnoreCase("F") &&
                    StudyGroup.formatStudyGroupToCSV(StudyGroupFabric.getEmptyStudyGroup()).split(",").length
                            != inputSplit.length) {
                throw new InsufficientNumberOfArguments("Add");
            }
            StudyGroup studyGroup = StudyGroupFabric.getStudyGroupFrom(inputMode, inputSplit, false, false);
            return new Request<>(Commands.ADD, studyGroup);
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
     * Returns the help information for the command.
     *
     * @return a string describing the command usage
     */
    @Override
    public String getHelp() {
        return "Adds a new element to the collection.";
    }
}
