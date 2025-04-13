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
import storage.RequestPair;

/**
 * Command that removes a study group by its group admin from console.
 * This command removes the first study group whose group admin matches the provided person.
 */
public class RemoveAnyByGroupAdmin implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        try {
            if (inputMode.equalsIgnoreCase("F")) {
                String[] inputSplit = arg.split(",");
                if (inputSplit.length != 4) {
                    throw new InsufficientNumberOfArguments("CountByGroupAdmin");
                }
            }
            Person person = PersonFabric.getPersonFrom(arg, inputMode);
            return new RequestPair<>(Commands.REMOVE_ANY_BY_GROUP_ADMIN, person);
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
        return "Removes the first study group with the specified group admin.";
    }
}
