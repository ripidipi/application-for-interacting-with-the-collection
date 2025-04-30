package commands;

import commands.interfaces.Command;
import storage.Request;

/**
 * Enum representing the available commands in the system along with their access rules.
 * Each constant pairs a Command implementation with its Rule set.
 */
public enum Commands {
    /** help : display help information about available commands */
    HELP(Help.getInstance(), Rules.U),

    /** info : display information about the collection (type, initialization date, number of elements, etc.) */
    INFO(new Info(), Rules.U),

    /** show : display all elements of the collection in string representation */
    SHOW(new Show(), Rules.U),

    /** add {element} : add a new element to the collection */
    ADD(new Add(), Rules.U),

    /** update id {element} : update a collection element by its ID */
    UPDATE(new Update(), Rules.U),

    /** remove_by_id id : remove an element from the collection by its ID */
    REMOVE_BY_ID(new RemoveById(), Rules.U),

    /** clear : clear the collection */
    CLEAR(new Clear(), Rules.U),

    /**
     * CHECK_IS_WITH_ID : internal command to verify whether an ID is already present
     * <p>Not available to regular users.</p>
     */
    CHECK_IS_WITH_ID(null, Rules.S),

    /** CHECK_AUTHENTICATION : internal command to verify user credentials
     * <p>Not available to regular users.</p>
     */
    CHECK_AUTHENTICATION(null, Rules.S),

    /**
     * execute_script file_name : read and execute commands from the specified script file
     * <p>Supports batch execution of commands in the same format as interactive input.</p>
     */
    EXECUTE_SCRIPT(new ExecuteScript(), Rules.U),

    /** ADD_USER : internal command to add a new user account
     * <p>Not available to regular users.</p>
     */
    ADD_USER(null, Rules.S),

    /** exit : terminate the program (without saving to file) */
    EXIT(new Exit(), Rules.U),

    /** add_if_max {element} : add a new element if it exceeds the current maximum */
    ADD_IF_MAX(new AddIfMax(), Rules.U),

    /** remove_greater {element} : remove all elements greater than the given one */
    REMOVE_GREATER(new RemoveGreater(), Rules.U),

    /** remove_lower {element} : remove all elements smaller than the given one */
    REMOVE_LOWER(new RemoveLower(), Rules.U),

    /** remove_any_by_group_admin groupAdmin : remove one element matching the given group admin */
    REMOVE_ANY_BY_GROUP_ADMIN(new RemoveAnyByGroupAdmin(), Rules.U),

    /** group_counting_by_id : group elements by ID and show counts */
    GROUP_COUNTING_BY_ID(new GroupCountingById(), Rules.U),

    /** count_by_group_admin groupAdmin : show number of elements matching the given group admin */
    COUNT_BY_GROUP_ADMIN(new CountByGroupAdmin(), Rules.U),

    /** Checks connection with the server via handshake message exchange
     * <p>Not available to regular users.</p>
     */
    HANDSHAKE(null, Rules.S);

    private final Command command;
    private final Rules rules;

    /**
     * Constructs a Commands enum constant.
     *
     * @param command the Command implementation (null for internal-only commands)
     * @param rules the access rules for this command
     */
    Commands(Command command, Rules rules) {
        this.command = command;
        this.rules = rules;
    }

    /**
     * Returns the access rules associated with the command.
     *
     * @return the Rules enum value indicating allowed user types
     */
    public Rules getRules() {
        return rules;
    }

    /**
     * Executes the associated Command implementation.
     *
     * @param arg the argument string to pass to the command
     * @param inputMode the input mode identifier (console, file, etc.)
     * @return a Request object produced by the command
     */
    public Request<?> execute(String arg, String inputMode) {
        return command.execute(arg, inputMode);
    }
}
