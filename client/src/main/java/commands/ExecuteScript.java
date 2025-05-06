package commands;

import exceptions.ServerDisconnect;
import io.Authentication;
import io.Server;
import storage.FileName;
import storage.Logging;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.IncorrectValue;
import exceptions.InfiniteRecursion;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import storage.Request;
import storage.RunningFiles;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Command that executes a series of commands defined in a script file.
 * <p>Prevents infinite recursion by tracking running scripts and logs
 * execution steps to a file.</p>
 */
public class ExecuteScript implements Helpable, Command {

    private static boolean executeScriptMode = false;

    /**
     * Reads and executes commands from the specified script file.
     * Tracks active scripts to avoid infinite recursion.
     * Logs start and end of execution and handles server interaction.
     *
     * @param fileName the name of the script file to execute
     */
    public static void executeScript(String fileName) {
        executeScriptMode = true;
        DistributionOfTheOutputStream.printlnToFile("Starting script execution: " + fileName);
        try {
            if (fileName == null || fileName.isEmpty()) {
                throw new IncorrectValue("File name cannot be empty.");
            }
            FileName fn = new FileName(fileName.toUpperCase(), Authentication.getInstance());
            if (RunningFiles.getInstance().contains(fn)) {
                throw new InfiniteRecursion("Infinite recursion detected with file: " + fileName);
            }
            RunningFiles.getInstance().addFileName(fn);
            DistributionOfTheOutputStream.printFromServer(
                    Server.interaction(new Request<>(Commands.EXECUTE_SCRIPT, null))
            );
            CommandsHandler.inputFromFile(fileName);
        } catch (ServerDisconnect e) {}
        catch (IncorrectValue | InfiniteRecursion e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            DistributionOfTheOutputStream.printlnToFile("");
            executeScriptMode = false;
        }
    }

    /**
     * Returns whether a script is currently being executed.
     *
     * @return true if in script execution mode; false otherwise
     */
    public static boolean getExecuteScriptMode() {
        return executeScriptMode;
    }

    /**
     * Enables or disables script execution mode flag.
     *
     * @param mode true to set script execution mode, false to clear
     */
    public static void setExecuteScriptMode(boolean mode) {
        executeScriptMode = mode;
    }

    /**
     * Implements Command interface: triggers script execution and returns a corresponding Request.
     *
     * @param arg       the script file name to execute
     * @param inputMode ignored for this command
     * @return a Request signaling EXECUTE_SCRIPT command to the server
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        executeScript(arg);
        return new Request<>(Commands.EXECUTE_SCRIPT, null);
    }

    /**
     * Provides help text explaining script execution command usage.
     *
     * @return help string for the ExecuteScript command
     */
    @Override
    public String getHelp() {
        return "Executes a script from the specified file.";
    }
}
