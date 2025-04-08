package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.RequestPair;

/**
 * Command that shows all study groups in the collection.
 */
public class Show implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.SHOW, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Displays all study groups in the collection. If the collection is empty, shows a message indicating that.";
    }
}
