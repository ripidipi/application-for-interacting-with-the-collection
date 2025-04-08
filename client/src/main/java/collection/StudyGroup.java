package collection;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Class representing a study group. This class implements the Comparable interface to allow comparison
 * of study groups based on their unique ID.
 */
public class StudyGroup implements Comparable<StudyGroup>, Serializable {

    public static Map<Integer, Boolean> IDs = new ConcurrentHashMap<>();
    private final Integer id;
    private final String name;
    private final Coordinates coordinates;
    private final LocalDateTime creationDate;
    private final Integer studentCount;
    private final FormOfEducation formOfEducation;
    private final Semester semester;
    private final Person groupAdmin;

    /**
     * Constructs a StudyGroup with a generated unique ID.
     *
     * @param name The name of the study group.
     * @param coordinates The coordinates of the study group.
     * @param studentCount The number of students in the group.
     * @param formOfEducation The form of education of the group.
     * @param semester The semester of the group.
     * @param groupAdmin The admin of the group.
     */
    public StudyGroup(String name, Coordinates coordinates,
                      Integer studentCount, FormOfEducation formOfEducation,
                      Semester semester, Person groupAdmin) {

        this.id = generateId();
        IDs.put(generateId(), true);
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.studentCount = studentCount;
        this.formOfEducation = formOfEducation;
        this.semester = semester;
        this.groupAdmin = groupAdmin;
    }

    /**
     * Constructs a StudyGroup with a provided unique ID.
     *
     * @param id The unique ID of the study group.
     * @param name The name of the study group.
     * @param coordinates The coordinates of the study group.
     * @param studentCount The number of students in the group.
     * @param formOfEducation The form of education of the group.
     * @param semester The semester of the group.
     * @param groupAdmin The admin of the group.
     */
    public StudyGroup(Integer id, String name, Coordinates coordinates, Integer studentCount,
                      FormOfEducation formOfEducation,
                      Semester semester, Person groupAdmin) {
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
    }

    /**
     * Compares this StudyGroup to another StudyGroup based on the ID.
     *
     * @param other The StudyGroup to compare this one to.
     * @return A negative integer, zero, or a positive integer as this ID is less than, equal to, or greater than the ID of the other StudyGroup.
     */
    @Override
    public int compareTo(StudyGroup other) {
        return this.id.compareTo(other.id);
    }

    /**
     * Compares this StudyGroup to another object for equality. Two StudyGroups are equal if their IDs are the same.
     *
     * @param o The object to compare to.
     * @return true if the IDs are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Returns a hash code value for this StudyGroup. The hash code is based on the ID, name, coordinates,
     * creation date, student count, form of education, semester, and group admin.
     *
     * @return The hash code value for this StudyGroup.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, studentCount,
                formOfEducation, semester, groupAdmin);
    }

    /**
     * Returns a string representation of this StudyGroup. The string includes the ID, name, coordinates,
     * creation date, student count, form of education, semester, and group admin.
     *
     * @return The string representation of this StudyGroup.
     */
    @Override
    public String toString() {
        return "StudyGroup {" +
                "id: " + id +
                "\tname: " + name +
                "\n" + coordinates +
                "\tcreation date: " + getCreationDateString() +
                "\tstudent count: " + studentCount +
                "\tform of education: " + formOfEducation +
                "\tsemester: " + semester +
                "\n" + groupAdmin + "}\n";
    }

    /**
     * Gets the ID of the StudyGroup.
     *
     * @return The ID of the StudyGroup.
     */
    public Integer getId() { return id; }

    public static Integer generateId() {
        int randomID;
        SecureRandom random = new SecureRandom();
        do {
            randomID = 100000 + random.nextInt(900000); // 6 digits
        } while (IDs.containsKey(randomID));
        return randomID;
    }

    /**
     * Gets the name of the StudyGroup.
     *
     * @return The name of the StudyGroup.
     */
    public String getName() { return name; }

    /**
     * Gets the coordinates of the StudyGroup.
     *
     * @return The coordinates of the StudyGroup.
     */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Gets the creation date of the StudyGroup.
     *
     * @return The creation date of the StudyGroup.
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * Gets the creation date of the StudyGroup as a formatted string.
     *
     * @return The creation date as a string in the format dd/MM/yyyy HH:mm:ss.
     */
    public String getCreationDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return creationDate.format(formatter);
    }

    /**
     * Gets the student count of the StudyGroup.
     *
     * @return The number of students in the StudyGroup.
     */
    public Integer getStudentCount() { return studentCount; }

    /**
     * Remove all ids from Id list
     */
    public static void clearIds() { IDs.clear(); }

    /**
     * Gets the form of education of the StudyGroup.
     *
     * @return The form of education of the StudyGroup.
     */
    public FormOfEducation getFormOfEducation() { return formOfEducation; }

    /**
     * Gets the semester of the StudyGroup.
     *
     * @return The semester of the StudyGroup.
     */
    public Semester getSemester() { return semester; }

    /**
     * Gets the group admin of the StudyGroup.
     *
     * @return The group admin of the StudyGroup.
     */
    public Person getGroupAdmin() { return groupAdmin; }

    /**
     * Formats a StudyGroup object into a CSV string representation.
     * This string is formatted to match the required CSV structure for study group details.
     *
     * @param studyGroup the study group to format
     * @return the formatted CSV string of the study group
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