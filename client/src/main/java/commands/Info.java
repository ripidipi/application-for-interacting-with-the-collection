package commands;

import collection.Collection;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

/**
 * Command that provides information about the collection.
 */
public class Info implements Helpable, Command {

    /**
     * Prints information about the collection (type, initialization date, number of elements).
     */
    public static void info() {
        DistributionOfTheOutputStream.println(Collection.getInstance().getInfo());
    }

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.INFO, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
