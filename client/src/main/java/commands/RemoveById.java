package commands;

import collection.fabrics.StudyGroupFabric;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.Server;
import storage.Logging;
import storage.RequestPair;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Command that removes a study group by its ID.
 * This command searches for a study group with the specified ID and removes it from the collection if it exists.
 */
public class RemoveById implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.REMOVE_BY_ID, StudyGroupFabric.getIdInteger(arg));
    }

    @Override
    public String getHelp() {
        return "Removes a study group from the collection by its ID. " +
                "If no study group with the specified ID exists, no action is performed.";
    }
}
