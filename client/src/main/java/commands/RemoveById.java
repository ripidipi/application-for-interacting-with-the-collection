package commands;

import collection.fabrics.StudyGroupFabric;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that removes a study group by its ID.
 * This command searches for a study group with the specified ID and removes it from the collection if it exists.
 */
public class RemoveById implements Helpable, Command {

    @Override
    public Request<?> execute(String arg, String inputMode) {
        Integer id = StudyGroupFabric.getIdInteger(arg);
        if (Command.checkIsNotWithId(id)) {
            throw new RuntimeException("No element to update with this id in collection");
        }
        return new Request<>(Commands.REMOVE_BY_ID, StudyGroupFabric.getIdInteger(arg));
    }

    @Override
    public String getHelp() {
        return "Removes a study group from the collection by its ID. " +
                "If no study group with the specified ID exists, no action is performed.";
    }
}
