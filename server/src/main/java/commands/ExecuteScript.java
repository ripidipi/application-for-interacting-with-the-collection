package commands;

import storage.Logging;
import storage.RunningFiles;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.IncorrectValue;
import exceptions.InfiniteRecursion;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;

/**
 * Command that executes a script from a specified file.
 */
public class ExecuteScript implements Helpable, Command<Void> {

    private static boolean executeScriptMode = false;


    public static void executeScript() {
        executeScriptMode ^= true;
        DistributionOfTheOutputStream.printlnC(
                executeScriptMode ? "Execute script started" : "Execute script failed");
    }

    /**
     * Returns the current state of script execution.
     *
     * @return {@code true} if a script is currently being executed; {@code false} otherwise.
     */
    public static boolean getExecuteScriptMode() {
        return executeScriptMode;
    }

    @Override
    public void execute(Void arg, boolean muteMode) {
        executeScript();
    }

    @Override
    public String getHelp() {
        return "Executes a script from the specified file.";
    }
}
