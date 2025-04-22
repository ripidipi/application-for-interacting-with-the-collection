package commands;

import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Request;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Command that provides information about available commands.
 * This class is used to retrieve and display help information for all available commands in the system.
 */
public class Help implements Helpable, Command {

    private static ArrayList<Helpable> commands;
    private static Help instance;

    /**
     * Private constructor to initialize the list of commands.
     */
    Help() {
        commands = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of the Help class.
     * If the instance is not yet created, it initializes a new instance.
     *
     * @return The singleton instance of the Help class.
     */
    public static Help getInstance() {
        if (instance == null) {
            instance = new Help();
        }
        return instance;
    }

    /**
     * Displays information about all available commands.
     * Each command's name and description are printed to the output stream.
     */
    public static void help() {
        for (Helpable command : commands) {
            DistributionOfTheOutputStream.println(String.join("_",
                    command.getClass().getSimpleName().split("(?=[A-Z])")));
            DistributionOfTheOutputStream.println("\t" + command.getHelp());
        }
    }

    /**
     * Adds commands to the list of supported commands.
     * The commands added will be available to display in the help section.
     *
     * @param commandArgs array of supporting commands.
     */
    public void addCommand(Helpable... commandArgs) {
        Collections.addAll(commands, commandArgs);
    }

    @Override
    public Request<?> execute(String arg, String inputMode) {
        return new Request<>(Commands.HELP, (Void)null);
    }

    @Override
    public String getHelp() {
        return "Returns information about commands.";
    }
}
