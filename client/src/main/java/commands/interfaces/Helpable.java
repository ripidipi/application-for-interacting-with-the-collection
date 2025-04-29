package commands.interfaces;

/**
 * Interface for commands that provide help information to users.
 * Classes implementing this interface should supply usage details and descriptions.
 */
public interface Helpable {

    /**
     * Retrieves a help string describing command usage and functionality.
     *
     * @return a detailed help message for the command
     */
    String getHelp();

}