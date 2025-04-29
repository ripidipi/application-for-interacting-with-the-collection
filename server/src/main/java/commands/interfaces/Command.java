package commands.interfaces;

import collection.Collection;
import collection.StudyGroup;
import io.DistributionOfTheOutputStream;
import storage.Authentication;

import java.util.TreeSet;
import java.util.function.BiPredicate;

/**
 * Defines a generic command with an execution contract.
 * <p>
 * Each command implementing this interface performs its specific logic
 * when {@link #execute(Object, boolean, Authentication)} is invoked.
 * </p>
 *
 * @param <T> the type of input accepted by the command
 */
public interface Command<T> {

    /**
     * Executes the command's primary functionality.
     * <p>
     * Implementing classes should perform all necessary actions
     * based on the provided input. The {@code muteMode} flag can be used
     * to suppress output or modify behavior, and {@code auth} provides
     * user authentication context.
     * </p>
     *
     * @param input the command-specific input data
     * @param muteMode if {@code true}, suppresses output or notifications; if {@code false}, produces normal output
     * @param auth the authentication context for access control and user verification
     */
    void execute(T input, boolean muteMode, Authentication auth);

    /**
     * Removes study groups from the singleton collection based on a comparison with the provided group.
     * <p>
     * If {@code compare.test(existingGroup, referenceGroup)} returns {@code true},
     * the existing group is removed. For example, to remove groups greater than
     * the reference, use a predicate that tests if the existing group is greater.
     * </p>
     *
     * @param studyGroup the reference study group for comparison
     * @param compare a predicate that accepts two groups and returns {@code true}
     *                if the existing group should be removed relative to the reference
     */
    static void remove(StudyGroup studyGroup, BiPredicate<StudyGroup, StudyGroup> compare) {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        collection.removeIf(existingGroup -> compare.test(existingGroup, studyGroup));
    }

}
