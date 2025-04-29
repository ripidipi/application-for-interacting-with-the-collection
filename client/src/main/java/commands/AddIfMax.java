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
 * Command that adds a study group to the collection only if it is strictly greater than the current maximum.
 * <p>Parses and validates input to create a StudyGroup via StudyGroupFabric.</p>
 */
public class AddIfMax implements Helpable, Command {

    /**
     * Executes the add-if-max logic by creating a StudyGroup and sending an ADD_IF_MAX request.
     * <p>On parsing or validation errors, prints messages or exits as appropriate.</p>
     *
     * @param arg the comma-separated arguments to construct the StudyGroup
     * @param inputMode the input mode ("C", "M", "F", etc.) determining parsing strategy
     * @return a Request with command ADD_IF_MAX and the constructed StudyGroup, or null on error
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "AddIfMax", false);
            return new Request<>(Commands.ADD_IF_MAX, studyGroup);
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
     * Returns usage information for the AddIfMax command.
     *
     * @return a help string describing the command's effect
     */
    @Override
    public String getHelp() {
        return "Adds a new element to the collection if it is larger than the current maximum element.";
    }
}
