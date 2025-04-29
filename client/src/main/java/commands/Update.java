package commands;

import collection.StudyGroup;
import collection.fabrics.StudyGroupFabric;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.Logging;
import storage.Request;

/**
 * Implements the “update” command, which replaces an existing {@link StudyGroup}
 * in the collection by its unique identifier.
 * <p>
 * The command first parses the ID from the provided argument string; if no element
 * with that ID exists, it reports an error. Otherwise, it constructs a new
 * {@code StudyGroup} object—either interactively or from a script file—and
 * sends an update request to the server or local controller.
 * </p>
 *
 * @implNote
 * This command handles several failure modes internally:
 * <ul>
 *   <li>If the ID parsing fails with {@link RemoveOfTheNextSymbol}, the error
 *       message is printed and the application exits.</li>
 *   <li>If the ID does not correspond to an existing element, a runtime error
 *       message is printed.</li>
 *   <li>Any other exception is logged via {@link Logging}.</li>
 * </ul>
 *
 * @see StudyGroupFabric#getIdInteger(String)
 * @see StudyGroupFabric#parseStudyGroup(String, String, String, boolean)
 * @see Commands#UPDATE
 */
public class Update implements Helpable, Command {

    /**
     * Attempts to build a request to update an existing study group.
     *
     * @param arg       the input string containing the target ID, plus any
     *                  inline study group data if in script mode
     * @param inputMode a flag indicating the input mode (“interactive” or “script”)
     * @return a {@code Request<StudyGroup>} with command type {@code UPDATE}
     *         and the parsed {@link StudyGroup}, or {@code null} if an error occurred
     */
    @Override
    public Request<StudyGroup> execute(String arg, String inputMode) {
        try {
            Integer id = StudyGroupFabric.getIdInteger(arg);
            if (Command.checkIsNotWithId(id)) {
                throw new RuntimeException("No element to update with this id in collection");
            }
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "Update", true);
            return new Request<>(Commands.UPDATE, studyGroup);

        } catch (RemoveOfTheNextSymbol e) {
            // Critical parsing error: terminate application after reporting
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();

        } catch (RuntimeException e) {
            // Non-critical error (e.g., ID not found): report to user
            DistributionOfTheOutputStream.println(e.getMessage());

        } catch (Exception e) {
            // Unexpected error: log for diagnostics
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }

        return null;
    }

    /**
     * Returns a help string describing usage of the update command.
     *
     * @return a one-line summary for help menus
     */
    @Override
    public String getHelp() {
        return "update <id> : Updates the study group with the specified ID. "
                + "Supports interactive prompts or script-based input.";
    }
}
