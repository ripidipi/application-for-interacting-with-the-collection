package commands;

import commands.interfaces.Command;
import storage.RequestPair;

/**
 * Enum representing the available commands in the system.
 * Each command corresponds to a specific action that can be executed.
 */
public enum Commands {
    /** help : display help information about available commands */
    HELP(Help.getInstance()),

    /** info : display information about the collection (type, initialization date, number of elements, etc.) */
    INFO(new Info()),

    /** show : display all elements of the collection in string representation */
    SHOW(new Show()),

    /** add {element} : add a new element to the collection */
    ADD(new Add()),

    /** update id {element} : update the value of a collection element whose ID matches the given one */
    UPDATE(new Update()),

    /** remove_by_id id : remove an element from the collection by its ID */
    REMOVE_BY_ID(new RemoveById()),

    /** clear : clear the collection */
    CLEAR(new Clear()),

    CHECK_IS_WITH_ID(new Show()),

    /** execute_script file_name : read and execute a script from the specified file.
     * The script contains commands in the same format as they are entered by the user in interactive mode.
     */
    EXECUTE_SCRIPT(new ExecuteScript()),

    /** exit : terminate the program (without saving to a file) */
    EXIT(new Exit()),

    /** add_if_max {element} : add a new element to the collection if its value exceeds the value of the largest element in the collection */
    ADD_IF_MAX(new AddIfMax()),

    /** remove_greater {element} : remove all elements from the collection that exceed the specified one */
    REMOVE_GREATER(new RemoveGreater()),

    /** remove_lower {element} : remove all elements from the collection that are smaller than the specified one */
    REMOVE_LOWER(new RemoveLower()),

    /** remove_any_by_group_admin groupAdmin : remove a single element from the collection whose groupAdmin field value is equivalent to the given one */
    REMOVE_ANY_BY_GROUP_ADMIN(new RemoveAnyByGroupAdmin()),

    /** group_counting_by_id : group the elements of the collection by the ID field and display the number of elements in each group */
    GROUP_COUNTING_BY_ID(new GroupCountingById()),

    /** count_by_group_admin groupAdmin : display the number of elements whose groupAdmin field value matches the given one */
    COUNT_BY_GROUP_ADMIN(new CountByGroupAdmin());

    private final Command command;

    /**
     * Constructor for a command.
     *
     * @param command the command to be executed
     */
    Commands(Command command) {
        this.command = command;
    }

    /**
     * Executes the command with the provided argument and input mode.
     *
     * @param arg       the argument to pass to the command
     * @param inputMode the mode in which the input is provided (e.g., console or file)
     */
    public RequestPair<?> execute(String arg, String inputMode) {
        return command.execute(arg, inputMode);
    }
}
