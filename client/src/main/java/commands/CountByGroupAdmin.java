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
 * Command that counts the number of study groups where a specified person is the admin from console.
 */
public class CountByGroupAdmin implements Helpable, Command {

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

    @Override
    public String getHelp() {
        return "Counts the number of study groups where the specified person is the admin.";
    }
}
