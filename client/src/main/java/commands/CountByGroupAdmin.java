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
 * Command that counts the number of study groups administrated by a specified person.
 * <p>Accepts person details via console or file input and returns a request for counting.</p>
 */
public class CountByGroupAdmin implements Helpable, Command {

    /**
     * Executes the count_by_group_admin command by parsing a Person from input.
     * <p>Validates that file-based input provides exactly four CSV fields.</p>
     *
     * @param arg the comma-separated person details (name,birthday,height,passportID)
     * @param inputMode the input mode identifier ("F" for file, others for console/mixed)
     * @return a Request with COUNT_BY_GROUP_ADMIN command and the parsed Person, or null on error
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        try {
            if (inputMode.equalsIgnoreCase("F")) {
                String[] inputSplit = arg.split(",");
                if (inputSplit.length != 4) {
                    throw new InsufficientNumberOfArguments("CountByGroupAdmin");
                }
            }
            Person person = PersonFabric.getPersonFrom(arg, inputMode);
            return new Request<>(Commands.COUNT_BY_GROUP_ADMIN, person);
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
     * Provides help information for the count_by_group_admin command.
     *
     * @return a descriptive usage string for the command
     */
    @Override
    public String getHelp() {
        return "Counts the number of study groups where the specified person is the admin.";
    }
}
