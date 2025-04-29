package collection.fabrics;

import collection.*;
import commands.Exit;
import exceptions.*;
import io.*;
import storage.Logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static collection.StudyGroup.generateId;

public class StudyGroupFabric {

    /**
     * Returns an empty StudyGroup instance with default placeholder values.
     * Useful for initializing or resetting state.
     *
     * @return a StudyGroup with placeholder values for all fields.
     */
    public static StudyGroup getEmptyStudyGroup() {
        return new StudyGroup(-1, " ", new Coordinates(-1L, -1F), -1,
                FormOfEducation.FULL_TIME_EDUCATION, Semester.EIGHTH,
                new Person(" ", LocalDateTime.parse("11/11/1111 11:11:11",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), -1D, " "),
                Authentication.getInstance().getUsername());
    }

    /**
     * Validates that a StudyGroup object has all required fields non-null.
     *
     * @param studyGroup the StudyGroup to validate
     * @return true if the StudyGroup is valid; false otherwise
     */
    private static boolean isRightFill(StudyGroup studyGroup) {
        if (studyGroup == null) {
            return false;
        }
        return PersonFabric.isRightFill(studyGroup.getGroupAdmin())
                && CoordinatesFabric.isRightFill(studyGroup.getCoordinates())
                && studyGroup.getName() != null
                && studyGroup.getCreationDate() != null
                && studyGroup.getStudentCount() != null
                && studyGroup.getFormOfEducation() != null
                && studyGroup.getSemester() != null
                && studyGroup.getId() != null;
    }

    /**
     * Prompts user for StudyGroup details via console input or uses provided ID argument.
     *
     * @param Arg optional argument array where Arg[0] can specify the ID
     * @return a StudyGroup populated with user-input values
     * @throws RemoveOfTheNextSymbol if input contains invalid symbols
     */
    public static StudyGroup input(String... Arg) throws RemoveOfTheNextSymbol {
        Integer id = null;
        if (Arg.length > 0) {
            id = PrimitiveDataInput.inputFromFile("id", Arg[0], Integer.class);
        }
        DistributionOfTheOutputStream.println("Enter information about study group");
        String name = PrimitiveDataInput.input("name", String.class);
        Coordinates coordinates = CoordinatesFabric.input();
        Integer studentCount = PrimitiveDataInput.input("students count", Integer.class);
        FormOfEducation formOfEducation = EnumInput.inputFromConsole(FormOfEducation.class);
        Semester semester = EnumInput.inputFromConsole(Semester.class);
        Person groupAdmin = PersonFabric.input();
        if (id != null) {
            return new StudyGroup(id, name, coordinates, studentCount,
                    formOfEducation, semester, groupAdmin,
                    Authentication.getInstance().getUsername());
        }
        return new StudyGroup(name, coordinates, studentCount,
                formOfEducation, semester, groupAdmin,
                Authentication.getInstance().getUsername());
    }

    /**
     * Constructs a StudyGroup from CSV-file input fields.
     *
     * @param inputSplit array of string values from CSV
     * @param notAdded flag indicating whether the ID should be excluded from tracking
     * @return a StudyGroup built from file data, or null if parsing fails
     */
    public static StudyGroup inputFromFile(String[] inputSplit, boolean notAdded) {
        try {
            Integer id = PrimitiveDataInput.inputFromFile("id", inputSplit[0], Integer.class);
            String name = PrimitiveDataInput.inputFromFile("name", inputSplit[1], String.class);
            Coordinates coordinates = CoordinatesFabric.inputFromFile(inputSplit[2], inputSplit[3]);
            Integer studentCount = PrimitiveDataInput.inputFromFile("students count", inputSplit[4], Integer.class);
            FormOfEducation formOfEducation = EnumTransform.TransformToEnum(FormOfEducation.class, inputSplit[5]);
            Semester semester = EnumTransform.TransformToEnum(Semester.class, inputSplit[6]);
            Person groupAdmin = PersonFabric.inputFromFile(
                    inputSplit[7], inputSplit[8], inputSplit[9], inputSplit[10]
            );
            return rightInisilizeStudyGroup(
                    inputSplit, notAdded, id, name, coordinates,
                    studentCount, formOfEducation, semester, groupAdmin
            );
        } catch (CommandDataFromTheFileIsIncorrect e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Processes mixed-mode input for StudyGroup (console or file), including optional ID parsing.
     *
     * @param inputSplit array of input strings
     * @param notAdded flag to skip adding ID to tracking
     * @param isId flag indicating if inputSplit contains an ID
     * @return a StudyGroup built from mixed input, or null if an error occurs
     */
    public static StudyGroup inputMixed(String[] inputSplit, boolean notAdded, boolean isId) {
        try {
            int index = 1;
            Integer id;
            if (isId) {
                id = (index < inputSplit.length)
                        ? PrimitiveDataInput.inputFromFile("id", inputSplit[index++], Integer.class)
                        : PrimitiveDataInput.input("id", Integer.class);
            } else {
                id = generateId();
            }

            String name = (index < inputSplit.length)
                    ? PrimitiveDataInput.inputFromFile("name", inputSplit[index++], String.class)
                    : PrimitiveDataInput.input("name", String.class);

            Long coordX = (index < inputSplit.length)
                    ? (inputSplit[index].isBlank()
                    ? null
                    : PrimitiveDataInput.inputFromFile("x", inputSplit[index], Long.class)
            )
                    : PrimitiveDataInput.input("x coordinate", Long.class, false, false, false, null);
            index++;
            Float coordY = (index < inputSplit.length)
                    ? (inputSplit[index].isBlank()
                    ? null
                    : PrimitiveDataInput.inputFromFile("y", inputSplit[index], Float.class)
            )
                    : PrimitiveDataInput.input("y coordinate", Float.class, false, false, false, null);
            index++;
            Coordinates coordinates = new Coordinates(coordX, coordY);

            Integer studentCount = (index < inputSplit.length)
                    ? PrimitiveDataInput.inputFromFile("students count", inputSplit[index++], Integer.class)
                    : PrimitiveDataInput.input("students count", Integer.class);

            FormOfEducation formOfEducation = (index < inputSplit.length)
                    ? EnumTransform.TransformToEnum(FormOfEducation.class, inputSplit[index++])
                    : EnumInput.inputFromConsole(FormOfEducation.class);

            Semester semester = (index < inputSplit.length)
                    ? EnumTransform.TransformToEnum(Semester.class, inputSplit[index++])
                    : EnumInput.inputFromConsole(Semester.class);

            Person groupAdmin = PersonFabric.PersonMixedInput(inputSplit, index);

            return rightInisilizeStudyGroup(
                    inputSplit, notAdded, id, name, coordinates,
                    studentCount, formOfEducation, semester, groupAdmin
            );
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    /**
     * Common initialization logic for StudyGroup construction, ensuring unique IDs and validity.
     *
     * @param inputSplit original input values
     * @param notAdded flag indicating ID tracking behavior
     * @param id the parsed or generated ID
     * @param name the study group name
     * @param coordinates the study group coordinates
     * @param studentCount the number of students
     * @param formOfEducation the education form
     * @param semester the semester
     * @param groupAdmin the group administrator
     * @return a valid StudyGroup instance
     * @throws CommandDataFromTheFileIsIncorrect if resulting object is invalid
     */
    private static StudyGroup rightInisilizeStudyGroup(
            String[] inputSplit,
            boolean notAdded,
            Integer id,
            String name,
            Coordinates coordinates,
            Integer studentCount,
            FormOfEducation formOfEducation,
            Semester semester,
            Person groupAdmin
    ) {
        if (id != null && StudyGroup.IDs.containsKey(id) && !notAdded) {
            id = null;
        }
        StudyGroup studyGroup = new StudyGroup(
                id, name, coordinates, studentCount,
                formOfEducation, semester, groupAdmin,
                Authentication.getInstance().getUsername()
        );
        if (!isRightFill(studyGroup)) {
            throw new CommandDataFromTheFileIsIncorrect(String.join(
                    ",", inputSplit));
        }
        return studyGroup;
    }

    /**
     * Factory method to create a StudyGroup based on input mode.
     * Supports mixed ("M"), file ("F"), or console input.
     *
     * @param inputMode the mode identifier ("M", "F", or other)
     * @param input array of input values
     * @param notAdd flag to skip adding ID to tracking
     * @param isId flag indicating if ID should be parsed
     * @return a StudyGroup instance
     * @throws RemoveOfTheNextSymbol if invalid symbols are encountered
     * @throws IncorrectValue if resulting StudyGroup is invalid
     */
    public static StudyGroup getStudyGroupFrom(
            String inputMode,
            String[] input,
            boolean notAdd,
            boolean isId
    ) throws RemoveOfTheNextSymbol, IncorrectValue {
        StudyGroup studyGroup;
        if (inputMode.equalsIgnoreCase("M")) {
            studyGroup = StudyGroupFabric.inputMixed(input, notAdd, isId);
        } else if (inputMode.equalsIgnoreCase("F")) {
            studyGroup = StudyGroupFabric.inputFromFile(input, notAdd);
        } else {
            if (input.length >= 1 && !input[0].isEmpty()) {
                studyGroup = StudyGroupFabric.input(input[0]);
            } else {
                studyGroup = StudyGroupFabric.input();
            }
        }
        if (!isRightFill(studyGroup)) {
            throw new IncorrectValue(String.join(
                    ",", input));
        }
        return studyGroup;
    }

    /**
     * Parses arguments and delegates to appropriate getStudyGroupFrom method.
     * Handles creation commands and checks argument lengths.
     *
     * @param arg the argument string
     * @param inputMode the input mode ("C", "F", or others)
     * @param commandName name of the invoking command for error messages
     * @param isId flag indicating if ID parsing is required
     * @return a StudyGroup instance based on parsed arguments
     * @throws InsufficientNumberOfArguments if too few args for command
     * @throws RemoveOfTheNextSymbol if invalid symbols encountered
     * @throws IncorrectValue if resulting StudyGroup is invalid
     */
    public static StudyGroup parseStudyGroup(
            String arg,
            String inputMode,
            String commandName,
            boolean isId
    ) throws InsufficientNumberOfArguments, RemoveOfTheNextSymbol, IncorrectValue {
        StudyGroup studyGroup;
        if (inputMode.equalsIgnoreCase("C")) {
            Integer id;
            if (isId) {
                id = getIdInteger(arg);
            } else
                id = PrimitiveDataInput.input("id", Integer.class);
            studyGroup = StudyGroupFabric.getStudyGroupFrom(
                    "C", new String[]{id.toString()}, false, true
            );
        } else {
            String[] inputSplit = arg.split(",");
            if (inputMode.equalsIgnoreCase("F")
                    && StudyGroup.formatStudyGroupToCSV(
                    StudyGroupFabric.getEmptyStudyGroup()
            ).split(",").length != inputSplit.length) {
                throw new InsufficientNumberOfArguments(commandName);
            }
            studyGroup = StudyGroupFabric.getStudyGroupFrom(
                    inputMode, inputSplit, false, true
            );
        }
        return studyGroup;
    }

    /**
     * Parses a string into an integer ID, throwing an exception if null.
     *
     * @param arg the ID string to parse
     * @return the parsed integer ID
     * @throws IncorrectValue if the parsed ID is null
     */
    public static Integer getIdInteger(String arg) {
        Integer id = PrimitiveDataInput.inputFromFile("id", arg, Integer.class);
        if (id == null) {
            throw new IncorrectValue("id");
        }
        return id;
    }

}