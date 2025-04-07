package commands.interfaces;

/**
 * Interface for commands that provide help information.
 */
public interface Helpable {

    /**
     * Returns information about the command.
     *
     * @return A string containing the help information about the command.
     */
    String getHelp();

}
