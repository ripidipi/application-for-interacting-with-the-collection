package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that shows all study groups in the collection.
 */
public class Show implements Helpable, Command {

    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.SHOW, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Displays all study groups in the collection. If the collection is empty, shows a message indicating that.";
    }
}
