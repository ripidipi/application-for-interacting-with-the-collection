package commands.interfaces;

import io.PrimitiveDataTransform;
import storage.RequestPair;
import storage.SavingAnEmergencyStop;
import collection.Collection;
import collection.StudyGroup;
import io.PrimitiveDataInput;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public interface Command {
    /**
     * Method for all commands. Perform main functional
     */
    RequestPair<?> execute(String arg, String inputMode);

    /**
     * Removes study groups from the collection that are greater or lower than the given one, based on the comparison logic.
     *
     * @param studyGroup The study group to compare with.
     * @param compare If true, removes all study groups greater than the specified one;
     *                         if false, removes all study groups lower than the specified one.
     */
    static void remove(StudyGroup studyGroup, BiPredicate<StudyGroup, StudyGroup> compare) {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        ArrayList<StudyGroup> toRemove = new ArrayList<>();
        for (StudyGroup sG : collection) {
            if (compare.test(sG, studyGroup)) {
                toRemove.add(sG);
            }
        }
        for (StudyGroup sG : toRemove) {
            collection.remove(sG);
        }
    }

    /**
     * Validates and parses the ID input.
     * This method checks if the provided ID exists in the collection of study groups. If the ID is valid and exists,
     * it returns the parsed ID. Otherwise, it throws a runtime exception.
     *
     * @param id The ID as a string to validate and parse.
     * @return The parsed ID as an Integer, if it exists in the collection.
     * @throws RuntimeException If the ID is invalid or does not exist in the collection.
     */
    static Integer validateId(String id) throws RuntimeException {
        Integer transformedId = PrimitiveDataTransform.transformToRequiredType("id", Integer.class, true,
                true, false, id, true, null, true);
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        boolean found = false;
        for (StudyGroup studyGroup: collection) {
            if (studyGroup.getId().equals(transformedId)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RuntimeException("No element to work with this id in =collection");
        }
        SavingAnEmergencyStop.addStringToFile(transformedId.toString());
        return transformedId;
    }

}
