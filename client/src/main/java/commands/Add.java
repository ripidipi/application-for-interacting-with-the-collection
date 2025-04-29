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
 * Command for adding study groups to the collection via console or file input.
 * <p>Supports validation of arguments and constructs a StudyGroup using StudyGroupFabric.</p>
 */
public class Add implements Helpable, Command {

    /**
     * Executes the add command by parsing input arguments and creating a StudyGroup.
     * <p>If inputMode is file-based ("F"), verifies the correct number of CSV fields.
     * Any parsing or validation errors result in messaging or termination.</p>
     *
     * @param arg the comma-separated study group data
     * @param inputMode the input mode identifier ("F" for file, others for interactive/mixed)
     * @return a Request carrying the ADD command and the constructed StudyGroup, or null on error
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            String[] inputSplit = arg.split(",");
            if (inputMode.equalsIgnoreCase("F")
                    && StudyGroup.formatStudyGroupToCSV(StudyGroupFabric.getEmptyStudyGroup()).split(",").length
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
     * Provides help information for the add command.
     *
     * @return a descriptive usage string
     */
    @Override
    public String getHelp() {
        return "Adds a new element to the collection.";
    }
}
