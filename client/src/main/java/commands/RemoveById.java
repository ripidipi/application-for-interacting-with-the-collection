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

    /**
     * Executes the RemoveById command.
     * Parses the ID from the input and creates a request to remove the corresponding study group.
     *
     * @param arg       the input argument representing the ID
     * @param inputMode the input mode (e.g., console or file)
     * @return a request to remove the study group by ID
     * @throws RuntimeException if no element with the specified ID exists
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        Integer id = StudyGroupFabric.getIdInteger(arg);
        if (Command.checkIsNotWithId(id)) {
            throw new RuntimeException("No element to remove with this ID in the collection.");
        }
        return new Request<>(Commands.REMOVE_BY_ID, id);
    }

    /**
     * Returns help information for the RemoveById command.
     *
     * @return a string describing the purpose of the command
     */
    @Override
    public String getHelp() {
        return "Removes a study group from the collection by its ID. " +
                "If no study group with the specified ID exists, no action is performed.";
    }
}
