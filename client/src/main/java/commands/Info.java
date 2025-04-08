package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import storage.RequestPair;

/**
 * Command that provides information about the collection.
 */
public class Info implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.INFO, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
