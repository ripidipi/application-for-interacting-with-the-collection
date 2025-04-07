package commands;

import collection.Collection;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;

/**
 * Command that provides information about the collection.
 */
public class Info implements Helpable, Command<Void> {

    /**
     * Prints information about the collection (type, initialization date, number of elements).
     */
    public static void info() {
        DistributionOfTheOutputStream.println(Collection.getInstance().getInfo());
    }

    @Override
    public void execute(Void arg, boolean muteMode) {
        info();
    }

    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
