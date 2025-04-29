package commands;

import collection.fabrics.PersonFabric;
import exceptions.IncorrectValue;
import storage.Logging;
import collection.Person;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.InsufficientNumberOfArguments;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.Request;

/**
 * Command that removes a study group by its group admin.
 * This command removes the first study group whose group admin matches the provided person.
 */
public class RemoveAnyByGroupAdmin implements Helpable, Command {

    /**
     * Executes the RemoveAnyByGroupAdmin command.
     * It parses the provided person and returns a request to remove the matching group.
     *
     * @param arg       the input data representing the person (group admin)
     * @param inputMode the input mode (e.g., console or file)
     * @return a request object containing the command type and the person as the argument,
     *         or null if an error occurred
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            if (inputMode.equalsIgnoreCase("F")) {
                String[] inputSplit = arg.split(",");
                if (inputSplit.length != 4) {
                    throw new InsufficientNumberOfArguments("RemoveAnyByGroupAdmin");
                }
            }
            Person person = PersonFabric.getPersonFrom(arg, inputMode);
            return new Request<>(Commands.REMOVE_ANY_BY_GROUP_ADMIN, person);
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
     * Returns help information for the RemoveAnyByGroupAdmin command.
     *
     * @return a string describing the purpose of the command
     */
    @Override
    public String getHelp() {
        return "Removes the first study group with the specified group admin.";
    }
}
