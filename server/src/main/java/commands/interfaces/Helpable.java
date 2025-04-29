package commands.interfaces;

/**
 * Marker interface for commands that can provide usage or help information.
 * <p>
 * Commands implementing this interface must supply
 * a descriptive help string explaining their purpose and usage.
 * </p>
 */
public interface Helpable {

    /**
     * Retrieves the help text describing this command's functionality and usage.
     * <p>
     * The returned string should include a brief summary of what the command does,
     * any required or optional parameters, and examples of typical invocation.
     * </p>
     *
     * @return a {@code String} containing detailed help information for the command
     */
    String getHelp();

}