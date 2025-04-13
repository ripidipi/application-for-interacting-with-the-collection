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
import storage.RequestPair;

/**
 * Command that adds a study group to the collection only if it is the largest from the console.
 */
public class AddIfMax implements Helpable, Command {

    /**
     * Executes the AddIfMax command.
     *
     * @param arg       the input arguments for creating a study group
     * @param inputMode the input mode (e.g., console or file)
     */
    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        try {
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "AddIfMax", false);
            return new RequestPair<>(Commands.ADD_IF_MAX, studyGroup);
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
        return "Adds a new element to the collection if the element is larger than the maximum in the collection.";
    }
}
