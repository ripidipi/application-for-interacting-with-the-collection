package commands;

import commands.interfaces.Command;
import storage.Request;

/**
 * Enum representing the available commands in the system.
 * Each command corresponds to a specific action that can be executed.
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

    /** update id {element} : update the value of a collection element whose ID matches the given one */
    UPDATE(new Update(), Rules.U),

    /** remove_by_id id : remove an element from the collection by its ID */
    REMOVE_BY_ID(new RemoveById(), Rules.U),

    /** clear : clear the collection */
    CLEAR(new Clear(), Rules.U),

    CHECK_IS_WITH_ID(new Show(), Rules.S),

    CHECK_AUTHENTICATION(new Info(), Rules.S),

    /** execute_script file_name : read and execute a script from the specified file.
     * The script contains commands in the same format as they are entered by the user in interactive mode.
     */
    EXECUTE_SCRIPT(new ExecuteScript(), Rules.U),

    ADD_USER(new Info(), Rules.S),

    /** exit : terminate the program (without saving to a file) */
    EXIT(new Exit(), Rules.U),

    /** add_if_max {element} : add a new element to the collection if its value exceeds the value of the largest element in the collection */
    ADD_IF_MAX(new AddIfMax(), Rules.U),

    /** remove_greater {element} : remove all elements from the collection that exceed the specified one */
    REMOVE_GREATER(new RemoveGreater(), Rules.U),

    /** remove_lower {element} : remove all elements from the collection that are smaller than the specified one */
    REMOVE_LOWER(new RemoveLower(), Rules.U),

    /** remove_any_by_group_admin groupAdmin : remove a single element from the collection whose groupAdmin field value is equivalent to the given one */
    REMOVE_ANY_BY_GROUP_ADMIN(new RemoveAnyByGroupAdmin(), Rules.U),

    /** group_counting_by_id : group the elements of the collection by the ID field and display the number of elements in each group */
    GROUP_COUNTING_BY_ID(new GroupCountingById(), Rules.U),

    /** count_by_group_admin groupAdmin : display the number of elements whose groupAdmin field value matches the given one */
    COUNT_BY_GROUP_ADMIN(new CountByGroupAdmin(), Rules.U);

    private final Command command;
    private final Rules rules;

    /**
     * Constructor for a command.
     *
     * @param command the command to be executed
     */
    Commands(Command command, Rules rules) {
        this.command = command;
        this.rules = rules;
    }

    public Rules getRules() {
        return rules;
    }

    /**
     * Executes the command with the provided argument and input mode.
     *
     * @param arg       the argument to pass to the command
     * @param inputMode the mode in which the input is provided (e.g., console or file)
     */
    public Request<?> execute(String arg, String inputMode) {
        return command.execute(arg, inputMode);
    }
}
