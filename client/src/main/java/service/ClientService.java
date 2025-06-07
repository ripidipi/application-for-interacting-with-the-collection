package service;

import collection.StudyGroup;
import collection.Person;
import collection.Coordinates;
import collection.FormOfEducation;
import collection.Semester;
import commands.Commands;
import io.Authentication;
import io.PrimitiveDataTransform;
import io.EnumTransform;
import io.Server;
import storage.Logging;
import storage.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client-side logic for interacting with StudyGroup-related server commands.
 */
public class ClientService {

    /** Retrieves all StudyGroup instances, parsed. */
    public static List<StudyGroup> fetchAllGroups() throws Exception {
        return showAllGroupsParsed();
    }

    /** Gets raw lines from server and splits by StudyGroup entries. */
    public static List<String> showAllRawLines() throws Exception {
        try {
            Request<Void> req = new Request<>(Commands.SHOW, null);
            String raw = Server.interaction(req);
            List<String> result = new ArrayList<>();
            for (String part : raw.split("##")) {
                if (part.startsWith("C#")) {
                    String line = part.substring(2);
                    if (line.endsWith("\n")) line = line.substring(0, line.length() - 1);
                    result.add(line);
                }
            }
            return result;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw e;
        }
    }

    /** Parses three lines into a StudyGroup object using regex and transforms. */
    public static StudyGroup parseThreeLinesToGroup(String line1, String line2, String line3) {
        try {
            Matcher m1 = Pattern.compile("id:\\s*(\\d+)\\s+name:\\s*(\\S+)").matcher(line1);
            if (!m1.find()) throw new IllegalArgumentException("Invalid line1: " + line1);
            Integer id = Integer.parseInt(m1.group(1));
            String name = m1.group(2);

            Matcher mc = Pattern.compile(
                    "Coordinates \\{\\s*(?:X:\\s*(\\d+))?\\s*(?:Y:\\s*([\\d.]+))?\\s*\\}")
                .matcher(line2);
            Long x = null;
            Float y = null;
            if (mc.find()) {
                if (mc.group(1) != null) x = Long.parseLong(mc.group(1));
                if (mc.group(2) != null) y = Float.parseFloat(mc.group(2));
            }

            String after = line2.substring(line2.indexOf('}') + 1);
            Matcher m2 = Pattern.compile(
                    "creation date:\\s*([\\d/\\s:]+)\\s+student count:\\s*(\\d+)\\s+form of education:\\s*(\\S+)\\s+semester:\\s*(\\S+)"
            ).matcher(after);
            if (!m2.find()) throw new IllegalArgumentException("Invalid line2: " + line2);
            Integer studentCount = Integer.parseInt(m2.group(2));
            FormOfEducation form = EnumTransform.TransformToEnum(FormOfEducation.class, m2.group(3));
            Semester sem = EnumTransform.TransformToEnum(Semester.class, m2.group(4));

            Matcher ma = Pattern.compile(
                    "Group admin \\{\\s*name:\\s*(.*?)\\s+" +
            "birthday:\\s*(\\d{2}/\\d{2}/\\d{4})" +
                    "(?:\\s+height:\\s*([\\d.]*))?" +
                            "\\s+passportID:\\s*(\\S+)\\s*}")
                .matcher(line3);

            if (!ma.find()) throw new IllegalArgumentException("Invalid line3: " + line3);
            String adminName = ma.group(1).trim();
            LocalDateTime adminBirthday = PrimitiveDataTransform.transformToRequiredType(
                    "admin birthday", LocalDateTime.class,
                    true, false, false,
                    ma.group(2), false,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"), false
            );
            String htxt = ma.group(3);
            Double height = null;
            if (htxt != null && !htxt.isEmpty()) {
                height = PrimitiveDataTransform.transformToRequiredType(
                        "admin height", Double.class,
                        false, true, false,
                        htxt, false, null, false
                );
            }
            String passport = ma.group(4).substring(0, ma.group(4).length() - 1);
            Person admin = new Person(adminName, adminBirthday, height, passport);

            return new StudyGroup(
                    id, name, new Coordinates(x, y), studentCount,
                    form, sem, admin, Authentication.getInstance().getUsername()
            );
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return null;
        }
    }

    /** Parses raw lines into StudyGroup list. */
    public static List<StudyGroup> showAllGroupsParsed() throws Exception {
        List<String> lines = showAllRawLines();
        List<StudyGroup> list = new ArrayList<>();
        for (int i = 0; i  < lines.size(); i++) {
            String line = lines.get(i);
            String[] split = line.split("\n");
            StudyGroup g = parseThreeLinesToGroup(split[0], split[1], split[2]);
            if (g != null) list.add(g);
        }
        return list;
    }

    public static String addGroup(StudyGroup group) throws Exception {
        Request<StudyGroup> req = new Request<>(Commands.ADD, group);
        return Server.interaction(req);
    }

    public static String updateGroup(int id, StudyGroup group) throws Exception {
        Request<StudyGroup> req = new Request<>(Commands.UPDATE, group);
        return Server.interaction(req);
    }

    public static String removeById(int id) throws Exception {
        Request<Integer> req = new Request<>(Commands.REMOVE_BY_ID, id);
        return Server.interaction(req);
    }

    public static String clearAll() throws Exception {
        Request<Void> req = new Request<>(Commands.CLEAR, null);
        return Server.interaction(req);
    }

    public static String countByAdmin(Person admin) throws Exception {
        Request<Person> req = new Request<>(Commands.COUNT_BY_GROUP_ADMIN, admin);
        return Server.interaction(req);
    }
}
