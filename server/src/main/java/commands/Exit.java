package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import io.PreparingOfOutputStream;

/**
 * Command that exits the program.
 */
public class Exit implements Helpable, Command<Void> {

    public static boolean running = true;

    /**
     * Exits the program with a status code of 0 (successful termination).
     */
    public static void exit() {
        DistributionOfTheOutputStream.println("Exiting the program...");
    }

    @Override
    public void execute(Void arg, boolean muteMode) {
        exit();
    }

    @Override
    public String getHelp() {
        return "Exits the program.";
    }
}
