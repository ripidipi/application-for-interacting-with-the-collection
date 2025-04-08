package collection;

import storage.Logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

/**
 * Singleton class managing a collection of study groups.
 * This class ensures that only one instance of the collection exists
 * and provides methods to manipulate, retrieve, and save the collection.
 */
public class Collection {

    private TreeSet<StudyGroup> collection = new TreeSet<>();
    private final LocalDateTime date;
    private static Collection instance;

    /**
     * Private constructor to initialize the collection and set the creation date.
     */
    private Collection() {
        date = LocalDateTime.now();
    }

    /**
     * Returns the singleton instance of the collection.
     * If the instance does not exist, it is created.
     *
     * @return the singleton instance of the collection
     */
    public static Collection getInstance() {
        if (instance == null) {
            instance = new Collection();
        }
        return instance;
    }

    /**
     * Returns information about the collection, including its size and creation date.
     *
     * @return a string with collection info (creation date and size)
     */
    public String getInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return "TreeSet " + date.format(formatter) + " " + collection.size();
    }

    /**
     * Clears all elements from the collection.
     */
    public void clearCollection() {
        collection.clear();
    }

    /**
     * Removes a specific study group from the collection.
     *
     * @param studyGroup the study group to remove from the collection
     */
    public void removeElement(StudyGroup studyGroup) {
        collection.remove(studyGroup);
    }

    /**
     * Returns the collection of study groups.
     *
     * @return the collection of study groups
     */
    public TreeSet<StudyGroup> getCollection() {
        return collection;
    }

    /**
     * Adds a new study group to the collection.
     *
     * @param studyGroup the study group to add
     */
    public void addElement(StudyGroup studyGroup) {
        collection.add(studyGroup);
    }

    /**
     * Saves the collection of study groups to a CSV file named "collection.csv".
     * If the file exists, it is overwritten.
     */
    public static void output() {
        TreeSet<StudyGroup> collection = Collection.getInstance().getCollection();
        String csvFile = "collection.csv";
        File file = new File(csvFile);

        if (file.exists()) {
            file.delete();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8)) {
            writer.write("ID,Name,CoordinateX,CoordinateY,StudentsCount," +
                    "FormOfEducation,Semester,AdminName,AdminBirthday,Height,PassportID\n");

            for (StudyGroup studyGroup : collection) {
                String writeRequest = StudyGroup.formatStudyGroupToCSV(studyGroup);
                writer.write(writeRequest);
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }
}
