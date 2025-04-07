package commands;

import storage.Logging;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.IncorrectValue;
import exceptions.InfiniteRecursion;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

/**
 * Command that executes a script from a specified file.
 */
public class ExecuteScript implements Helpable, Command {

    private static boolean executeScriptMode = false;

    /**
     * Executes a script from the given file.
     * This method reads commands from the file and executes them one by one.
     * It also prevents infinite recursion if the same script is executed multiple times.
     *
     * @param fileName The name of the script file to execute.
     */
    public static void executeScript(String fileName) {
        executeScriptMode = true;
        DistributionOfTheOutputStream.printlnToFile("Starting script execution: " + fileName);
        try {
            if (fileName == null || fileName.isEmpty()) {
                throw new IncorrectValue("File name cannot be empty.");
            }
        } catch (IncorrectValue e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            DistributionOfTheOutputStream.printlnToFile("");
            executeScriptMode = false;
            DistributionOfTheOutputStream.println("Finished script execution from file: " + fileName);
        }
    }
    //TODO
    /**
     * Returns the current state of script execution.
     *
     * @return {@code true} if a script is currently being executed; {@code false} otherwise.
     */
    public static boolean getExecuteScriptMode() {
        return executeScriptMode;
    }

    /**
     * Sets the mode of script execution.
     *
     * @param mode The mode to set for script execution.
     */
    public static void setExecuteScriptMode(boolean mode) {
        executeScriptMode = mode;
    }

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        return new RequestPair<>(Commands.EXECUTE_SCRIPT, arg);
    }

    @Override
    public String getHelp() {
        return "Executes a script from the specified file.";
    }
}
