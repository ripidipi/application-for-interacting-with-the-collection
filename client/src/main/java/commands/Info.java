package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.Request;

/**
 * Command that provides information about the collection.
 */
public class Info implements Helpable, Command {

    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.INFO, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
