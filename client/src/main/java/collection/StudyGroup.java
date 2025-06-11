package collection;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a study group with unique ID, name, coordinates, creation date, student count,
 * form of education, semester, group administrator, and owner. Implements Comparable for ID-based ordering.
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable {

    /**
     * Map tracking allocated IDs to ensure uniqueness.
     */
    public static Map<Integer, Boolean> IDs = new ConcurrentHashMap<>();

    private final Integer id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final Integer studentCount;
    private final FormOfEducation formOfEducation;
    private final Semester semester;
    private final Person groupAdmin;
    private final String owner;

    /**
     * Constructs a new StudyGroup with an automatically generated unique ID.
     * The creation date is set to the current timestamp.
     *
     * @param name the name of the study group
     * @param coordinates the location coordinates for the group
     * @param studentCount the number of students in the group
     * @param formOfEducation the form of education for the group
     * @param semester the semester associated with the group
     * @param groupAdmin the administrator (Person) of the group
     * @param owner the username of the creator/owner
     */
    public StudyGroup(String name, Coordinates coordinates,
                      Integer studentCount, FormOfEducation formOfEducation,
                      Semester semester, Person groupAdmin, String owner) {
        this.id = generateId();
        IDs.put(generateId(), true);
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.studentCount = studentCount;
        this.formOfEducation = formOfEducation;
        this.semester = semester;
        this.groupAdmin = groupAdmin;
        this.owner = owner;
    }

    /**
     * Constructs a StudyGroup with the specified ID and current creation timestamp.
     * If the ID is non-null, it is tracked in the IDs map.
     *
     * @param id the unique identifier for the study group
     * @param name the name of the study group
     * @param coordinates the location coordinates
     * @param studentCount the number of students
     * @param formOfEducation the form of education
     * @param semester the semester
     * @param groupAdmin the administrator (Person) of the group
     * @param owner the username of the creator/owner
     */
    public StudyGroup(Integer id, String name, Coordinates coordinates, Integer studentCount,
                      FormOfEducation formOfEducation,
                      Semester semester, Person groupAdmin, String owner) {
        this.id = id;
        if (id != null)
            IDs.put(id, true);
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.studentCount = studentCount;
        this.formOfEducation = formOfEducation;
        this.semester = semester;
        this.groupAdmin = groupAdmin;
        this.owner = owner;
    }

    /**
     * Compares this StudyGroup to another based on their IDs.
     *
     * @param other the StudyGroup to compare against
     * @return negative if this ID is less, zero if equal, positive if greater
     */
    @Override
    public int compareTo(StudyGroup other) {
        return this.id.compareTo(other.studentCount);
    }

    /**
     * Checks equality based on ID value.
     *
     * @param o the object to compare
     * @return true if IDs are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Generates a hash code based on all major fields.
     *
     * @return hash code value for this StudyGroup
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, studentCount,
                formOfEducation, semester, groupAdmin);
    }

    /**
     * Returns a detailed string representation of the StudyGroup.
     * Includes all major fields in a readable format.
     *
     * @return formatted string with group details
     */
    @Override
    public String toString() {
        return "StudyGroup {" +
                "id: " + id +
                "\tname: " + name +
                "\n" + coordinates.toString() +
                "\tcreation date: " + getCreationDateString() +
                "\tstudent count: " + studentCount +
                "\tform of education: " + formOfEducation +
                "\tsemester: " + semester +
                "\n" + groupAdmin.toString() + "}\n";
    }

    /**
     * Retrieves the unique ID for this StudyGroup.
     *
     * @return the group's ID
     */
    public Integer getId() { return id; }

    /**
     * Generates a new unique 6-digit ID not already in use.
     * Tracks the new ID in the IDs map.
     *
     * @return a unique integer ID
     */
    public static Integer generateId() {
        int randomID;
        SecureRandom random = new SecureRandom();
        do {
            randomID = 100000 + random.nextInt(900000); // 6 digits
        } while (IDs.containsKey(randomID));
        return randomID;
    }

    /**
     * Retrieves the name of this StudyGroup.
     *
     * @return the group name
     */
    public String getName() { return name; }

    /**
     * Retrieves the owner (username) of this StudyGroup.
     *
     * @return the owner username
     */
    public String getOwner() { return owner; }

    /**
     * Retrieves the coordinates of this StudyGroup.
     *
     * @return the group's Coordinates
     */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Retrieves the creation timestamp of this StudyGroup.
     *
     * @return the creation LocalDateTime
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * Returns a formatted creation date string in dd/MM/yyyy HH:mm:ss pattern.
     *
     * @return formatted creation date
     */
    public String getCreationDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return creationDate.format(formatter);
    }

    /**
     * Retrieves the student count for this StudyGroup.
     *
     * @return number of students in the group
     */
    public Integer getStudentCount() { return studentCount; }

    /**
     * Clears all tracked IDs from the IDs map.
     */
    public static void clearIds() { IDs.clear(); }

    /**
     * Retrieves the education form of this StudyGroup.
     *
     * @return the group's FormOfEducation
     */
    public FormOfEducation getFormOfEducation() { return formOfEducation; }

    /**
     * Retrieves the semester associated with this StudyGroup.
     *
     * @return the group's Semester
     */
    public Semester getSemester() { return semester; }

    /**
     * Retrieves the administrator of this StudyGroup.
     *
     * @return the group's Person admin
     */
    public Person getGroupAdmin() { return groupAdmin; }

    /**
     * Formats the given StudyGroup into a CSV-compatible string.
     * Fields are comma-separated and follow the study group CSV schema.
     *
     * @param studyGroup the StudyGroup to format
     * @return CSV string representation including newline
     */
    public static String formatStudyGroupToCSV(StudyGroup studyGroup) {
        return studyGroup.getId().toString() + "," + studyGroup.getName() + "," +
                studyGroup.getCoordinates().xToString() + "," +
                studyGroup.getCoordinates().yToString() +  "," +
                studyGroup.getStudentCount().toString() + "," + studyGroup.getFormOfEducation().toString() + "," +
                studyGroup.getSemester().toString() + "," + studyGroup.getGroupAdmin().name() + "," +
                studyGroup.getGroupAdmin().getBirthdayString() + "," + studyGroup.getGroupAdmin().heightToString() + "," +
                studyGroup.getGroupAdmin().passportID() + '\n';
    }
}