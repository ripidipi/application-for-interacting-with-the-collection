package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Request;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Singleton command that provides help information about all available commands.
 * Maintains a list of Helpable command instances and displays their usage information.
 */
public class Help implements Helpable, Command {

    /**
     * List of registered commands providing help information.
     */
    private static ArrayList<Helpable> commands;

    /**
     * Singleton instance of the Help command.
     */
    private static Help instance;

    /**
     * Private constructor to initialize the commands list.
     */
    private Help() {
        commands = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of Help, creating it if necessary.
     *
     * @return the Help singleton instance
     */
    public static Help getInstance() {
        if (instance == null) {
            instance = new Help();
        }
        return instance;
    }

    /**
     * Prints help information for all registered commands to the output stream.
     * Each command's simple class name is split at camel-case boundaries for display,
     * followed by its help description.
     */
    public static void help() {
        for (Helpable command : commands) {
            String commandName = String.join("_",
                    command.getClass().getSimpleName().split("(?=[A-Z])"));
            DistributionOfTheOutputStream.println(commandName);
            DistributionOfTheOutputStream.println("\t" + command.getHelp());
        }
    }

    /**
     * Registers one or more Helpable commands to the help list.
     * These commands will be included in the output of {@link #help()}.
     *
     * @param commandArgs varargs of commands to register
     */
    public void addCommand(Helpable... commandArgs) {
        Collections.addAll(commands, commandArgs);
    }

    /**
     * Returns a Request object representing the HELP command.
     * This method fulfills the Command interface contract.
     *
     * @param arg unused argument string
     * @param inputMode unused input mode identifier
     * @return a Request with Commands.HELP and null payload
     */
    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.HELP, (Void) null);
    }

    /**
     * Provides help information for the HELP command itself.
     *
     * @return a brief description of the HELP functionality
     */
    @Override
    public String getHelp() {
        return "Returns information about available commands.";
    }
}
