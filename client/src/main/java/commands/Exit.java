package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

/**
 * Command that exits the program.
 */
public class Exit implements Helpable, Command {

    public static boolean running = true;

    /**
     * Exits the program with a status code of 0 (successful termination).
     */
    public static void exit() {
        running = false;
    }

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        exit();
        return new RequestPair<>(Commands.EXIT, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Exits the program.";
    }
}
